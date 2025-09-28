package com.rrg.contabilidade;

import com.rrg.contabilidade.controller.PeriodoController;
import com.rrg.contabilidade.model.Periodo;
import com.rrg.contabilidade.model.StatusPeriodo;
import com.rrg.contabilidade.util.AbreBancoEmpresa;
import com.rrg.contabilidade.util.AbreBancoGeral;
import com.rrg.contabilidade.util.SessaoDeUsuario;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * JDialog para gerenciamento de períodos contábeis.
 */
public class PeriodoJDialog extends JDialog {

    private PeriodoController periodoController;
    private JTable tabelaPeriodos;
    private DefaultTableModel tabelaModel;

    private JSpinner spinnerInicio, spinnerFim;
    private JComboBox<String> cbPlanos;
    private JButton btnAdicionar, btnConfirmar, btnVoltar;

    private List<Periodo> periodosAbertos;
    private List<Integer> idsPlanos;
    private List<Boolean> planosDoGeral;

    private SimpleDateFormat formatoData = new SimpleDateFormat("yyyy-MM-dd");
    private Connection connEmpresa; // conexão persistente

    public PeriodoJDialog(Frame parent) {
        super(parent, "Gerenciamento de Períodos", true);

        periodosAbertos = new ArrayList<>();
        idsPlanos = new ArrayList<>();
        planosDoGeral = new ArrayList<>();

        // abrir conexão persistente
        try {
            connEmpresa = AbreBancoEmpresa.obterConexao();
            this.periodoController = new PeriodoController(connEmpresa);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erro ao abrir conexão com o banco da empresa: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            dispose();
            return;
        }

        initComponents();
        carregarPeriodosAbertos();
        carregarPlanos();
        configurarListeners();

        setSize(750, 450);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Tabela de períodos
        tabelaModel = new DefaultTableModel(new String[]{"ID", "Início", "Fim", "Plano", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaPeriodos = new JTable(tabelaModel);
        tabelaPeriodos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Renderer seguro
        tabelaPeriodos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!periodosAbertos.isEmpty() && row < periodosAbertos.size()) {
                    Periodo p = periodosAbertos.get(row);
                    if (p.getStatus() == StatusPeriodo.FECHADO) {
                        c.setBackground(Color.LIGHT_GRAY);
                        c.setForeground(Color.BLACK);
                        setToolTipText("Período fechado, não é permitido lançamentos/ARE");
                    } else {
                        boolean doGeral = planosDoGeral.get(row % planosDoGeral.size());
                        c.setBackground(doGeral ? new Color(255, 255, 200) : new Color(200, 255, 200));
                        c.setForeground(Color.BLACK);
                        setToolTipText(doGeral ? "Plano do Banco Geral" : "Plano do Banco Empresa");
                    }
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                    setToolTipText(null);
                }

                if (isSelected) {
                    c.setBackground(new Color(100, 149, 237));
                    c.setForeground(Color.WHITE);
                }

                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(tabelaPeriodos);
        scroll.setBorder(new TitledBorder("Períodos"));
        add(scroll, BorderLayout.CENTER);

        // Painel inferior
        JPanel painelNovo = new JPanel(new GridBagLayout());
        painelNovo.setBorder(new TitledBorder("Adicionar Novo Período"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Spinners
        spinnerInicio = new JSpinner(new SpinnerDateModel());
        spinnerInicio.setEditor(new JSpinner.DateEditor(spinnerInicio, "yyyy-MM-dd"));
        spinnerFim = new JSpinner(new SpinnerDateModel());
        spinnerFim.setEditor(new JSpinner.DateEditor(spinnerFim, "yyyy-MM-dd"));

        gbc.gridx = 0;
        gbc.gridy = 0;
        painelNovo.add(new JLabel("Início:"), gbc);
        gbc.gridx = 1;
        painelNovo.add(spinnerInicio, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        painelNovo.add(new JLabel("Fim:"), gbc);
        gbc.gridx = 1;
        painelNovo.add(spinnerFim, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        painelNovo.add(new JLabel("Plano de Contas:"), gbc);
        gbc.gridx = 1;
        cbPlanos = new JComboBox<>();
        painelNovo.add(cbPlanos, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        btnAdicionar = new JButton("Adicionar Novo Período");
        painelNovo.add(btnAdicionar, gbc);
        gbc.gridx = 1;
        btnConfirmar = new JButton("Confirmar Seleção");
        painelNovo.add(btnConfirmar, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        btnVoltar = new JButton("Voltar para Lançamentos");
        painelNovo.add(btnVoltar, gbc);

        add(painelNovo, BorderLayout.SOUTH);
    }

    private void configurarListeners() {
        tabelaPeriodos.getSelectionModel().addListSelectionListener(e -> {
            int linha = tabelaPeriodos.getSelectedRow();
            if (linha >= 0 && linha < periodosAbertos.size()) {
                Periodo p = periodosAbertos.get(linha);
                SessaoDeUsuario.logarPeriodo(p);
            }
        });

        btnAdicionar.addActionListener(this::adicionarNovoPeriodo);

        btnConfirmar.addActionListener(e -> {
            int linha = tabelaPeriodos.getSelectedRow();
            if (linha >= 0 && linha < periodosAbertos.size()) {
                Periodo p = periodosAbertos.get(linha);
                SessaoDeUsuario.logarPeriodo(p);
                JOptionPane.showMessageDialog(this, "Período de trabalho definido.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um período.", "Atenção", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnVoltar.addActionListener(e -> {
            dispose(); // apenas fecha o diálogo, retorna ao fluxo anterior
        });
    }

    private void carregarPeriodosAbertos() {
        try {
            periodosAbertos = periodoController.listarPeriodos();
            tabelaModel.setRowCount(0);

            if (periodosAbertos.isEmpty()) {
                tabelaModel.addRow(new Object[]{"-", "-", "-", "-", "-"});
            } else {
                for (Periodo p : periodosAbertos) {
                    tabelaModel.addRow(new Object[]{
                            p.getId(), p.getInicio(), p.getFim(), p.getIdPlano(), p.getStatus()
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar períodos: " + e.getMessage());
        }
    }

    private void carregarPlanos() {
        cbPlanos.removeAllItems();
        idsPlanos.clear();
        planosDoGeral.clear();

        try {
            // Banco geral
            try (Connection connGeral = AbreBancoGeral.obterConexao();
                 PreparedStatement ps = connGeral.prepareStatement("SELECT id, nome FROM planos_de_contas");
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    cbPlanos.addItem("[GERAL] " + rs.getString("nome"));
                    idsPlanos.add(rs.getInt("id"));
                    planosDoGeral.add(true);
                }
            }

            // Banco empresa
            try (PreparedStatement ps = connEmpresa.prepareStatement("SELECT id, nome FROM planos_de_contas");
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    cbPlanos.addItem("[EMPRESA] " + rs.getString("nome"));
                    idsPlanos.add(rs.getInt("id"));
                    planosDoGeral.add(false);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar planos: " + e.getMessage());
        }
    }

    private void adicionarNovoPeriodo(ActionEvent evt) {
        java.util.Date inicioDate = (java.util.Date) spinnerInicio.getValue();
        java.util.Date fimDate = (java.util.Date) spinnerFim.getValue();

        if (inicioDate == null || fimDate == null) {
            JOptionPane.showMessageDialog(this, "Preencha as datas de início e fim.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (fimDate.before(inicioDate)) {
            JOptionPane.showMessageDialog(this, "Data de fim não pode ser anterior à data de início.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date inicio = new Date(inicioDate.getTime());
        Date fim = new Date(fimDate.getTime());

        int idxPlano = cbPlanos.getSelectedIndex();
        if (idxPlano < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um plano de contas.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validação de sobreposição
        for (Periodo existente : periodosAbertos) {
            if (!(fim.before(existente.getInicio()) || inicio.after(existente.getFim()))) {
                JOptionPane.showMessageDialog(this,
                        "O período informado se sobrepõe com o período existente (ID: " + existente.getId() + ").",
                        "Erro de sobreposição",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            Periodo p = new Periodo();
            p.setInicio(inicio);
            p.setFim(fim);
            p.setStatus(StatusPeriodo.ABERTO);

            int idPlano = idsPlanos.get(idxPlano);
            boolean doGeral = planosDoGeral.get(idxPlano);

            if (doGeral) {
                periodoController.abrirPeriodoComPlanoImportado(p, idPlano);
            } else {
                p.setIdPlano(idPlano);
                periodoController.cadastrarPeriodo(p);
            }

            carregarPeriodosAbertos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar período: " + e.getMessage());
        }
    }

    @Override
    public void dispose() {
        // fecha a conexão persistente
        try {
            if (connEmpresa != null && !connEmpresa.isClosed()) {
                connEmpresa.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.dispose();
    }
}
