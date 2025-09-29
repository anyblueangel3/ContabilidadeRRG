package com.rrg.contabilidade;

import com.rrg.contabilidade.model.Usuario;
import com.rrg.contabilidade.util.OperacoesDoSistema;
import com.rrg.contabilidade.util.PermissaoUtil;
import com.rrg.contabilidade.util.SessaoDeUsuario;
import com.rrg.contabilidade.util.SessaoListener;

import javax.swing.*;

/**
 *
 * @author Ronaldo Rodrigues Godoi e Chat GPT
 *
 * JMenuBar do sistema, chama diretamente os painéis de cadastro existentes.
 * Barra de menu principal com verificação de permissões.
 */
public class MenuPrincipal extends JMenuBar implements SessaoListener {

    private ProgramaPrincipal programaPrincipal;
    private Usuario usuario = SessaoDeUsuario.getUsuarioLogado();

    // Agora todos os JMenuItem são atributos
    private JMenuItem miUsuarios;
    private JMenuItem miEmpresas;
    private JMenuItem miPapeis;
    private JMenuItem miPlanoDeContasEContas;
    private JMenuItem miLancamentos;
    private JMenuItem miAre;
    private JMenuItem miBalancete;
    private JMenuItem miBalanco;
    private JMenuItem miDre;
    private JMenuItem miPlanoContas;
    private JMenuItem miBackup;
    private JMenuItem miIndices;
    
    private JMenuItem miLoginTriplo;

    public MenuPrincipal(ProgramaPrincipal programaPrincipal) {
        this.programaPrincipal = programaPrincipal;

        // ===== Menu Cadastros =====
        JMenu menuCadastros = new JMenu("Cadastros");

        miUsuarios = new JMenuItem("Cadastro de Usuários e suas Operações");
        miUsuarios.addActionListener(e -> {
            CadastroDeUsuarios cadastroUsuarios = new CadastroDeUsuarios(programaPrincipal, null, false);
            programaPrincipal.setPainelCentral(cadastroUsuarios);
        });

        miEmpresas = new JMenuItem("Cadastro de Empresas");
        miEmpresas.addActionListener(e -> {
            CadastroDeEmpresas cadastroEmpresa = new CadastroDeEmpresas(programaPrincipal, null);
            programaPrincipal.setPainelCentral(cadastroEmpresa);
        });

        miPapeis = new JMenuItem("Cadastro de Papéis e suas Operações");
        miPapeis.addActionListener(e -> {
            CadastroDePapeis cadastroDePapeis = new CadastroDePapeis(programaPrincipal, null);
            programaPrincipal.setPainelCentral(cadastroDePapeis);
        });

        miPlanoDeContasEContas = new JMenuItem("Cadastro de Planos de Contas e Contas");
        miPlanoDeContasEContas.addActionListener(e -> {
            CadastroDePlanosDeContasEContas cadastroPlanosEContas =
                    new CadastroDePlanosDeContasEContas(programaPrincipal);
            programaPrincipal.setPainelCentral(cadastroPlanosEContas);
        });

        menuCadastros.add(miUsuarios);
        menuCadastros.add(miEmpresas);
        menuCadastros.add(miPapeis);
        menuCadastros.add(miPlanoDeContasEContas);

        // ===== Item Sair =====
        JMenuItem miSair = new JMenuItem("Sair");
        miSair.addActionListener(e -> programaPrincipal.sairDoSistema());

        menuCadastros.addSeparator();
        menuCadastros.add(miSair);

        // ===== Menu Operações =====
        JMenu menuOperacoes = new JMenu("Operações");
        
        miLoginTriplo = new JMenuItem("Login Usuário + Empresa + Periodo");
        miLoginTriplo.addActionListener(e -> {
            LoginUsuarioEmpresaPeriodo dialog = 
                    new LoginUsuarioEmpresaPeriodo(programaPrincipal);
            dialog.setVisible(true);
        });

        miLancamentos = new JMenuItem("Lançamentos");
        miLancamentos.addActionListener(e -> programaPrincipal.setPainelCentral(
                new Lancamentos(programaPrincipal)));

        miAre = new JMenuItem("ARE");
        miAre.addActionListener(e -> {
            // TODO: implementar classe ARE
            // programaPrincipal.setPainelCentral(new ArePanel());
        });
        
        menuOperacoes.add(miLoginTriplo);

        menuOperacoes.add(miLancamentos);
        menuOperacoes.add(miAre);

        // ===== Menu Relatórios =====
        JMenu menuRelatorios = new JMenu("Relatórios");

        miBalancete = new JMenuItem("Balancete");
        miBalancete.addActionListener(e -> {
            // TODO: implementar relatório
        });

        miBalanco = new JMenuItem("Balanço");
        miBalanco.addActionListener(e -> {
            // TODO: implementar relatório
        });

        miDre = new JMenuItem("DRE");
        miDre.addActionListener(e -> {
            // TODO: implementar relatório
        });

        miPlanoContas = new JMenuItem("Plano de Contas");
        miPlanoContas.addActionListener(e -> {
            // TODO: implementar relatório
        });

        menuRelatorios.add(miBalancete);
        menuRelatorios.add(miBalanco);
        menuRelatorios.add(miDre);
        menuRelatorios.add(miPlanoContas);

        // ===== Menu Manutenção =====
        JMenu menuManutencao = new JMenu("Manutenção");

        miBackup = new JMenuItem("Backup");
        miBackup.addActionListener(e -> {
            // TODO: implementar Backup
        });

        miIndices = new JMenuItem("Índices");
        miIndices.addActionListener(e -> {
            // TODO: implementar ou excluir
        });

        menuManutencao.add(miBackup);
        menuManutencao.add(miIndices);

        // ===== Registra como listener de sessão =====
        SessaoDeUsuario.addListener(this);

        // ===== Aplica permissões iniciais =====
        atualizarPermissoesMenu();

        // ===== Adiciona menus à barra =====
        add(menuCadastros);
        add(menuOperacoes);
        add(menuRelatorios);
        add(menuManutencao);
    }

