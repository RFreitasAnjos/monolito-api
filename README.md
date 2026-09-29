# MVC API Pastel da Hora

Sistema modular com Spring MVC, Spring Security, Flyway e H2. O módulo
`employee` atende exclusivamente os funcionários pelo próprio backend. O futuro
módulo `customer` será acessado por um frontend Angular ou React.

## Canais de acesso

```text
Funcionário -> Spring MVC -> /employee/**
Cliente     -> Angular/React -> futura API /api/customer/**
```

Não existe página `/customer/login` no backend. O frontend dos clientes será
responsável pela tela de login e enviará as credenciais para a API de
autenticação que será criada no módulo `customer`.

## Módulo employee

```text
employee/
├── domain/
├── application/
│   ├── port/in/
│   ├── port/out/
│   └── service/
├── adapter/
│   ├── in/mvc/
│   ├── out/persistence/
│   └── out/security/
└── config/
```

O employee possui:

- login MVC por e-mail e senha;
- sessão e proteção CSRF;
- dashboard gerencial;
- criação e edição de funcionários;
- desativação e reativação;
- paginação e filtros;
- auditoria;
- controle otimista de concorrência;
- cargos separados de perfis de acesso;
- senhas BCrypt.

Perfis:

| Perfil | Acesso |
|---|---|
| `ADMIN` | Cadastro, edição, desativação e reativação |
| `MANAGER` | Consulta da gestão de funcionários |
| `OPERATOR` | Dashboard gerencial |

## Executar

```powershell
$env:EMPLOYEE_ADMIN_NAME="Administrador"
$env:EMPLOYEE_ADMIN_CPF="52998224725"
$env:EMPLOYEE_ADMIN_EMAIL="admin@pasteldahora.local"
$env:EMPLOYEE_ADMIN_PASSWORD="UmaSenhaForte@123"
.\mvnw.cmd spring-boot:run
```

- Login employee: http://localhost:8080/employee/login
- Dashboard: http://localhost:8080/employee/dashboard
- Funcionários: http://localhost:8080/employee/employees
- Swagger gerencial: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console

## Futuro módulo customer

O módulo customer deverá ter portas e adaptadores próprios:

```text
customer/
├── domain/
├── application/
├── adapter/in/api/
├── adapter/out/persistence/
└── config/
```

Sua autenticação deverá retornar tokens para o frontend. Ela não reutilizará:

- o formulário `/employee/login`;
- a sessão MVC dos funcionários;
- controllers ou templates Thymeleaf;
- papéis administrativos do employee.

Angular ou React ainda não foi escolhido; essa decisão não altera o contrato da
API de autenticação customer.
