# Brinquedos Esportivos Kids – AC4 (Spring Boot)

Aplicação para uma empresa de **brinquedos esportivos para crianças de até 15 anos**, desenvolvida com **Spring Boot (Maven, Java 21)**, persistência com **Spring Data JPA** em banco **H2**, validações com **Bean Validation**, segurança com **Spring Security** e interface Web com **Thymeleaf**.

> **Integrantes:** _preencher nomes e RAs_ · **IDE:** IntelliJ IDEA · **Turma/Curso:** _preencher_

## 1. Tecnologias e dependências (Spring Initializr)

| Dependência | Uso no projeto |
|---|---|
| Spring Web | API REST (`/brinquedos`) e controllers Web |
| Spring Data JPA | Persistência e `JpaRepository` |
| H2 Database | Banco em memória |
| Validation | Validações com anotações (`@NotBlank`, `@Min`, ...) |
| Spring Security | Login, perfis `USER` e `ADMIN` |
| Thymeleaf | Landing page, login e painel |

![Spring Initializr](docs/img/initializr.png)

## 2. Como executar

```bash
./mvnw spring-boot:run        # Linux/macOS
.\mvnw spring-boot:run        # Windows (PowerShell)
```

Acesse `http://localhost:8080`. Usuários de teste:

| Usuário | Senha | Perfil | Permissões |
|---|---|---|---|
| `user` | `user123` | USER | Consultar (GET) |
| `admin` | `admin123` | ADMIN | Consultar, cadastrar, editar e excluir |

## 3. Estrutura do projeto

```
src/main/java/com/example/demo
├── model        Brinquedo (entidade + validações)
├── repository   BrinquedoRepository (JpaRepository)
├── service      BrinquedoService (regras e transações)
├── controller   BrinquedoRestController (API) e WebController (Thymeleaf)
├── exception    Tratamento de erros da API (400 e 404)
└── config       SecurityConfig e DataLoader (dados de exemplo)
src/main/resources
├── application.properties
├── templates    index.html, login.html, painel.html
└── static/css   estilo.css
```

## 4. Banco de dados (H2)

Configurado em `application.properties` (`jdbc:h2:mem:brinquedosdb`). A tabela **TDS_TB_BRINQUEDOS** é criada pelo Hibernate (`ddl-auto=update`):

| Coluna | Tipo | Regras |
|---|---|---|
| ID | BIGINT (identity) | Chave primária |
| NOME | VARCHAR(100) | Obrigatório, 2 a 100 caracteres |
| TIPO | VARCHAR(50) | Obrigatório |
| CLASSIFICACAO | INTEGER | Obrigatório, de 0 a 15 (idade mínima recomendada) |
| TAMANHO | VARCHAR(20) | Obrigatório |
| PRECO | DECIMAL(10,2) | Obrigatório, maior que zero |

Ao iniciar, a classe `DataLoader` insere 4 brinquedos de exemplo.

## 5. Repository (`JpaRepository`)

```java
public interface BrinquedoRepository extends JpaRepository<Brinquedo, Long> {
    List<Brinquedo> findByNomeContainingIgnoreCase(String nome);
    List<Brinquedo> findByTipoIgnoreCase(String tipo);
}
```

- **`JpaRepository<Brinquedo, Long>`**: o primeiro parâmetro é a entidade e o segundo é o tipo da chave primária. A interface estende `CrudRepository` (via `ListCrudRepository`), então já traz `save`, `saveAll`, `findById`, `findAll`, `deleteById`, `count` e outros.
- Não escrevemos a implementação nem SQL: o **Spring Data** gera, em tempo de execução, uma classe que implementa a interface.
- **Consultas derivadas:** o Spring interpreta o nome do método. `findByNomeContainingIgnoreCase` vira uma consulta `LIKE` sem diferenciar maiúsculas e minúsculas, e `findByTipoIgnoreCase` compara o tipo sem diferenciar maiúsculas e minúsculas.
- O `BrinquedoService` usa o repository dentro de transações (`@Transactional`). No POST e no PUT, o objeto é colocado em uma **lista**, e a lista é enviada ao banco com `saveAll` (commit ao final do método). O DELETE remove pelo **Id**.

