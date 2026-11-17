# Resolução — Aula 26 | JDBC com SQLite

## Missões concluídas

1. **DDL automático (`criarTabelaSeNaoExistir`)** — verifica/cria a tabela `pecas` com os campos e restrições definidos no enunciado.
2. **Cadastro (`salvar`)** — insere com `PreparedStatement`, utiliza `Statement.RETURN_GENERATED_KEYS` e atribui ao objeto o ID gerado pelo SQLite.
3. **Listagem (`listarTodas`)** — consulta todos os registros, percorre o `ResultSet` com `while (rs.next())`, monta os objetos `Peca` e ordena pelo nome.
4. **Busca (`buscarPorCodigo`)** — utiliza parâmetro no `WHERE`, retorna a peça encontrada ou `null`.
5. **Baixa (`darBaixaEstoque`)** — executa o `UPDATE` condicionado à existência de estoque suficiente e retorna `true` somente quando uma linha é atualizada.

As operações JDBC utilizam `try-with-resources` para fechar conexões, comandos e resultados automaticamente. A baixa também rejeita ID nulo e quantidade menor ou igual a zero, evitando que uma quantidade negativa aumente o estoque.

## Validação realizada

- Compilação das classes do projeto com `javac`: concluída sem erros.
- Cadastro e recuperação das chaves primárias: verificados.
- Listagem e ordenação por nome: verificadas.
- Busca de código existente e inexistente: verificadas.
- Baixa válida, estoque insuficiente, quantidade zero e quantidade negativa: verificadas.
- Persistência: os dados foram gravados em uma execução e recuperados após encerrar e iniciar uma segunda execução do programa.

## Como executar no Windows

Na pasta `Aula26Kit`, execute `executar.bat`. O script compila as classes e abre o menu interativo. O arquivo `autotech.db` é criado automaticamente na pasta de execução e mantém os dados entre as execuções.
