# Sistema de Gestão de Cantina

## Pré-requisitos
- Java 17+
- MySQL 8.0+
- Maven (ou usar o mvnw incluído)

## Configurar MySQL
Antes de correr o projeto, cria a base de dados no MySQL:

```sql
CREATE DATABASE cantina_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Se a tua password do MySQL não for vazia, edita o ficheiro:
`src/main/resources/application.properties`

Muda a linha:
```
spring.datasource.password=
```
Para a tua password, por exemplo:
```
spring.datasource.password=1234
```

## Correr o projeto
```bash
./mvnw spring-boot:run
```
No Windows:
```
mvnw.cmd spring-boot:run
```

## Aceder ao sistema
Abre o browser em: http://localhost:8080

## Contas de teste (criadas automaticamente na primeira execução)
| Username     | Password | Perfil      |
|--------------|----------|-------------|
| gerente      | admin    | Gerente     |
| funcionario  | 1234     | Funcionário |

## Estrutura do projeto
```
src/main/java/com/cantina/
├── config/          ← SecurityConfig, DadosIniciais
├── controller/      ← AuthController, DashboardController, VendaController...
├── modelo/          ← Produto, Venda, ItemVenda, Utilizador
├── repositorio/     ← Interfaces JPA
└── servico/         ← VendaServico, EstoqueServico, FuncionarioServico...

src/main/resources/
├── templates/       ← HTML (Thymeleaf)
│   ├── public/      ← Páginas públicas (inicio, sobre)
│   ├── auth/        ← Login, recuperar password
│   ├── vendas/      ← Nova venda, fatura, histórico
│   ├── cardapio/    ← Gestão do cardápio (Gerente)
│   └── funcionarios/← Gestão de funcionários (Gerente)
└── static/
    ├── css/style.css
    └── js/venda.js
```
