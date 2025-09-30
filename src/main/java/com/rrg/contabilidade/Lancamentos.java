package com.rrg.contabilidade;

import com.rrg.contabilidade.controller.ContaController;
import com.rrg.contabilidade.model.Lancamento;
import com.rrg.contabilidade.model.LancamentoItem;
import com.rrg.contabilidade.model.Conta;
import com.rrg.contabilidade.model.Empresa;
import com.rrg.contabilidade.model.Periodo;
import com.rrg.contabilidade.model.Usuario;
import com.rrg.contabilidade.util.SessaoDeUsuario;
import com.rrg.contabilidade.util.SessaoListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Painel de Lançamentos. Mantém labels de sessão, campos de data/histórico,
 * chama ItemJDialog para digitar itens, mostra tabela de itens e permite gravar.
 */
public class Lancamentos extends JPanel implements SessaoListener {

    private ProgramaPrincipal programaPrincipal;
    private Usuario usuario;
    private Empresa empresa;
    private Periodo periodo;

    private JButton btAbrirPeriodo;
    private JButton btSair;
    private JButton btGravarLancamento;
    private JButton btAlterarLancamento;

    private JLabel lbUsuarioLogado;
    private JLabel lbEmpresaLogada;
    private JLabel lbPeriodoLogado;

    // Campos de lançamento
    private JTextField tfData;            // digitação ddMMyyyy
    private JTextArea taHistorico;
    private DefaultTableModel modelItens;
    private JTable tabelaItens;

    private final SimpleDateFormat formatoDigitacao = new SimpleDateFormat("ddMMyyyy");
    private final SimpleDateFormat formatoExibicao = new SimpleDateFormat("dd/MM/yyyy");

    private final ContaController contaController = new ContaController();