    /**
     * Atualiza o estado de habilitação dos menus com base nas permissões
     * do usuário logado.
     */
    public void atualizarPermissoesMenu() {
        miUsuarios.setEnabled(PermissaoUtil.temPermissao(OperacoesDoSistema.CADASTRO_USUARIOS));
        miEmpresas.setEnabled(PermissaoUtil.temPermissao(OperacoesDoSistema.CADASTRO_EMPRESAS));
        miPapeis.setEnabled(PermissaoUtil.temPermissao(OperacoesDoSistema.CADASTRO_PAPEIS));
        miPlanoDeContasEContas.setEnabled(PermissaoUtil.temPermissao(OperacoesDoSistema.CADASTRO_PLANOS_E_CONTAS));
        miLancamentos.setEnabled(PermissaoUtil.temPermissao(OperacoesDoSistema.LANCAMENTOS));
        miAre.setEnabled(PermissaoUtil.temPermissao(OperacoesDoSistema.ARE));
        miBalancete.setEnabled(PermissaoUtil.temPermissao(OperacoesDoSistema.BALANCETE));
        miBalanco.setEnabled(PermissaoUtil.temPermissao(OperacoesDoSistema.BALANCO));
        miDre.setEnabled(PermissaoUtil.temPermissao(OperacoesDoSistema.DRE));
        miPlanoContas.setEnabled(PermissaoUtil.temPermissao(OperacoesDoSistema.PLANO_DE_CONTAS));
        miBackup.setEnabled(PermissaoUtil.temPermissao(OperacoesDoSistema.BACKUP));
        miIndices.setEnabled(PermissaoUtil.temPermissao(OperacoesDoSistema.INDICES));
    }

    @Override
    public void aoLogar() {
        usuario = SessaoDeUsuario.getUsuarioLogado();
        atualizarPermissoesMenu();
    }

    @Override
    public void aoLogout() {
        usuario = null;
        //SessaoDeUsuario.logout();
        SessaoDeUsuario.logoutEmpresa();
        SessaoDeUsuario.logoutPeriodo();
        atualizarPermissoesMenu();
        
        JOptionPane.showMessageDialog(this,
                "Você foi desconectado.", "Logout",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
