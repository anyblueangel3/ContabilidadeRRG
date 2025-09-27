package com.rrg.contabilidade.model.dao;

import com.rrg.contabilidade.model.Periodo;
import com.rrg.contabilidade.model.StatusPeriodo;
import com.rrg.contabilidade.util.AbreBancoGeral;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para a entidade Periodo. Usa a Connection do banco da empresa passada no
 * construtor. Fornece também método multi-step para abrir período e importar
 * plano/contas do banco geral.
 */
public class PeriodoDAO {

    private final Connection conn; // conexão para o banco da empresa

    public PeriodoDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Insere um novo período no banco da empresa. Faz verificação de
     * sobreposição para o mesmo id_plano. Utiliza transação (commit/rollback).
     */
    public void inserir(Periodo periodo) throws SQLException {
        if (periodo == null) {
            throw new SQLException("Período nulo.");
        }
        if (periodo.getInicio() == null || periodo.getFim() == null) {
            throw new SQLException("Datas inválidas.");
        }
        if (periodo.getIdPlano() == null) {
            throw new SQLException("Plano (id_plano) obrigatório.");
        }
        if (periodo.getInicio().after(periodo.getFim())) {
            throw new SQLException("Data de início não pode ser depois da data de fim.");
        }

        // verifica sobreposição para o mesmo id_plano
        String sqlOverlap = "SELECT COUNT(*) FROM periodos WHERE id_plano = ? AND (inicio <= ? AND fim >= ?)";
        String sqlInsert = "INSERT INTO periodos (inicio, fim, status, id_plano) VALUES (?, ?, ?, ?)";

        boolean autoCommitAnterior = conn.getAutoCommit();
        try (PreparedStatement psOverlap = conn.prepareStatement(sqlOverlap); PreparedStatement psInsert = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

            conn.setAutoCommit(false);

            psOverlap.setInt(1, periodo.getIdPlano());
            psOverlap.setDate(2, periodo.getFim());
            psOverlap.setDate(3, periodo.getInicio());

            try (ResultSet rs = psOverlap.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Não é possível criar o período: datas sobrepõem período(s) existente(s) para este plano de contas.");
                }
            }

            psInsert.setDate(1, periodo.getInicio());
            psInsert.setDate(2, periodo.getFim());
            psInsert.setString(3, (periodo.getStatus() != null ? periodo.getStatus().name() : StatusPeriodo.ABERTO.name()));
            psInsert.setInt(4, periodo.getIdPlano());

            int affected = psInsert.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Inserção do período falhou, nenhuma linha afetada.");
            }
            try (ResultSet keys = psInsert.getGeneratedKeys()) {
                if (keys.next()) {
                    periodo.setId(keys.getInt(1));
                }
            }