    public Lancamentos(ProgramaPrincipal pP) {
        this.programaPrincipal = pP;
        // desativa menu principal
        this.programaPrincipal.setMenuAtivo(false);

        this.usuario = SessaoDeUsuario.getUsuarioLogado();
        this.empresa = SessaoDeUsuario.getEmpresaLogada();
        this.periodo = SessaoDeUsuario.getPeriodoLogado();

        SessaoDeUsuario.addListener(this);

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // valida sessão
        if (usuario == null || empresa == null) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, faça login no menu Operações → Login Usuário + Empresa + Período.",
                    "Login necessário",
                    JOptionPane.WARNING_MESSAGE);
            SwingUtilities.invokeLater(() -> programaPrincipal.abrirTelaPrincipal());
            return;
        }

        setLayout(new BorderLayout(12,12));
        setBorder(BorderFactory.createEmptyBorder(12,12,12,12)); // margem superior/inferior/lat

        JLabel lbTitulo = new JLabel("Lançamentos", SwingConstants.CENTER);
        lbTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        add(lbTitulo, BorderLayout.NORTH);

        // painel central: data + histórico + tabela de itens
        JPanel centro = new JPanel(new BorderLayout(6,6));

        // topo centro: data e histórico
        JPanel topoCentro = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        topoCentro.add(new JLabel("Data (ddMMyyyy)"), gbc);
        tfData = new JTextField(10);
        gbc.gridx = 1;
        topoCentro.add(tfData, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        topoCentro.add(new JLabel("Histórico"), gbc);
        taHistorico = new JTextArea(3, 40);
        JScrollPane scrollHist = new JScrollPane(taHistorico);
        gbc.gridx = 1;
        topoCentro.add(scrollHist, gbc);

        centro.add(topoCentro, BorderLayout.NORTH);

        // tabela de itens (visualização)
        modelItens = new DefaultTableModel(new Object[]{"Conta", "Tipo", "Valor"}, 0) {
            @Override public boolean isCellEditable(int row,int col){ return false; }
        };
        tabelaItens = new JTable(modelItens);
        JScrollPane scrollItens = new JScrollPane(tabelaItens);
        scrollItens.setBorder(BorderFactory.createTitledBorder("Itens do Lançamento"));
        centro.add(scrollItens, BorderLayout.CENTER);

        add(centro, BorderLayout.CENTER);

        // rodapé com infos à esquerda e botões (2 linhas) à direita
        JPanel rodape = new JPanel(new BorderLayout(6,6));

        JPanel info = new JPanel(new GridLayout(3,1));
        lbUsuarioLogado = new JLabel();
        lbEmpresaLogada = new JLabel();
        lbPeriodoLogado = new JLabel();
        info.add(lbUsuarioLogado);
        info.add(lbEmpresaLogada);
        info.add(lbPeriodoLogado);
        rodape.add(info, BorderLayout.WEST);

        // botões: 2 linhas (painel vertical)
        JPanel botoesVert = new JPanel(new GridLayout(2,1,6,6));
        JPanel linha1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        JPanel linha2 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));

        btGravarLancamento = new JButton("Gravar Lançamento");
        btAlterarLancamento = new JButton("Alterar Lançamento");
        btAbrirPeriodo = new JButton("Abrir Período");
        btSair = new JButton("Sair dos Lançamentos");

        // ordem pedida: linha1 = Gravar, Alterar ; linha2 = Abrir Periodo, Sair
        linha1.add(btGravarLancamento);
        linha1.add(btAlterarLancamento);

        linha2.add(btAbrirPeriodo);
        linha2.add(btSair);

        botoesVert.add(linha1);
        botoesVert.add(linha2);

        rodape.add(botoesVert, BorderLayout.EAST);

        add(rodape, BorderLayout.SOUTH);

        // inicializações
        atualizarInfoSessao();
        definirEventos();

        // inicialmente botão gravar desabilitado
        btGravarLancamento.setEnabled(false);
    }

    private void definirEventos() {
        // ao pressionar ENTER no histórico -> abre ItemJDialog
        taHistorico.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    e.consume(); // evita quebra de linha
                    String hist = taHistorico.getText().trim();
                    if (!hist.isEmpty()) {
                        abrirItemDialogEReceberItens();
                    }
                }
            }
        });

        btAbrirPeriodo.addActionListener(e -> {
            PeriodoJDialog dialog = new PeriodoJDialog(programaPrincipal);
            dialog.setVisible(true);
            // SessaoDeUsuario notifica listeners — atualizarInfoSessao será chamado por listener
        });

        btSair.addActionListener(e -> {
            programaPrincipal.setMenuAtivo(true);
            programaPrincipal.abrirTelaPrincipal();
        });

        btGravarLancamento.addActionListener(e -> {
            // montar objeto Lancamento e chamar controller para gravar (atomicidade no controller)
            try {
                Lancamento lanc = montarLancamentoParaGravacao();
                // TODO: chamar LancamentoController para persistir (controller cuidará da transação)
                // Exemplo: LancamentoController lc = new LancamentoController(AbreBancoEmpresa.obterConexao()); lc.gravarLancamento(lanc);
                JOptionPane.showMessageDialog(this, "Lançamento pronto para gravação (simulate).");
                limparCampos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao preparar lançamento: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btAlterarLancamento.addActionListener(e -> {
            // apenas abre mensagem por enquanto; autorização deve ser verificada aqui no futuro
            JOptionPane.showMessageDialog(this, "Alterar lançamento — funcionalidade a implementar.");
        });
    }

    private void abrirItemDialogEReceberItens() {
        ItemJDialog dialog = new ItemJDialog(SwingUtilities.getWindowAncestor(this) instanceof Frame ? (Frame) SwingUtilities.getWindowAncestor(this) : null);
        List<LancamentoItem> itens = dialog.mostrarDialog();
        if (itens != null) {
            // recebeu lista balanceada
            mostrarItensNaTabela(itens);
            btGravarLancamento.setEnabled(true); // habilita salvar
        } else {
            // cancelado
            // não altera nada
        }
    }

    private void mostrarItensNaTabela(List<LancamentoItem> itens) {
        modelItens.setRowCount(0);
        for (LancamentoItem it : itens) {
            String conta = it.getIdConta();
            String tipo = it.getTipo() != null ? it.getTipo().name() : "";
            String valor = it.getValor() != null ? it.getValor().setScale(2).toPlainString() : "";
            modelItens.addRow(new Object[]{conta, tipo, valor});
        }
    }

    private Lancamento montarLancamentoParaGravacao() throws ParseException {
        String dataTexto = tfData.getText().trim();
        Date data = null;
        if (dataTexto != null && !dataTexto.isEmpty()) {
            data = formatoDigitacao.parse(dataTexto);
            // valida se dentro do período logado, se periodo != null
            if (periodo != null) {
                java.sql.Date inicio = periodo.getInicio();
                java.sql.Date fim = periodo.getFim();
                java.sql.Date dataSql = new java.sql.Date(data.getTime());
                if (dataSql.before(inicio) || dataSql.after(fim)) {
                    throw new IllegalArgumentException("Data fora do período logado.");
                }
            }
        } else {
            throw new IllegalArgumentException("Data obrigatória.");
        }

        String historico = taHistorico.getText().trim();
        if (historico.isEmpty()) throw new IllegalArgumentException("Histórico obrigatório.");

        Lancamento lanc = new Lancamento();
        if (periodo != null) lanc.setIdPeriodo(periodo.getId());
        lanc.setDataLancamento(new java.sql.Date(data.getTime()));
        lanc.setHistorico(historico);
        if (usuario != null) lanc.setIdUsuario(usuario.getId());

        // converte linhas da tabela em itens
        List<LancamentoItem> itens = new ArrayList<>();
        for (int r = 0; r < modelItens.getRowCount(); r++) {
            Object contaObj = modelItens.getValueAt(r, 0);
            Object tipoObj = modelItens.getValueAt(r, 1);
            Object valorObj = modelItens.getValueAt(r, 2);
            if (contaObj == null || tipoObj == null || valorObj == null) continue;
            String conta = contaObj.toString().trim();
            String tipo = tipoObj.toString().trim();
            String valorStr = valorObj.toString().trim().replace(".", "").replace(",", "");
            if (conta.isEmpty() || tipo.isEmpty() || valorStr.isEmpty()) continue;
            // assume valorStr is like "123.45" or "12345" -> convert to BigDecimal
            BigDecimal bd;
            try {
                bd = new BigDecimal(valorStr.replace(".", "").replace(",", ""));
                // but since tabela exibe "123.45", we attempt to parse with decimal
                if (valorStr.contains(".")) {
                    bd = new BigDecimal(valorStr);
                } else {
                    // if no dot, treat as plain whole with 2 decimals? keep as is
                    bd = new BigDecimal(valorStr);
                }
            } catch (Exception ex) {
                bd = BigDecimal.ZERO;
            }
            LancamentoItem item = new LancamentoItem();
            item.setIdConta(conta);
            item.setTipo(com.rrg.contabilidade.model.TipoLancamento.valueOf(tipo));
            item.setValor(bd);
            itens.add(item);
        }
        lanc.setLancamentoItens(new ArrayList<>(itens));
        return lanc;
    }

    private void limparCampos() {
        tfData.setText("");
        taHistorico.setText("");
        modelItens.setRowCount(0);
        btGravarLancamento.setEnabled(false);
    }

    private void atualizarInfoSessao() {
        this.usuario = SessaoDeUsuario.getUsuarioLogado();
        this.empresa = SessaoDeUsuario.getEmpresaLogada();
        this.periodo = SessaoDeUsuario.getPeriodoLogado();

        String usuarioText = (usuario != null) ? ("Usuário Logado: " + usuario.getNome()) : "Usuário Logado: null";
        String empresaText = (empresa != null) ? ("Empresa Logada: " + empresa.getRazao()) : "Empresa Logada: null";
        String periodoText;
        if (periodo != null && periodo.getInicio() != null && periodo.getFim() != null) {
            periodoText = "Período Logado: " + formatoExibicao.format(periodo.getInicio()) + " até " + formatoExibicao.format(periodo.getFim());
        } else {
            periodoText = "Período Logado: null";
        }

        lbUsuarioLogado.setText(usuarioText);
        lbEmpresaLogada.setText(empresaText);
        lbPeriodoLogado.setText(periodoText);
    }

    @Override
    public void aoLogar() {
        SwingUtilities.invokeLater(this::atualizarInfoSessao);
    }

    @Override
    public void aoLogout() {
        SwingUtilities.invokeLater(this::atualizarInfoSessao);
    }
}