## 6. Validações (Bean Validation)

As regras ficam na entidade `Brinquedo` e são ativadas por `@Valid`:

| Campo | Regras | Mensagem |
|---|---|---|
| nome | `@NotBlank`, `@Size(2,100)` | "O nome é obrigatório." |
| tipo | `@NotBlank`, `@Size(max 50)` | "O tipo é obrigatório..." |
| classificacao | `@NotNull`, `@Min(0)`, `@Max(15)` | "A classificação máxima é 15 anos." |
| tamanho | `@NotBlank`, `@Size(max 20)` | "O tamanho é obrigatório..." |
| preco | `@NotNull`, `@DecimalMin(0.01)`, `@Digits(8,2)` | "O preço deve ser maior que zero." |

- **API:** se os dados forem inválidos, o `ApiExceptionHandler` devolve **400** com um JSON listando cada campo e a mensagem.
- **Web:** o `BindingResult` mostra a mensagem abaixo do campo no formulário.

## 7. Segurança (Spring Security)

- **Login por formulário** (`/login`) para a interface Web e **HTTP Basic** para o Postman.
- Senhas com **BCrypt**; usuários em memória (`user` e `admin`).
- Regras de acesso:

| Rota | Acesso |
|---|---|
| `/`, `/login`, `/css/**` | Público |
| `GET /brinquedos/**` | USER e ADMIN |
| `POST`, `PUT`, `DELETE /brinquedos/**` | Somente ADMIN |
| `/painel` | USER e ADMIN |
| `/painel/salvar`, `/painel/editar/**`, `/painel/excluir/**` | Somente ADMIN |

O CSRF fica desligado apenas para `/brinquedos/**` (API usada pelo Postman). Os formulários Web usam o token CSRF.

## 8. Interface Web (Thymeleaf)

| Tela | Print |
|---|---|
| Landing page pública (`/`) | ![landing](docs/img/landing.png) |
| Login (`/login`) | ![login](docs/img/login.png) |
| Painel como USER (só consulta) | ![painel user](docs/img/painel-user.png) |
| Painel como ADMIN (cadastro/edição/exclusão) | ![painel admin](docs/img/painel-admin.png) |
| Validação no formulário | ![validacao web](docs/img/validacao-web.png) |

## 9. Testes no Postman (CRUD)

Servidor: `http://localhost:8080`. Autenticação: **Basic Auth** (`admin`/`admin123`). A coleção está em `postman/Brinquedos.postman_collection.json`.

### 9.1 Create – `POST /brinquedos`
Body (JSON):
```json
{ "nome": "Skate Infantil", "tipo": "Skate", "classificacao": 7, "tamanho": "M", "preco": 199.9 }
```
Resultado esperado: **201 Created**, com o brinquedo e o `id` gerado, e o cabeçalho `Location`.
![POST](docs/img/post.png)

### 9.2 Read – `GET /brinquedos` e `GET /brinquedos/{id}`
Resultado esperado: **200 OK** com a lista (ou o brinquedo pedido). Filtro opcional: `GET /brinquedos?nome=bola`.
![GET lista](docs/img/get-lista.png)
![GET id](docs/img/get-id.png)

### 9.3 Update – `PUT /brinquedos/{id}`
Body com os novos dados. Resultado esperado: **200 OK** com o brinquedo atualizado.
![PUT](docs/img/put.png)

### 9.4 Delete – `DELETE /brinquedos/{id}`
Resultado esperado: **204 No Content**. Uma nova consulta ao mesmo id retorna **404 Not Found**.
![DELETE](docs/img/delete.png)
![GET após delete](docs/img/get-404.png)

### 9.5 Validação – `POST` com dados inválidos
Resultado esperado: **400 Bad Request** com a lista de campos inválidos.
![Validação](docs/img/post-400.png)

### 9.6 Segurança
- Perfil `user` fazendo `POST` ou `DELETE`: **403 Forbidden**.
- Requisição sem credenciais: não é atendida (401 ou redirecionamento ao login).

![403](docs/img/user-403.png)

## 10. Vídeo de apresentação
Link: _colar aqui_
