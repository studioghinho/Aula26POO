package br.com.autotech.dao;

import br.com.autotech.factory.ConexaoFactory;
import br.com.autotech.model.Peca;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * KIT DE DESENVOLVIMENTO DE LABORATÓRIO • PECADAO (AUTOTECH)
 * ============================================================================
 *
 * 🏢 CENÁRIO DE NEGÓCIO:
 * A oficina mecânica AutoTech sofre com oscilações frequentes de rede Wi-Fi em seu galpão.
 * Sua missão como engenheiro de software é implementar a persistência local em SQLite
 * para que os mecânicos consigam cadastrar, consultar e dar baixa nas peças mesmo
 * sem sinal de internet!
 *
 * 🛠️ MISSÕES IMPLEMENTADAS NESTE ARQUIVO:
 * - Missão 1: Implementar o método criarTabelaSeNaoExistir()
 * - Missão 2: Implementar o método salvar(Peca peca) com resgate do ID gerado
 * - Missão 3: Implementar o método listarTodas() com ResultSet e while(rs.next())
 * - Missão 4: Implementar o método buscarPorCodigo(String codigo) com WHERE
 * - Missão 5: Implementar o método darBaixaEstoque(Long id, int quantidade)
 * ============================================================================
 */
public class PecaDao {

    /**
     * 🎯 MISSÃO 1: DDL AUTOMÁTICO
     * Cria a tabela "pecas" no SQLite se ela ainda não existir.
     */
    public void criarTabelaSeNaoExistir() {
        String sql = """
            CREATE TABLE IF NOT EXISTS pecas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                codigo TEXT UNIQUE NOT NULL,
                nome TEXT NOT NULL,
                preco REAL NOT NULL,
                estoque INTEGER NOT NULL
            );
        """;

        try (Connection conn = ConexaoFactory.obterConexao();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("🔧 [AutoTech] Tabela 'pecas' verificada/criada com sucesso!");

        } catch (SQLException e) {
            System.err.println("❌ Erro na Missão 1 (criarTabela): " + e.getMessage());
        }
    }

    /**
     * 🎯 MISSÃO 2: INSERT COM RESGATE DE CHAVE PRIMÁRIA
     * Salva a peça no banco e preenche o ID gerado automaticamente pelo SQLite.
     *
     * @param peca Objeto a ser salvo.
     * @return O mesmo objeto com o ID preenchido quando a inserção é concluída.
     */
    public Peca salvar(Peca peca) {
        String sql = "INSERT INTO pecas (codigo, nome, preco, estoque) VALUES (?, ?, ?, ?);";

        try (Connection conn = ConexaoFactory.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, peca.getCodigo());
            stmt.setString(2, peca.getNome());
            stmt.setDouble(3, peca.getPreco());
            stmt.setInt(4, peca.getEstoque());

            int linhas = stmt.executeUpdate();
            if (linhas > 0) {
                try (ResultSet chaves = stmt.getGeneratedKeys()) {
                    if (chaves.next()) {
                        peca.setId(chaves.getLong(1));
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro na Missão 2 (salvar): " + e.getMessage());
        }

        return peca;
    }

    /**
     * 🎯 MISSÃO 3: CONSULTA GERAL DE PEÇAS (SELECT)
     * Consulta todas as peças da tabela ordenadas pelo nome.
     *
     * @return Lista de peças encontradas no banco.
     */
    public List<Peca> listarTodas() {
        List<Peca> lista = new ArrayList<>();
        String sql = "SELECT id, codigo, nome, preco, estoque FROM pecas ORDER BY nome;";

        try (Connection conn = ConexaoFactory.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Peca p = new Peca(
                    rs.getLong("id"),
                    rs.getString("codigo"),
                    rs.getString("nome"),
                    rs.getDouble("preco"),
                    rs.getInt("estoque")
                );
                lista.add(p);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro na Missão 3 (listarTodas): " + e.getMessage());
        }

        return lista;
    }

    /**
     * 🎯 MISSÃO 4: CONSULTA POR CÓDIGO ESPECÍFICO (FILTRAGEM)
     * Busca uma peça específica através do seu código único.
     *
     * @param codigo Código alfanumérico da peça.
     * @return Objeto Peca se existir, ou null se não encontrada.
     */
    public Peca buscarPorCodigo(String codigo) {
        String sql = "SELECT id, codigo, nome, preco, estoque FROM pecas WHERE codigo = ?;";

        if (codigo == null || codigo.isBlank()) {
            return null;
        }

        try (Connection conn = ConexaoFactory.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Peca(
                        rs.getLong("id"),
                        rs.getString("codigo"),
                        rs.getString("nome"),
                        rs.getDouble("preco"),
                        rs.getInt("estoque")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro na Missão 4 (buscarPorCodigo): " + e.getMessage());
        }

        return null;
    }

    /**
     * 🎯 MISSÃO 5: BAIXA DE ESTOQUE NO BALCÃO (UPDATE)
     * Subtrai do estoque somente quando há quantidade suficiente.
     *
     * @param id Identificador da peça.
     * @param quantidadeRetirada Quantidade a subtrair.
     * @return true se atualizou, false se estoque insuficiente ou ID inexistente.
     */
    public boolean darBaixaEstoque(Long id, int quantidadeRetirada) {
        String sql = "UPDATE pecas SET estoque = estoque - ? WHERE id = ? AND estoque >= ?;";

        // Impede quantidade inválida e evita que uma retirada negativa aumente o estoque.
        if (id == null || quantidadeRetirada <= 0) {
            return false;
        }

        try (Connection conn = ConexaoFactory.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantidadeRetirada);
            stmt.setLong(2, id);
            stmt.setInt(3, quantidadeRetirada);

            int linhas = stmt.executeUpdate();
            return linhas > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erro na Missão 5 (darBaixaEstoque): " + e.getMessage());
            return false;
        }
    }
}
