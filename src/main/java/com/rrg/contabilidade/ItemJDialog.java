package com.rrg.contabilidade;

import com.rrg.contabilidade.model.LancamentoItem;
import com.rrg.contabilidade.model.TipoLancamento;
import com.rrg.contabilidade.util.AbreBancoEmpresa;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;

/**
 * Dialog que permite digitar/editar itens do lançamento.
 * Não grava nada no DB — apenas consulta contas para autocomplete.
 * Ao confirmar, retorna lista de LancamentoItem apenas se débitos == créditos.
 */
public class ItemJDialog extends JDialog {

    private final DefaultTableModel model;
    private final JTable table;
    private final JButton btConfirmar;
    private final JButton btFechar;
    private final JLabel lbSoma;

    // popup de sugestões para conta
    private final JPopupMenu popupSugestoes = new JPopupMenu();
    private final JList<String> listSugestoes = new JList<>();
    private final JScrollPane scrollSugestoes = new JScrollPane(listSugestoes);

    // returned result
    private List<com.rrg.contabilidade.model.LancamentoItem> resultado = null;

    public ItemJDialog(Frame parent) {
        super(parent, "Digitar Itens do Lançamento", true);

        model = new DefaultTableModel(new Object[]{"Conta", "Tipo", "Valor (cents)", "Valor"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                // Permite edição nas colunas Conta, Tipo, Valor (col 0,1,2). Col 3 é apenas exibição formatada.
                return col >= 0 && col <= 2;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) return String.class;
                return String.class;
            }
        };

