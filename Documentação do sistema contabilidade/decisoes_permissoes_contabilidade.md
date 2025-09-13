
# Resumo de decisões — Permissões e Autorização
**Projeto:** contabilidade  
**Autor das decisões:** Ronaldo R. Godoi  
**Data:** (conversa anterior)  

---

## 1) Decisões tomadas (resumidas)
- **Códigos de operação:** serão cadastrados pelo programador (texto livre); serão usados **no código** para checagem.  
- **Operações (`operacoes`)** terão também o campo `descricao` para exibição nas UIs (ao lado do `JCheckBox`).  
- **Modelo de autorização adotado:** **RBAC + permissões por usuário (ACL hybrid)**.  
  - A tabela **`operacoes_usuarios`** é a *autoridade final*: **se o registro existir → o usuário pode executar**.  
  - `operacoes_papeis` serve para atribuir operações a papéis; ao atribuir a um papel, a operação pode ser **propagada** (copiada) para os usuários daquele papel (ou ser apenas usada como base — sua escolha).  
- **Política por padrão:** *deny by default* (negar se não houver registro).  
- **Admin:** papel `ADMIN` tem, por enquanto, **todas** as permissões (tratamento especial no código).  
- **Granularidade:** média — suficiente para pequenas empresas e educação, nem muito fina nem muito grosseira.  
- **Auditoria:** por enquanto **não será criada tabela de auditoria** (você registrará autoria em lançamentos e tem `data_ultimo_acesso`).  
- **Cache de permissões:** permissão do usuário será carregada na sessão (`SessaoDeUsuario`) ao logar e usada durante a sessão.  
- **Operações — formato:** você prefere **texto livre** para nomes (ex.: "Cadastro de Usuários") — o código e o programador manterão o alinhamento entre código e registros do banco.  

---

## 2) Pequenas alterações DDL recomendadas
- Adicionar `descricao` em `operacoes` (já decidido):
```sql
ALTER TABLE operacoes ADD COLUMN descricao VARCHAR(255);
```
- Estrutura atual (sugestão mínima):
```sql
CREATE TABLE operacoes (
  id INT AUTO_INCREMENT PRIMARY KEY,
  operacao VARCHAR(255) NOT NULL UNIQUE, -- nome "cadastrado" (texto livre)
  descricao VARCHAR(255)
);
```
- `operacoes_usuarios` permanece simples (sem tipo):
```sql
CREATE TABLE operacoes_usuarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  id_usuario INT NOT NULL,
  id_operacao INT NOT NULL,
  UNIQUE KEY idx_usuario_operacao (id_usuario, id_operacao),
  FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE,
  FOREIGN KEY (id_operacao) REFERENCES operacoes(id) ON DELETE CASCADE
);
```
- `operacoes_papeis` (mesma ideia, sem tipo):
```sql
CREATE TABLE operacoes_papeis (
  id INT AUTO_INCREMENT PRIMARY KEY,
  id_papel INT NOT NULL,
  id_operacao INT NOT NULL,
  UNIQUE KEY idx_papel_operacao (id_papel, id_operacao),
  FOREIGN KEY (id_papel) REFERENCES papeis(id) ON DELETE CASCADE,
  FOREIGN KEY (id_operacao) REFERENCES operacoes(id) ON DELETE CASCADE
);
```

---

## 3) Fluxos e regras de negócio (especificação prática)
1. **Login**
   - Ao logar, carregar para a sessão (`SessaoDeUsuario`) o `Set<Integer> permissoesIds` que contém **todas** as operações atribuídas ao usuário:
     - Comece com as operações derivadas do(s) papel(is) do usuário (consulta em `operacoes_papeis`), **e** as operações já presentes em `operacoes_usuarios` para o usuário.
     - Se sua regra for _operacoes_usuarios_ ser única autoridade, então preencha a sessão apenas com `operacoes_usuarios` e — opcionalmente — ofereça comando para “sincronizar papel → usuário” (explicado abaixo).
2. **Checagem de permissão no código**
   - Em cada ação sensível (controller/service), chame `AutorizacaoService.temPermissao(usuario, idOperacao)` ou `AutorizacaoService.temPermissao(usuario, "nome_operacao")` antes de executar. **Sempre** validar no backend, UI é só conveniência.
3. **Gerenciamento de permissões (UI)**
   - Tela: `PermissoesPorUsuario` (JPanel) — lista todas `operacoes` com `JCheckBox` ao lado da `descricao`. Marcar = permitido.
   - Ao salvar: calcular diff entre `operacoes_usuarios` atuais e marcadas; executar deletes e inserts (opção 2 escolhida).
