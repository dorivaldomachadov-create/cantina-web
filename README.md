# Sistema de Gestão de Cantina - v2.0

Sistema interno de gestão para cantina escolar/empresa.  
**Uso exclusivo de funcionários e gerente.**

**Versão atual:** 2.0 (Modelo Caixa de Supermercado)

---

## 📋 Sobre o Projeto

Este sistema foi desenvolvido para gerir uma cantina de forma **interna**. O cliente **não** interage diretamente com o software. O funcionário é quem opera o sistema (regista vendas, controla stock, etc.).

### Principais Funcionalidades
- Login com dois níveis de acesso (Funcionário e Gerente)
- Realização de vendas (modelo caixa)
- Controlo completo de estoque
- Gestão de cardápio (apenas Gerente)
- Histórico de vendas
- Relatórios (apenas Gerente)
- Sistema de segurança com Spring Security

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
- Java 17
- Maven

### Passos

1. Clone o repositório:
```bash
git clone https://github.com/SEU_USUARIO/cantina-gestao.git
cd cantina-gestao
```

2. Execute o projeto:
```bash
./mvnw spring-boot:run -DskipTests
```

3. Acesse no navegador:
   **http://localhost:8080/login**

---

## 👤 Utilizadores de Teste

| Perfil       | Utilizador     | Senha   |
|--------------|----------------|---------|
| Funcionário  | `funcionario`  | `1234`  |
| Gerente      | `gerente`      | `1234`  |

---

## 🔐 Níveis de Acesso

| Funcionalidade           | Funcionário | Gerente |
|--------------------------|-------------|---------|
| Realizar Venda           | Sim         | Sim     |
| Ver Estoque              | Sim         | Sim     |
| Gerir Cardápio           | Não         | Sim     |
| Repor Stock              | Não         | Sim     |
| Cancelar Venda           | Não         | Sim     |
| Ver Histórico Completo   | Não         | Sim     |
| Ver Relatórios           | Não         | Sim     |

---

## 🛠 Tecnologias Utilizadas

- **Backend**: Spring Boot 3.3.4
- **Frontend**: Thymeleaf + HTML5 + CSS3
- **Segurança**: Spring Security
- **Build**: Maven
- **Java**: 17

---

## 📁 Estrutura de Pastas

```
src/main/java/com/cantina/
├── config/
│   └── SecurityConfig.java
├── controller/
│   ├── AuthController.java
│   └── DashboardController.java
├── modelo/
├── repositorio/
├── servico/
└── CantinaWebApplication.java

src/main/resources/
├── static/css/style.css
└── templates/
    ├── login.html
    ├── dashboard-funcionario.html
    └── dashboard-gerente.html
```

---

## 📋 Regras do Git (Obrigatórias)

- **Nunca** trabalhar diretamente na branch `main`
- Criar branch para cada funcionalidade: `feature/nome-da-tarefa`
- Commits pequenos e com mensagens claras
- Fazer Pull Request para revisão antes de merge
- Atualizar sempre com `git pull origin main` antes de começar

---

## 👥 Equipa

- **Dorivaldo Machado** — Líder do Projeto (Setup + Security)
- Flejon — Cardápio e Estoque
- Eduardo — Venda e Fatura
- Pitra — Interface e Layout
- Elias — Relatórios e Testes

---

## 📌 Próximos Passos

- Implementação completa do módulo de Vendas
- Controlo de Estoque
- Relatórios
- Melhorias na interface

---

**Desenvolvido por Dorivaldo Machado**  
**Para uso interno da Cantina**
