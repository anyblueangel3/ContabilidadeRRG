package com.rrg.contabilidade;

import com.rrg.contabilidade.model.LancamentoItem;
import com.rrg.contabilidade.model.TipoLancamento;
import com.rrg.contabilidade.util.AbreBancoEmpresa;

import javax.swing.*;
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

public class ItemJDialog extends JDialog {

    private final DefaultTableModel model;
    private final JTable table;
    private final JButton btConfirmar;
    private final JButton btFechar;
    private final JLabel lbSoma;

    private final JPopupMenu popupSugestoes = new JPopupMenu();
    private final JList<String> listSugestoes = new JList<>();
    private final JScrollPane scrollSugestoes = new JScrollPane(listSugestoes);

    private List<LancamentoItem> resultado = null;
    private boolean updating = false; // para evitar loops

    public ItemJDialog(Frame parent) {
        super(parent, "Digitar Itens do Lançamento", true);

        model = new DefaultTableModel(new Object[]{"Conta", "Tipo", "Valor (cents)", "Valor"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col >= 0 && col <= 2;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        table = new JTable(model);
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);

        // Tipo editor
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"D", "C"});
        table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(cbTipo));

        // Valor editor
        JTextField tfValorEditor = new JTextField();
        ((AbstractDocument) tfValorEditor.getDocument()).setDocumentFilter(new DigitsLimitFilter(15));
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(tfValorEditor) {
            @Override
            public boolean stopCellEditing() {
                String text = (String) getCellEditorValue();
                if (text != null && !text.isBlank() && !text.matches("\\d{1,15}")) {
                    JOptionPane.showMessageDialog(ItemJDialog.this,
                            "Valor deve conter apenas dígitos (até 15).", "Erro", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                return super.stopCellEditing();
            }
        });

        // Mouse listener para foco em edição da conta
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 0) {
                    table.editCellAt(row, col);
                    Component editor = table.getEditorComponent();
                    if (editor instanceof JTextField tf) {
                        tf.requestFocusInWindow();
                        tf.selectAll();
                    }
                }
            }
        });

        // KeyListener para autocomplete e movimentação entre colunas
        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();
                if (row < 0 || col < 0) return;

                if (col == 0) {
                    Component c = table.getEditorComponent();
                    if (c instanceof JTextField tf) {
                        SwingUtilities.invokeLater(() -> mostrarSugestoes(tf, tf.getText()));
                    }
                } else if (col == 1) {
                    if (e.getKeyChar() == 'd' || e.getKeyChar() == 'D' ||
                        e.getKeyChar() == 'c' || e.getKeyChar() == 'C') {
                        table.editCellAt(row, 2);
                        Component ce = table.getEditorComponent();
                        if (ce instanceof JTextField tf) tf.requestFocusInWindow();
                    }
                } else if (col == 2 && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    int nextRow = table.getSelectedRow();
                    if (nextRow == table.getRowCount() - 1) {
                        model.addRow(new Object[]{"", "D", "", ""});
                    }
                    table.changeSelection(nextRow + 1, 0, false, false);
                    table.editCellAt(nextRow + 1, 0);
                }
            }
        });

        // Popup sugestões
        listSugestoes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSugestoes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) selecionarSugestao();
            }
        });
        scrollSugestoes.setPreferredSize(new Dimension(300, 160));
        popupSugestoes.setFocusable(false);
        popupSugestoes.add(scrollSugestoes);
        listSugestoes.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) selecionarSugestao();
            }
        });

        JPanel painel = new JPanel(new BorderLayout(6, 6));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painel.add(new JScrollPane(table), BorderLayout.CENTER);

        lbSoma = new JLabel("Débito: 0.00    Crédito: 0.00");
        btConfirmar = new JButton("Confirmar (só habilita se balanço)");
        btFechar = new JButton("Fechar (cancela)");

        JPanel south = new JPanel(new BorderLayout());
        JPanel southLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        southLeft.add(lbSoma);
        south.add(southLeft, BorderLayout.WEST);

        JPanel southRight = new JPanel(new GridLayout(1, 2, 6, 6));
        southRight.add(btConfirmar);
        southRight.add(btFechar);
        south.add(southRight, BorderLayout.EAST);

        painel.add(south, BorderLayout.SOUTH);

        getContentPane().add(painel);
        setSize(700, 400);
        setLocationRelativeTo(parent);

        // Primeira linha e foco inicial
        model.addRow(new Object[]{"", "D", "", ""});
        table.editCellAt(0, 0);
        table.requestFocusInWindow();

        // Ações botões
        btConfirmar.addActionListener(e -> {
            if (validarBalanceamento()) {
                resultado = montarListaItens();
                setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Lançamento não balanceado: débitos e créditos diferem.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btFechar.addActionListener(e -> {
            resultado = null;
            setVisible(false);
        });

        getRootPane().registerKeyboardAction(e -> {
            resultado = null;
            setVisible(false);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Atualiza soma e formatação
        model.addTableModelListener(e -> {
            if (updating) return;
            updating = true;
            try {
                updateFormattedValues();
                atualizarSomas();
                btConfirmar.setEnabled(validarBalanceamento());
            } finally {
                updating = false;
            }
        });
    }

    private void selecionarSugestao() {
        String sel = listSugestoes.getSelectedValue();
        if (sel == null) return;
        int row = table.getEditingRow();
        if (row < 0) row = table.getSelectedRow();
        if (row >= 0) {
            table.setValueAt(sel, row, 0);
            popupSugestoes.setVisible(false);
            table.editCellAt(row, 1);
            Component ce = table.getEditorComponent();
            if (ce instanceof JComboBox<?> cb) cb.requestFocusInWindow();
        }
    }

    private void mostrarSugestoes(JTextField tf, String texto) {
        if (texto == null) texto = "";
        final String t = texto.trim();
        if (t.isEmpty()) {
            popupSugestoes.setVisible(false);
            return;
        }
        SwingUtilities.invokeLater(() -> {
            List<String> resultados = buscarContas(t);
            if (resultados.isEmpty()) {
                popupSugestoes.setVisible(false);
                return;
            }
            listSugestoes.setListData(resultados.toArray(new String[0]));
            listSugestoes.setSelectedIndex(0);
            try {
                Rectangle cellRect = table.getCellRect(table.getEditingRow(),
                        table.getEditingColumn(), true);
                popupSugestoes.show(this,
                        cellRect.x + table.getLocation().x,
                        cellRect.y + cellRect.height + table.getLocation().y);
                popupSugestoes.setInvoker(this);
            } catch (IllegalComponentStateException ex) {
                popupSugestoes.show(this, getWidth() / 2 - 150, getHeight() / 2 - 80);
            }
        });
    }

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
                    saida.add(rs.getString("id") + " - " + rs.getString("descricao"));
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao buscar contas: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        return saida;
    }

    private void updateFormattedValues() {
        for (int r = 0; r < model.getRowCount(); r++) {
            Object raw = model.getValueAt(r, 2);
            String s = raw == null ? "" : raw.toString().trim();
            if (s.isBlank()) {
                model.setValueAt("", r, 3);
            } else {
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
            String tipo = model.getValueAt(r, 1) == null ? "" : model.getValueAt(r, 1).toString().trim().toUpperCase();
            String raw = model.getValueAt(r, 2) == null ? "" : model.getValueAt(r, 2).toString().trim();
            if (raw.isBlank()) continue;
            try {
                BigDecimal val = new BigDecimal(new BigInteger(raw), 2);
                if ("D".equals(tipo)) somaDebitos = somaDebitos.add(val);
                else if ("C".equals(tipo)) somaCreditos = somaCreditos.add(val);
            } catch (Exception ignored) {}
        }
        lbSoma.setText("Débito: " + somaDebitos.setScale(2).toPlainString() +
                "    Crédito: " + somaCreditos.setScale(2).toPlainString());
    }

    private boolean validarBalanceamento() {
        BigDecimal somaDebitos = BigDecimal.ZERO;
        BigDecimal somaCreditos = BigDecimal.ZERO;
        for (int r = 0; r < model.getRowCount(); r++) {
            String tipo = model.getValueAt(r, 1) == null ? "" : model.getValueAt(r, 1).toString().trim().toUpperCase();
            String raw = model.getValueAt(r, 2) == null ? "" : model.getValueAt(r, 2).toString().trim();
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

    private List<LancamentoItem> montarListaItens() {
        List<LancamentoItem> lista = new ArrayList<>();
        for (int r = 0; r < model.getRowCount(); r++) {
            String contaText = model.getValueAt(r, 0) == null ? "" : model.getValueAt(r, 0).toString().trim();
            if (contaText.isBlank()) continue;
            String tipo = model.getValueAt(r, 1) == null ? "" : model.getValueAt(r, 1).toString().trim().toUpperCase();
            String raw = model.getValueAt(r, 2) == null ? "" : model.getValueAt(r, 2).toString().trim();
            if (raw.isBlank()) continue;
            String idConta = contaText.split(" - ")[0].trim();
            BigDecimal valor = new BigDecimal(new BigInteger(raw), 2);
            LancamentoItem item = new LancamentoItem();
            item.setIdConta(idConta);
            item.setTipo(TipoLancamento.valueOf(tipo));
            item.setValor(valor);
            lista.add(item);
        }
        return lista;
    }

    public List<LancamentoItem> mostrarDialog() {
        setVisible(true);
        return resultado;
    }

    private static class DigitsLimitFilter extends DocumentFilter {
        private final int max;
        public DigitsLimitFilter(int max) { this.max = max; }
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            StringBuilder sb = new StringBuilder();
            for (char c : string.toCharArray()) if (Character.isDigit(c)) sb.append(c);
            if (fb.getDocument().getLength() + sb.length() <= max) super.insertString(fb, offset, sb.toString(), attr);
        }
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;
            StringBuilder sb = new StringBuilder();
            for (char c : text.toCharArray()) if (Character.isDigit(c)) sb.append(c);
            if (fb.getDocument().getLength() - length + sb.length() <= max) super.replace(fb, offset, length, sb.toString(), attrs);
        }
    }
}