            conn.commit();
        } catch (SQLException ex) {
            try {
                conn.rollback();
            } catch (Exception e) {
                /* ignora */ }
            throw ex;
        } finally {
            try {
                conn.setAutoCommit(autoCommitAnterior);
            } catch (Exception e) {
                /* ignora */ }
        }
    }

    /**
     * Método multi-step: cria período e, se necessário, importa um plano e suas
     * contas do banco GERAL.
     *
     * - idPlanoGeral: id do plano no banco geral que será importado. - fluxo:
     * 1) lê plano (geral) 2) cria plano no banco empresa (ou reutiliza se já
     * existir plano com mesmo nome) 3) copia contas do plano (pulando contas já
     * existentes) 4) insere período vinculando id_plano = idPlanoEmpresa
     * novo/recuperado
     *
     * Tudo executado em transação na conexão do banco da empresa (conn).
     * Leitura do banco geral usa nova conexão.
     */
    public void abrirPeriodoComPlanoImportado(Periodo periodo, int idPlanoGeral) throws SQLException {
        if (periodo == null) {
            throw new SQLException("Período nulo.");
        }
        if (periodo.getInicio() == null || periodo.getFim() == null) {
            throw new SQLException("Datas inválidas.");
        }
        if (periodo.getInicio().after(periodo.getFim())) {
            throw new SQLException("Data de início não pode ser depois da data de fim.");
        }

        boolean autoCommitAnterior = conn.getAutoCommit();

        // SQLs usados no banco empresa
        String sqlFindPlanoByName = "SELECT id FROM planos_de_contas WHERE nome = ?";
        String sqlInsertPlano = "INSERT INTO planos_de_contas (nome, descricao) VALUES (?, ?)";
        String sqlCountContaExists = "SELECT COUNT(*) FROM contas WHERE id = ?";
        String sqlInsertConta = "INSERT INTO contas (id, codigo_sped, descricao, classificacao, natureza, id_plano, obrigatorio_ecd, obrigatorio_ecf) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlOverlap = "SELECT COUNT(*) FROM periodos WHERE id_plano = ? AND (inicio <= ? AND fim >= ?)";
        String sqlInsertPeriodo = "INSERT INTO periodos (inicio, fim, status, id_plano) VALUES (?, ?, ?, ?)";

        // Conexão ao banco geral para ler plano e contas
        try (Connection connGeral = AbreBancoGeral.obterConexao()) {

            // 1) Ler plano do geral
            String sqlSelectPlanoGeral = "SELECT id, nome, descricao FROM planos_de_contas WHERE id = ?";
            String planoNome;
            String planoDescricao;
            try (PreparedStatement ps = connGeral.prepareStatement(sqlSelectPlanoGeral)) {
                ps.setInt(1, idPlanoGeral);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Plano de contas não encontrado no banco geral (id=" + idPlanoGeral + ").");
                    }
                    planoNome = rs.getString("nome");
                    planoDescricao = rs.getString("descricao");
                }
            }

            // inicia transação na conexão da empresa
            try {
                conn.setAutoCommit(false);

                // 2) verificar se plano já existe no banco empresa (por nome)
                Integer idPlanoEmpresa = null;
                try (PreparedStatement psFind = conn.prepareStatement(sqlFindPlanoByName)) {
                    psFind.setString(1, planoNome);
                    try (ResultSet rs = psFind.executeQuery()) {
                        if (rs.next()) {
                            idPlanoEmpresa = rs.getInt("id");
                        }
                    }
                }

                // se não existir, inserir plano e capturar id gerado
                if (idPlanoEmpresa == null) {
                    try (PreparedStatement psInsPlano = conn.prepareStatement(sqlInsertPlano, Statement.RETURN_GENERATED_KEYS)) {
                        psInsPlano.setString(1, planoNome);
                        psInsPlano.setString(2, planoDescricao);
                        int aff = psInsPlano.executeUpdate();
                        if (aff == 0) {
                            throw new SQLException("Falha ao inserir plano de contas no banco empresa.");
                        }
                        try (ResultSet keys = psInsPlano.getGeneratedKeys()) {
                            if (keys.next()) {
                                idPlanoEmpresa = keys.getInt(1);
                            } else {
                                throw new SQLException("Falha ao recuperar id do plano inserido no banco empresa.");
                            }
                        }
                    }
                }

                // 3) copiar contas do plano do geral para empresa (pulando contas já existentes)
                String sqlSelectContasGeral = "SELECT id, codigo_sped, descricao, classificacao, natureza, obrigatorio_ecd, obrigatorio_ecf FROM contas WHERE id_plano = ?";
                try (PreparedStatement psContasGeral = connGeral.prepareStatement(sqlSelectContasGeral)) {
                    psContasGeral.setInt(1, idPlanoGeral);
                    try (ResultSet rs = psContasGeral.executeQuery()) {
                        while (rs.next()) {
                            String contaId = rs.getString("id");
                            int codigoSped = rs.getInt("codigo_sped");
                            String descricao = rs.getString("descricao");
                            String classificacao = rs.getString("classificacao"); // já é string do enum
                            String natureza = rs.getString("natureza");
                            boolean obrigEcd = rs.getBoolean("obrigatorio_ecd");
                            boolean obrigEcf = rs.getBoolean("obrigatorio_ecf");

                            // verifica se conta já existe na empresa (pelo id)
                            try (PreparedStatement psContaExists = conn.prepareStatement(sqlCountContaExists)) {
                                psContaExists.setString(1, contaId);
                                try (ResultSet rsCnt = psContaExists.executeQuery()) {
                                    if (rsCnt.next() && rsCnt.getInt(1) > 0) {
                                        // já existe → pula (não altera)
                                        continue;
                                    }
                                }
                            }

                            // insere conta no banco empresa, associando ao idPlanoEmpresa
                            try (PreparedStatement psInsConta = conn.prepareStatement(sqlInsertConta)) {
                                psInsConta.setString(1, contaId);
                                psInsConta.setInt(2, codigoSped);
                                psInsConta.setString(3, descricao);
                                psInsConta.setString(4, classificacao);
                                psInsConta.setString(5, natureza);
                                psInsConta.setInt(6, idPlanoEmpresa);
                                psInsConta.setBoolean(7, obrigEcd);
                                psInsConta.setBoolean(8, obrigEcf);

                                psInsConta.executeUpdate();
                            }
                        } // fim while contas
                    }
                }

                // 4) Verificar sobreposição de períodos para este plano (agora idPlanoEmpresa)
                try (PreparedStatement psOverlapCheck = conn.prepareStatement(sqlOverlap)) {
                    psOverlapCheck.setInt(1, idPlanoEmpresa);
                    psOverlapCheck.setDate(2, periodo.getFim());
                    psOverlapCheck.setDate(3, periodo.getInicio());
                    try (ResultSet rsOverlap = psOverlapCheck.executeQuery()) {
                        if (rsOverlap.next() && rsOverlap.getInt(1) > 0) {
                            throw new SQLException("Não é possível criar o período: datas sobrepõem período(s) existente(s) para este plano de contas (empresa).");
                        }
                    }
                }

                // 5) Inserir o período com id_plano = idPlanoEmpresa
                try (PreparedStatement psInsPeriodo = conn.prepareStatement(sqlInsertPeriodo, Statement.RETURN_GENERATED_KEYS)) {
                    psInsPeriodo.setDate(1, periodo.getInicio());
                    psInsPeriodo.setDate(2, periodo.getFim());
                    psInsPeriodo.setString(3, (periodo.getStatus() != null ? periodo.getStatus().name() : StatusPeriodo.ABERTO.name()));
                    psInsPeriodo.setInt(4, idPlanoEmpresa);

                    int affected = psInsPeriodo.executeUpdate();
                    if (affected == 0) {
                        throw new SQLException("Falha ao inserir o período no banco empresa.");
                    }
                    try (ResultSet keys = psInsPeriodo.getGeneratedKeys()) {
                        if (keys.next()) {
                            periodo.setId(keys.getInt(1));
                        }
                    }
                }

                conn.commit();
            } catch (SQLException ex) {
                try {
                    conn.rollback();
                } catch (Exception e) {
                    /* ignora */ }
                throw ex;
            } finally {
                try {
                    conn.setAutoCommit(autoCommitAnterior);
                } catch (Exception e) {
                    /* ignora */ }
            }

        } // connGeral fechado automaticamente
    }

    /* Métodos auxiliares de listagem / busca / fechamento */
    public List<Periodo> listarTodos() throws SQLException {
        List<Periodo> lista = new ArrayList<>();
        String sql = "SELECT id, inicio, fim, status, id_plano FROM periodos ORDER BY inicio DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Periodo p = new Periodo();
                p.setId(rs.getInt("id"));
                p.setInicio(rs.getDate("inicio"));
                p.setFim(rs.getDate("fim"));
                p.setStatus(StatusPeriodo.valueOf(rs.getString("status")));
                p.setIdPlano(rs.getInt("id_plano"));
                lista.add(p);
            }
        }
        return lista;
    }

    public List<Periodo> listarAbertos() throws SQLException {
        List<Periodo> lista = new ArrayList<>();
        String sql = "SELECT id, inicio, fim, status, id_plano FROM periodos WHERE status = 'ABERTO' ORDER BY inicio DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Periodo p = new Periodo();
                p.setId(rs.getInt("id"));
                p.setInicio(rs.getDate("inicio"));
                p.setFim(rs.getDate("fim"));
                p.setStatus(StatusPeriodo.valueOf(rs.getString("status")));
                p.setIdPlano(rs.getInt("id_plano"));
                lista.add(p);
            }
        }
        return lista;
    }

    public Periodo buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, inicio, fim, status, id_plano FROM periodos WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Periodo p = new Periodo();
                    p.setId(rs.getInt("id"));
                    p.setInicio(rs.getDate("inicio"));
                    p.setFim(rs.getDate("fim"));
                    p.setStatus(StatusPeriodo.valueOf(rs.getString("status")));
                    p.setIdPlano(rs.getInt("id_plano"));
                    return p;
                }
            }
        }
        return null;
    }

    public boolean fecharPeriodo(int idPeriodo) throws SQLException {
        String sql = "UPDATE periodos SET status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, StatusPeriodo.FECHADO.name());
            ps.setInt(2, idPeriodo);
            int updated = ps.executeUpdate();
            return updated > 0;
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM periodos WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

}
