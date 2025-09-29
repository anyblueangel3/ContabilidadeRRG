package com.rrg.contabilidade.model.dao;

import com.rrg.contabilidade.model.Lancamento;
import com.rrg.contabilidade.model.LancamentoItem;
import com.rrg.contabilidade.model.TipoLancamento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para Lancamento / LancamentoItem.
 * NÃO abre/fecha conexão; NÃO faz commit/rollback.
 */
public class LancamentoDAO {

    private static final String SQL_INSERT_LANCAMENTO =
            "INSERT INTO lancamentos (id_periodo, data_lancamento, historico, id_usuario) VALUES (?, ?, ?, ?)";

    private static final String SQL_INSERT_ITEM =
            "INSERT INTO lancamentos_itens (id_lancamento, id_plano, id_conta, tipo, valor) VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_SELECT_LANCAMENTO =
            "SELECT id, id_periodo, data_lancamento, historico, id_usuario FROM lancamentos WHERE id = ?";

    private static final String SQL_SELECT_ITENS =
            "SELECT id, id_lancamento, id_plano, id_conta, tipo, valor FROM lancamentos_itens WHERE id_lancamento = ?";

    /**
     * Insere o registro principal e retorna o ID gerado.
     * (não faz commit/rollback)
     */
    public long inserirLancamento(Lancamento lancamento, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT_LANCAMENTO, Statement.RETURN_GENERATED_KEYS)) {

            // id_periodo (Integer pode ser null)
            if (lancamento.getIdPeriodo() == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, lancamento.getIdPeriodo());
            }

            // data_lancamento (Date pode ser null)
            if (lancamento.getDataLancamento() == null) {
                ps.setNull(2, Types.DATE);
            } else {
                ps.setDate(2, lancamento.getDataLancamento());
            }

            // historico
            ps.setString(3, lancamento.getHistorico());

            // id_usuario
            if (lancamento.getIdUsuario() == null) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, lancamento.getIdUsuario());
            }

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Inserção do lançamento falhou, nenhuma linha afetada.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                } else {
                    throw new SQLException("Falha ao obter ID gerado do lançamento.");
                }
            }
        }
    }

    /**
     * Insere os itens do lançamento (usa lancamento.getId()).
     */
    public void inserirItensLancamento(Lancamento lancamento, Connection conn) throws SQLException {
        List<LancamentoItem> itens = lancamento.getLancamentoItens();
        if (itens == null || itens.isEmpty()) return;

        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT_ITEM)) {
            for (LancamentoItem item : itens) {
                ps.setLong(1, lancamento.getId());

                if (item.getIdPlano() == null) {
                    ps.setNull(2, Types.INTEGER);
                } else {
                    ps.setInt(2, item.getIdPlano());
                }

                ps.setString(3, item.getIdConta());

                // tipo: D ou C
                ps.setString(4, item.getTipo() == null ? null : item.getTipo().name());

                ps.setBigDecimal(5, item.getValor());

                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /**
     * Busca um lançamento por id e popula seus itens.
     * Retorna null se não existir.
     */
    public Lancamento buscarPorId(long id, Connection conn) throws SQLException {
        Lancamento lanc = null;

        try (PreparedStatement ps = conn.prepareStatement(SQL_SELECT_LANCAMENTO)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    lanc = new Lancamento();
                    lanc.setId(rs.getLong("id"));

                    int idPeriodo = rs.getInt("id_periodo");
                    lanc.setIdPeriodo(rs.wasNull() ? null : idPeriodo);

                    Date dt = rs.getDate("data_lancamento");
                    lanc.setDataLancamento(dt);

                    lanc.setHistorico(rs.getString("historico"));

                    int idUsuario = rs.getInt("id_usuario");
                    lanc.setIdUsuario(rs.wasNull() ? null : idUsuario);

                    lanc.setLancamentoItens(new ArrayList<>());
                }
            }
        }

        if (lanc != null) {
            try (PreparedStatement ps2 = conn.prepareStatement(SQL_SELECT_ITENS)) {
                ps2.setLong(1, id);
                try (ResultSet rs2 = ps2.executeQuery()) {
                    while (rs2.next()) {
                        com.rrg.contabilidade.model.LancamentoItem item = new com.rrg.contabilidade.model.LancamentoItem();
                        item.setId(rs2.getLong("id"));
                        item.setIdLancamento(rs2.getLong("id_lancamento"));

                        int idPlano = rs2.getInt("id_plano");
                        item.setIdPlano(rs2.wasNull() ? null : idPlano);

                        item.setIdConta(rs2.getString("id_conta"));

                        String tipoStr = rs2.getString("tipo");
                        if (tipoStr != null) {
                            item.setTipo(TipoLancamento.valueOf(tipoStr));
                        } else {
                            item.setTipo(null);
                        }

                        item.setValor(rs2.getBigDecimal("valor"));

                        lanc.getLancamentoItens().add(item);
                    }
                }
            }
        }

        return lanc;
    }
}
