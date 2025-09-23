package com.rrg.contabilidade.util;

import com.rrg.contabilidade.controller.EmpresaController;
import com.rrg.contabilidade.model.Empresa;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import javax.swing.JOptionPane;

/**
 *
 * @author Chat GPT e Ronaldo Rodrigues Godoi
 *
 * Responsável por criar as tabelas do banco 'Geral'.
 *
 */
public class CriadorDasTabelasDoBancoDeDadosGeral {

    Connection conexao;
    Statement stmt;

    public CriadorDasTabelasDoBancoDeDadosGeral(Connection conexao, Statement stmt) {

        this.conexao = conexao;
        this.stmt = stmt;

    }

    public void criarTabelas() {
        try {

            criarTabelasDeUsuarios(stmt);

            criarTabelasDePlanoDeContas(stmt);

            criarTabelaDePeriodos(stmt);

            criarTabelasDeLancamentos(stmt);

            criarTabelaEmpresas(stmt);

            GeradorDosPlanosDeContasPadrao gerador = new GeradorDosPlanosDeContasPadrao(conexao);
            gerador.gerarPlanosPadrao(stmt);

            GeradorDosPapeis geradorDosPapeis = new GeradorDosPapeis(stmt);
            geradorDosPapeis.gerarPapeisPadrao();

            GeradorDasOperacoes geradorDasOperacoes = new GeradorDasOperacoes(stmt);
            geradorDasOperacoes.gerarOperacoes();
            
            GeradorDasOperacoesPapeis gerarOperacoesAdmin = new GeradorDasOperacoesPapeis(stmt);
            gerarOperacoesAdmin.gerarOperacoes();

            // Dentro do CriadorDasTabelasDoBancoDeDadosGeral, após gerar planos, papeis e operações
            EmpresaController empresaController = new EmpresaController();
            String cnpjFicticio = "99999999999999";

            try {
                Optional<Empresa> existente = empresaController.buscarEmpresaPorCnpj(cnpjFicticio);

                if (existente.isEmpty()) {
                    Empresa empresaFicticia = new Empresa();
                    empresaFicticia.setCnpj(cnpjFicticio);
                    empresaFicticia.setRazao("Empresa Fictícia");
                    empresaFicticia.setEndereco("Rua Exemplo, 123");
                    empresaFicticia.setResponsavel("Administrador");
                    empresaFicticia.setTelefoneEmpresa("0000-0000");
                    empresaFicticia.setTelefoneResponsavel("0000-0000");

                    // Usa o controller para inserir
                    empresaController.inserirEmpresa(empresaFicticia);

                    // Cria o banco da empresa
                    InicializadorDeBancoDeDadosEmpresa.verificarOuCriarBancoEmpresa(cnpjFicticio);
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Erro ao criar empresa fictícia: " + e.getMessage());
                e.printStackTrace();
            }

            // Outras tabelas futuras...
            JOptionPane.showMessageDialog(null,
                    "Tabelas criadas com sucesso.");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erro ao criar tabelas:\n " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void criarTabelasDeUsuarios(Statement stmt) throws SQLException {
        // Tabela de papeis
        String sql = """
        CREATE TABLE IF NOT EXISTS papeis (
            id INT AUTO_INCREMENT PRIMARY KEY,
            nome_papel VARCHAR(50) NOT NULL UNIQUE
        );
        """;
        stmt.executeUpdate(sql);
        JOptionPane.showMessageDialog(null, "Tabela 'papeis' criada ou verificada com sucesso.");

        // Tabela de usuários
        sql = """
        CREATE TABLE IF NOT EXISTS usuarios (
            id INT AUTO_INCREMENT PRIMARY KEY,
            nome VARCHAR(100) NOT NULL,
            login VARCHAR(100) NOT NULL UNIQUE,
            senha VARCHAR(255) NOT NULL,
            papel INT,
            status ENUM('ATIVO', 'INATIVO') DEFAULT 'ATIVO',
            data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            data_ultimo_acesso TIMESTAMP NULL,
            FOREIGN KEY (papel) REFERENCES papeis(id) 
                ON DELETE SET NULL 
                ON UPDATE CASCADE
        );
        """;
        stmt.executeUpdate(sql);
        JOptionPane.showMessageDialog(null, "Tabela 'usuarios' criada ou verificada com sucesso.");

        // Tabela de operacoes
        sql = """
        CREATE TABLE IF NOT EXISTS operacoes (
            id INT AUTO_INCREMENT PRIMARY KEY,
            operacao VARCHAR(100) NOT NULL UNIQUE,
            descricao VARCHAR(200) NOT NULL UNIQUE
        );
        """;
        stmt.executeUpdate(sql);
        JOptionPane.showMessageDialog(null, "Tabela 'operacoes' criada ou verificada com sucesso.");

        // Tabela de associações usuario <-> operacoes
        sql = """
        CREATE TABLE IF NOT EXISTS operacoes_usuarios (
            id INT AUTO_INCREMENT PRIMARY KEY,
            id_usuario INT NOT NULL,
            id_operacao INT NOT NULL,
            FOREIGN KEY (id_usuario) REFERENCES usuarios(id) 
                ON DELETE CASCADE 
                ON UPDATE CASCADE,
            FOREIGN KEY (id_operacao) REFERENCES operacoes(id) 
                ON DELETE CASCADE 
                ON UPDATE CASCADE,
            UNIQUE KEY idx_usuario_operacao (id_usuario, id_operacao)
        );
        """;
        stmt.executeUpdate(sql);
        JOptionPane.showMessageDialog(null, "Tabela 'operacoes_usuarios' criada ou verificada com sucesso.");

        // Tabela de associações papeis <-> operacoes
        sql = """
        CREATE TABLE IF NOT EXISTS operacoes_papeis (
            id INT AUTO_INCREMENT PRIMARY KEY,
            id_papel INT NOT NULL,
            id_operacao INT NOT NULL,
            FOREIGN KEY (id_papel) REFERENCES papeis(id) 
                ON DELETE CASCADE 
                ON UPDATE CASCADE,
            FOREIGN KEY (id_operacao) REFERENCES operacoes(id) 
                ON DELETE CASCADE 
                ON UPDATE CASCADE,
            UNIQUE KEY idx_papel_operacao (id_papel, id_operacao)
        );
        """;
        stmt.executeUpdate(sql);
        JOptionPane.showMessageDialog(null, "Tabela 'operacoes_papeis' criada ou verificada com sucesso.");
    }

    private void criarTabelasDePlanoDeContas(Statement stmt) throws SQLException {
        String sql = """
                     CREATE TABLE IF NOT EXISTS planos_de_contas (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        nome VARCHAR(255) NOT NULL UNIQUE,
                        descricao TEXT
                     );
        """;
        stmt.executeUpdate(sql);
        JOptionPane.showMessageDialog(null, "Tabela 'plano_de_contas' criada.");

        sql = """
       CREATE TABLE IF NOT EXISTS contas (
            id VARCHAR(20) NOT NULL,
            codigo_sped TINYINT UNSIGNED NOT NULL,
            descricao VARCHAR(255) NOT NULL,
            classificacao ENUM('ANALITICA', 'SINTETICA') NOT NULL,
            natureza ENUM('CREDORA', 'DEVEDORA') NOT NULL,
            id_plano INT NOT NULL,
            obrigatorio_ecd BOOLEAN DEFAULT TRUE,
            obrigatorio_ecf BOOLEAN DEFAULT TRUE,
            PRIMARY KEY (id_plano, id),
            FOREIGN KEY (id_plano) REFERENCES planos_de_contas(id)
           );
        """;
        stmt.executeUpdate(sql);

        JOptionPane.showMessageDialog(null, "Tabela 'contas' criada.");
    }

    private void criarTabelasDeLancamentos(Statement stmt) throws SQLException {
        String sql = """
                     CREATE TABLE IF NOT EXISTS lancamentos (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        id_periodo INT NOT NULL,
                        data_lancamento DATE NOT NULL,
                        historico TEXT NOT NULL,
                        id_usuario INT NOT NULL,
                        FOREIGN KEY (id_periodo) REFERENCES periodos(id)
                     );
                     """;

        stmt.executeUpdate(sql);
        JOptionPane.showMessageDialog(null, "Tabela 'lancamentos' criada.");

        sql = """
                     CREATE TABLE IF NOT EXISTS lancamentos_itens (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        id_lancamento BIGINT NOT NULL,
                        id_plano INT NOT NULL,
                        id_conta VARCHAR(20) NOT NULL,
                        tipo ENUM('D', 'C') NOT NULL,
                        valor DECIMAL(15, 2) NOT NULL,
                        historico_item TEXT,
                        documento VARCHAR(50),
                        FOREIGN KEY (id_lancamento) REFERENCES lancamentos(id),
                        FOREIGN KEY (id_plano, id_conta) REFERENCES contas(id_plano, id)
                     );
                     """;

        stmt.executeUpdate(sql);
        JOptionPane.showMessageDialog(null, "Tabela 'lancamentos_itens' criada.");

    }

    private void criarTabelaDePeriodos(Statement stmt) throws SQLException {
        String sql = """
                    CREATE TABLE IF NOT EXISTS periodos (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        inicio DATE NOT NULL UNIQUE,
                        fim DATE NOT NULL UNIQUE,
                        status ENUM('ABERTO', 'FECHADO') DEFAULT 'ABERTO',
                        id_plano INT NOT NULL,
                        FOREIGN KEY (id_plano) REFERENCES planos_de_contas(id)
                     );
                     """;
        stmt.executeUpdate(sql);
        JOptionPane.showMessageDialog(null, "Tabela 'periodos' criada.");
    }

    private void criarTabelaEmpresas(Statement stmt) throws SQLException {
        String sql = """
                     CREATE TABLE IF NOT EXISTS empresas (
                        cnpj VARCHAR(20)PRIMARY KEY NOT NULL,
                        razao VARCHAR(100) NOT NULL,
                        endereco VARCHAR(255) NOT NULL,
                        responsavel VARCHAR(100) NOT NULL,
                        telefone_empresa VARCHAR(20),
                        telefone_responsavel VARCHAR(20)
                     );
                     """;
        stmt.executeUpdate(sql);
        JOptionPane.showMessageDialog(null, "Tabela 'empresas' criada.");
    }

}
