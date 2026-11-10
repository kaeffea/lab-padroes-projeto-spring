# Explorando Padrões de Projetos na Prática com Java

Repositório com as implementações dos padrões de projeto explorados no Lab "Explorando Padrões de Projetos na Prática com Java". Especificamente, este projeto explorou alguns padrões usando o Spring Framework, são eles:
- Singleton
- Strategy/Repository
- Facade

## Evoluções desta versão

Além dos padrões do lab, esta versão adiciona:

- **Chain of Responsibility**: o cliente passa por uma cadeia de validadores (`validation/`) antes de ser salvo. A ordem da cadeia é montada em `ValidacaoConfig`: nome → endereço → CEP (8 dígitos). Para criar uma nova regra, basta estender `ValidadorCliente` e encaixá-la na cadeia.
- **Erros de domínio**: `ClienteNaoEncontradoException` (HTTP 404) e `ClienteInvalidoException` (HTTP 400), traduzidos por um `@RestControllerAdvice`. Antes, buscar um id inexistente gerava erro 500.
- **Correções**:
  - `PUT /clientes/{id}` agora preserva o id (antes criava um novo cliente).
  - CEP inexistente no ViaCEP vira erro 400 claro.
  - O endereço já salvo é reaproveitado de fato (o ViaCEP devolve o CEP com hífen; a busca no banco agora usa esse formato).
- **Injeção por construtor** no service e **testes automatizados** (JUnit + Mockito, sem rede), com o ViaCEP mockado.

## Como rodar

Requisitos: JDK 11 ou superior. O Maven não precisa estar instalado (usa o Maven Wrapper).

```bash
./mvnw test          # testes
./mvnw spring-boot:run
```

Com a aplicação no ar:

- Swagger: http://localhost:8080/swagger-ui.html
- Console do H2: http://localhost:8080/h2-console (se habilitado em `application.properties`)

Exemplo de requisição:

```bash
curl -X POST http://localhost:8080/clientes \
  -H "Content-Type: application/json" \
  -d '{"nome":"Maria","endereco":{"cep":"57240000"}}'
```