        table = new JTable(model);
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Remove a coluna 3 (valor formatado) do modelo de edição, mas mantemos para exibição
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);

        // Editor para 'Tipo' (coluna 1)
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"D", "C"});
        DefaultCellEditor tipoEditor = new DefaultCellEditor(cbTipo);
        table.getColumnModel().getColumn(1).setCellEditor(tipoEditor);

        // Editor para 'Valor' (coluna 2): aceita apenas dígitos até 15
        JTextField tfValorEditor = new JTextField();
        ((AbstractDocument) tfValorEditor.getDocument()).setDocumentFilter(new DigitsLimitFilter(15));
        DefaultCellEditor valorEditor = new DefaultCellEditor(tfValorEditor) {
            @Override
            public boolean stopCellEditing() {
                String text = (String) getCellEditorValue();
                if (text == null || text.isBlank()) {
                    // empty allowed (linha pode continuar)
                    return super.stopCellEditing();
                }
                if (!text.matches("\\d{1,15}")) {
                    JOptionPane.showMessageDialog(ItemJDialog.this, "Valor deve conter apenas dígitos (até 15).", "Erro", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                return super.stopCellEditing();
            }
        };
        table.getColumnModel().getColumn(2).setCellEditor(valorEditor);

        // Autocomplete: intercepta edição da coluna 0 (Conta)
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // quando clicar na célula, inicia edição
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 0) {
                    table.editCellAt(row, col);
                    Component editor = table.getEditorComponent();
                    if (editor instanceof JTextField) {
                        ((JTextField) editor).requestFocusInWindow();
                        ((JTextField) editor).selectAll();
                    }
                }
            }
        });

        // Quando começar a editar conta, adiciona document listener para buscar sugestões
        table.getDefaultEditor(String.class).addCellEditorListener(new CellEditorListenerAdapter() {
            @Override
            public void editingStopped(ChangeEvent e) {
                // atualiza coluna 3 com valor formatado
                updateFormattedValues();
            }
        });

        // Escuta teclas para abrir popup autocomplete ao editar coluna 0
        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();
                if (row < 0 || col < 0) return;
                if (col == 0) {
                    // tenta mostrar sugestões
                    Component c = table.getEditorComponent();
                    if (c instanceof JTextField) {
                        JTextField tf = (JTextField) c;
                        String texto = tf.getText();
                        SwingUtilities.invokeLater(() -> mostrarSugestoes(tf, texto));
                    } else {
                        // inicia edição se não estiver editando
                        table.editCellAt(row, col);
                    }
                } else if (col == 1) {
                    // quando digitar d/c no tipo, auto-move para valor
                    if (e.getKeyChar()=='d' || e.getKeyChar()=='D' || e.getKeyChar()=='c' || e.getKeyChar()=='C') {
                        table.editCellAt(row, 2);
                        table.requestFocusInWindow();
                        Component ce = table.getEditorComponent();
                        if (ce instanceof JTextField) ((JTextField) ce).requestFocusInWindow();
                    }
                } else if (col == 2) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        // move para nova linha (ou cria nova)
                        int nextRow = table.getSelectedRow();
                        if (nextRow == table.getRowCount()-1) {
                            model.addRow(new Object[]{"", "D", "", ""});
                        }
                        table.changeSelection(nextRow+1, 0, false, false);
                        table.editCellAt(nextRow+1, 0);
                    }
                }
            }
        });

        // configura popup sugestões
        listSugestoes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSugestoes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    selecionarSugestao();
                }
            }
        });
        scrollSugestoes.setPreferredSize(new Dimension(300, 160));
        popupSugestoes.setFocusable(false);
        popupSugestoes.add(scrollSugestoes);

        // painel principal
        JPanel painel = new JPanel(new BorderLayout(6, 6));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painel.add(new JScrollPane(table), BorderLayout.CENTER);

        // soma / botões
        lbSoma = new JLabel("Débito: 0.00    Crédito: 0.00");
        btConfirmar = new JButton("Confirmar (só habilita se balanço)");
        btFechar = new JButton("Fechar (cancela)");

        JPanel south = new JPanel(new BorderLayout());
        JPanel southLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        southLeft.add(lbSoma);
        south.add(southLeft, BorderLayout.WEST);

        JPanel southRight = new JPanel(new GridLayout(1,2,6,6));
        southRight.add(btConfirmar);
        southRight.add(btFechar);
        south.add(southRight, BorderLayout.EAST);

        painel.add(south, BorderLayout.SOUTH);

        getContentPane().add(painel);
        setSize(700, 400);
        setLocationRelativeTo(parent);

        // inicialmente com uma linha vazia
        model.addRow(new Object[]{"", "D", "", ""});
        updateFormattedValues();
        atualizarSomas();

        // ações botões
        btConfirmar.addActionListener(e -> {
            if (validarBalanceamento()) {
                resultado = montarListaItens();
                setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Lançamento não balanceado: débitos e créditos diferem.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btFechar.addActionListener(e -> {
            resultado = null;
            setVisible(false);
        });

        // fechar com ESC
        getRootPane().registerKeyboardAction(e -> {
            resultado = null;
            setVisible(false);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        // update soma quando tabela muda
        model.addTableModelListener(e -> {
            updateFormattedValues();
            atualizarSomas();
            btConfirmar.setEnabled(validarBalanceamento());
        });

        // clique duplo na sugestão faz seleção
        listSugestoes.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER) selecionarSugestao();
            }
        });
    }

    private void selecionarSugestao() {
        String sel = listSugestoes.getSelectedValue();
        if (sel == null) return;
        // define na célula atualmente editada
        int row = table.getEditingRow();
        int col = table.getEditingColumn();
        if (row < 0 || col < 0) {
            // encontra a célula selecionada
            row = table.getSelectedRow();
            col = 0;
        }
        if (row >= 0) {
            table.getModel().setValueAt(sel, row, 0);
            popupSugestoes.setVisible(false);
            table.editCellAt(row, 1);
            Component ce = table.getEditorComponent();
            if (ce instanceof JComboBox) ((JComboBox<?>) ce).requestFocusInWindow();
        }
    }

    private void mostrarSugestoes(JTextField tf, String texto) {
        if (texto == null) texto = "";
        final String textoFinal = texto.trim();
        texto = texto.trim();
        if (textoFinal.isEmpty()) {
            popupSugestoes.setVisible(false);
            return;
        }
        // busca no banco (até 10 resultados)
        SwingUtilities.invokeLater(() -> {
            List<String> resultados = buscarContas(textoFinal);
            if (resultados.isEmpty()) {
                popupSugestoes.setVisible(false);
                return;
            }
            listSugestoes.setListData(resultados.toArray(new String[0]));
            listSugestoes.setSelectedIndex(0);
            try {
                Rectangle cellRect = table.getCellRect(table.getEditingRow(), table.getEditingColumn(), true);
                Point locationOnScreen = table.getLocationOnScreen();
                int x = locationOnScreen.x + cellRect.x;
                int y = locationOnScreen.y + cellRect.y + cellRect.height;
                popupSugestoes.show(this, cellRect.x + table.getLocation().x, cellRect.y + cellRect.height + table.getLocation().y);
                // adjust position relative to editor component
                popupSugestoes.setInvoker(this);
            } catch (IllegalComponentStateException ex) {
                // fallback: mostra no centro
                popupSugestoes.show(this, getWidth()/2 - 150, getHeight()/2 - 80);
            }
        });
    }

    /**
     * Busca contas no banco da empresa logada (id OR descricao) usando LIKE %texto%.
     * Retorna até 10 itens no formato "id - descricao".
     */
    private List<String> buscarContas(String texto) {
        List<String> saida = new ArrayList<>();
        String sql = "SELECT id, descricao FROM contas WHERE id LIKE ? OR descricao LIKE ? LIMIT 10";
        try (Connection conn = AbreBancoEmpresa.obterConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String param = "%" + texto + "%";
            ps.setString(1, param);
            ps.setString(2, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("id");
                    String desc = rs.getString("descricao");
                    saida.add(id + " - " + desc);
                }
            }
        } catch (Exception ex) {
            // erro de consulta: mostra nada (não fatal aqui)
            ex.printStackTrace();
        }
        return saida;
    }

    /**
     * Converte o conteúdo da coluna valor (texto sem ponto) em BigDecimal com 2 casas.
     * Atualiza coluna 3 (formatação) para exibição.
     */
    private void updateFormattedValues() {
        for (int r = 0; r < model.getRowCount(); r++) {
            Object raw = model.getValueAt(r, 2);
            String s = raw == null ? "" : raw.toString().trim();
            if (s.isBlank()) {
                model.setValueAt("", r, 3);
            } else {
                // converte string de dígitos para BigDecimal com 2 decimais
                try {
                    BigDecimal bd = new BigDecimal(new BigInteger(s), 2);
                    model.setValueAt(bd.setScale(2).toPlainString(), r, 3);
                } catch (Exception ex) {
                    model.setValueAt("ERR", r, 3);
                }
            }
        }
    }

    private void atualizarSomas() {
        BigDecimal somaDebitos = BigDecimal.ZERO;
        BigDecimal somaCreditos = BigDecimal.ZERO;
        for (int r = 0; r < model.getRowCount(); r++) {
            String tipo = ((model.getValueAt(r,1) == null) ? "" : model.getValueAt(r,1).toString()).trim().toUpperCase();
            String raw = ((model.getValueAt(r,2) == null) ? "" : model.getValueAt(r,2).toString()).trim();
            if (raw.isBlank()) continue;
            try {
                BigDecimal val = new BigDecimal(new BigInteger(raw), 2);
                if ("D".equals(tipo)) somaDebitos = somaDebitos.add(val);
                else if ("C".equals(tipo)) somaCreditos = somaCreditos.add(val);
            } catch (Exception ex) {
                // ignora linhas inválidas
            }
        }
        lbSoma.setText("Débito: " + somaDebitos.setScale(2).toPlainString() + "    Crédito: " + somaCreditos.setScale(2).toPlainString());
    }

    private boolean validarBalanceamento() {
        BigDecimal somaDebitos = BigDecimal.ZERO;
        BigDecimal somaCreditos = BigDecimal.ZERO;
        for (int r = 0; r < model.getRowCount(); r++) {
            String tipo = ((model.getValueAt(r,1) == null) ? "" : model.getValueAt(r,1).toString()).trim().toUpperCase();
            String raw = ((model.getValueAt(r,2) == null) ? "" : model.getValueAt(r,2).toString()).trim();
            if (raw.isBlank()) continue;
            try {
                BigDecimal val = new BigDecimal(new BigInteger(raw), 2);
                if ("D".equals(tipo)) somaDebitos = somaDebitos.add(val);
                else if ("C".equals(tipo)) somaCreditos = somaCreditos.add(val);
            } catch (Exception ex) {
                return false;
            }
        }
        return somaDebitos.compareTo(somaCreditos) == 0 && somaDebitos.compareTo(BigDecimal.ZERO) > 0;
    }

    private List<com.rrg.contabilidade.model.LancamentoItem> montarListaItens() {
        List<com.rrg.contabilidade.model.LancamentoItem> lista = new ArrayList<>();
        for (int r = 0; r < model.getRowCount(); r++) {
            String contaText = ((model.getValueAt(r,0) == null) ? "" : model.getValueAt(r,0).toString()).trim();
            if (contaText.isBlank()) continue;
            String tipo = ((model.getValueAt(r,1) == null) ? "" : model.getValueAt(r,1).toString()).trim().toUpperCase();
            String raw = ((model.getValueAt(r,2) == null) ? "" : model.getValueAt(r,2).toString()).trim();
            if (raw.isBlank()) continue;
            String idConta = contaText.split(" - ")[0].trim();
            BigDecimal valor = new BigDecimal(new BigInteger(raw), 2);
            com.rrg.contabilidade.model.LancamentoItem item = new com.rrg.contabilidade.model.LancamentoItem();
            item.setIdConta(idConta);
            item.setTipo(TipoLancamento.valueOf(tipo));
            item.setValor(valor);
            // idPlano e idLancamento serão definidos no controller/dao se necessário; deixamos idPlano = null
            lista.add(item);
        }
        return lista;
    }

    /**
     * Abre o diálogo e retorna a lista de itens (ou null se cancelado).
     */
    public List<com.rrg.contabilidade.model.LancamentoItem> mostrarDialog() {
        setVisible(true);
        return resultado;
    }

    // DocumentFilter que permite apenas dígitos até um limite
    private static class DigitsLimitFilter extends DocumentFilter {
        private final int max;

        public DigitsLimitFilter(int max) { this.max = max; }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            StringBuilder sb = new StringBuilder();
            for (char c : string.toCharArray()) if (Character.isDigit(c)) sb.append(c);
            int newLen = fb.getDocument().getLength() + sb.length();
            if (newLen <= max) super.insertString(fb, offset, sb.toString(), attr);
            // else ignore
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;
            StringBuilder sb = new StringBuilder();
            for (char c : text.toCharArray()) if (Character.isDigit(c)) sb.append(c);
            int newLen = fb.getDocument().getLength() - length + sb.length();
            if (newLen <= max) super.replace(fb, offset, length, sb.toString(), attrs);
            // else ignore
        }
    }

    // small adapter for CellEditorListener to avoid implementing both methods everywhere
    private static abstract class CellEditorListenerAdapter implements javax.swing.event.CellEditorListener {
        @Override public void editingCanceled(ChangeEvent e) {}
        @Override public void editingStopped(ChangeEvent e) {}
    }
}