4. **Sincronização papel → usuário (opcional)**
   - Ao adicionar/remover uma operação a um **papel**, você pode **propagar automaticamente** (criar/delete) a operação em `operacoes_usuarios` para cada usuário com aquele papel (isto garante que `operacoes_usuarios` seja realmente a autoridade final). **Cuidado**: isso dificulta reversões; prefira perguntar ao admin se quer propagar.
5. **Admin**
   - `ADMIN` pode ser tratado como **superuser**: `temPermissao` retorna true para todas operações; ou alternativamente, ao criar o `ADMIN`, insira entradas `operacoes_usuarios` para todas as operações existentes (menos elegante).

---

## 4) UI: Painel "Permissões por Usuário" (esqueleto)
- Componentes:
  - Combobox/Busca para selecionar usuário (ou abrir já para o usuário logado, se for auto-gerenciamento).
  - `JScrollPane` com painel vertical contendo `JCheckBox` por operação (label = `operacao` + " — " + `descricao`).
  - Botões: `Salvar` (faz diff), `Selecionar todos`, `Desmarcar todos`, `Recarregar`.
- Query sugerida para carregar com flag:
```sql
SELECT o.id, o.operacao, o.descricao, 
       (CASE WHEN ou.id_operacao IS NOT NULL THEN 1 ELSE 0 END) AS permitida
FROM operacoes o
LEFT JOIN operacoes_usuarios ou ON o.id = ou.id_operacao AND ou.id_usuario = ?
ORDER BY o.operacao;
```
- Salvamento: executar em transação; deletar os ids que foram desmarcados e inserir os novos ids.

---

## 5) Autorização em runtime — `AutorizacaoService` (esboço)
- API sugerida:
```java
public class AutorizacaoService {
    public boolean temPermissao(Usuario u, int idOperacao);
    public Set<Integer> permissoesDoUsuario(Usuario u);
    public void atualizarPermissoesUsuario(int usuarioId, List<Integer> idsOperacao); // controller chama DAO
}
```
- Implementação prática:
  - Ao logar: carregar `Set<Integer>` de permissões e guardar em `SessaoDeUsuario` (cache).
  - `temPermissao` primeiro verifica `SessaoDeUsuario.getPermissoes()`; se null, carrega do DB e armazena.
  - Invalidação: sempre que alterar permissões de um usuário, atualizar a sessão desse usuário (se estiver logado) ou forçar reload no próximo login.

---

## 6) Riscos / Observações
- **Manutenção:** permissões por usuário (ACL) aumentam custos de manutenção. Recomendo preferência por papéis (RBAC) para regras gerais; usar ACLs apenas para exceções/ajustes.  
- **Consistência:** se as `operacoes` forem renomeadas livremente, quebram referências no código. Ideal: programador documentar/backup/versão dos códigos de operação.  
- **Performance:** carregar permissões na sessão evita consultas repetidas; para sistemas com muitos usuários/sessões, monitore memória.  
- **Segurança:** esconder UI não é suficiente; validação no backend é obrigatória.

---

## 7) Próximos passos sugeridos (implementação incremental)
1. Adicionar campo `descricao` em `operacoes` e popular com textos amigáveis.  
2. Implementar `OperacaoDAO` + `OperacaoController` (listar, procurar).  
3. Implementar `AutorizacaoService` (carregar permissões por login, temPermissao).  
4. Implementar `PermissoesPorUsuario` (JPanel com JCheckBox) — usar diff para salvar.  
5. Opcional: tela `PermissoesPorPapel` (aplicar/preservar propagate-to-users).  
6. Garantir que todos controllers chequem `AutorizacaoService.temPermissao` antes de executar ações sensíveis.  

---

Se quiser, eu já **posso**:
- Gerar o código Java do `AutorizacaoService` + `OperacaoDAO/Controller`; ou
- Gerar o JPanel `PermissoesPorUsuario` completo com lógica de carregamento e salvamento (JCheckBox + diff); ou
- Gerar scripts SQL para criar/alterar as tabelas sugeridas; ou
- Gerar o código que integra o carregamento de permissões na `SessaoDeUsuario` no momento do login.

Diga qual peça quer que eu **gere primeiro**.  
---

*Obs.: não posso persistir memórias fora desta conversa permanente — se quiser manter um registro definitivo, salve este arquivo ou peça para eu gerar outro formato (PDF, TXT).*
