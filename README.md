# MVC API Pastel da Hora

Sistema modular com Spring MVC, Spring Security, Flyway e H2. O módulo
`employee` atende exclusivamente os funcionários pelo próprio backend. O futuro
módulo `customer` será acessado por um frontend Angular ou React.

## Canais de acesso

```text
Funcionário -> Spring MVC -> /employee/**
Cliente     -> Angular/React -> API /api/customers/**
```

Não existe página `/customer/login` no backend. O frontend dos clientes solicita
um código temporário de seis dígitos por e-mail ou WhatsApp e o valida na API.

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

## Módulo catalog

O catálogo gerencial está disponível em:

- Produtos: http://localhost:8080/employee/catalog/products
- Categorias: http://localhost:8080/employee/catalog/categories
- Ingredientes: http://localhost:8080/employee/catalog/ingredients

Produtos podem possuir composição fixa ou serem customizáveis. Nos customizáveis, os
ingredientes vinculados são opções disponíveis ao cliente; nos demais, representam a
composição padrão definida pelo administrador. Ingredientes ativos também podem ser
controlados individualmente no módulo de estoque.

O módulo possui:

- cadastro, edição, busca, paginação, ativação e desativação de produtos;
- SKU único e preço monetário com duas casas decimais;
- categorias com proteção contra desativação enquanto possuírem produtos ativos;
- auditoria e controle otimista de concorrência;
- políticas `NOT_CONTROLLED`, `DIRECT_STOCK` e `RECIPE_BASED`;
- porta pública `CatalogItemQuery` para integração com o Inventory.

### API pública do catálogo

O frontend do cliente pode consultar somente produtos e categorias ativos:

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/catalog/products` | Lista paginada e filtrada |
| `GET` | `/api/catalog/products/{id}` | Detalhe de um produto |
| `GET` | `/api/catalog/categories` | Categorias disponíveis |
| `GET` | `/api/catalog/categories/{id}` | Detalhe de uma categoria |
| `GET` | `/api/catalog/ingredients` | Ingredientes ativos disponíveis |

A listagem aceita `name`, `minPrice`, `maxPrice`, `categoryId`, `page`, `size`
e `sort`. As ordenações disponíveis são `NAME_ASC`, `NAME_DESC`, `PRICE_ASC`
e `PRICE_DESC`. A API não expõe dados de auditoria nem produtos inativos.

## Módulo inventory

O estoque gerencial está disponível em:

- Estoque: http://localhost:8080/employee/inventory

O Inventory integra-se ao Catalog por `CatalogItemQuery`, sem acessar entidades
JPA ou tabelas do catálogo. A primeira etapa suporta produtos ativos com
`DIRECT_STOCK` e oferece:

- criação de um item de estoque por produto;
- saldo e estoque mínimo;
- entradas de compra com valor unitário e custo médio ponderado;
- saídas por venda com receita e CMV histórico;
- entradas e saídas manuais e registro de perdas;
- bloqueio de saldo negativo;
- histórico auditável de movimentações;
- controle otimista de concorrência.

## Módulo reporting

O dashboard gerencial consolida os dados financeiros publicados pelo Inventory,
sem acessar diretamente suas entidades ou tabelas. Atualmente apresenta:

- compras acumuladas;
- receita de vendas;
- custo da mercadoria vendida (CMV);
- resultado e margem bruta;
- valor atual do estoque pelo custo médio ponderado.

## Módulo order

O gerenciamento de pedidos está disponível em:

- Pedidos: http://localhost:8080/employee/orders

O módulo oferece:

- criação de pedidos abertos;
- inclusão e remoção de múltiplos produtos;
- preço de venda congelado no item do pedido;
- cálculo do total;
- cancelamento antes da conclusão;
- conclusão transacional com baixa automática dos produtos de estoque direto;
- geração da movimentação de venda utilizada pelo Reporting.

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

## Módulo customer

O módulo Customer é exclusivamente API-first:

```text
customer/
├── domain/
├── application/
├── adapter/in/api/
├── adapter/out/persistence/
└── config/
```

Endpoints:

| Método | Endpoint | Acesso |
|---|---|---|
| `POST` | `/api/customers` | Público |
| `POST` | `/api/customers/auth/code` | Público |
| `POST` | `/api/customers/auth/verify` | Público |
| `GET` | `/api/customers/me` | Cliente autenticado |
| `POST` | `/api/customers/auth/logout` | Cliente autenticado |
| `GET` | `/api/customers` | `ADMIN` ou `MANAGER` |
| `GET` | `/api/customers/{id}` | `ADMIN` ou `MANAGER` |
| `PUT` | `/api/customers/{id}` | `ADMIN` ou `MANAGER` |
| `PATCH` | `/api/customers/{id}/deactivate` | `ADMIN` ou `MANAGER` |
| `PATCH` | `/api/customers/{id}/reactivate` | `ADMIN` ou `MANAGER` |

O cadastro solicita somente nome, e-mail e telefone. O módulo não armazena CPF
nem senha. A autenticação usa um código de seis dígitos com validade configurável,
uso único, intervalo mínimo entre envios e limite de tentativas. Após a validação,
a API retorna um token opaco temporário para o frontend. Os códigos são
persistidos somente como hash e os tokens somente como SHA-256.

O envio por e-mail usa o SMTP do módulo Notification. O envio por WhatsApp usa
um webhook configurável por `CUSTOMER_WHATSAPP_WEBHOOK_URL`, com o corpo
`{"phone":"...","message":"..."}`. Sem webhook, a API informa que o canal está
indisponível, sem simular sucesso.

Os e-mails são enviados em formato multipart, com template HTML responsivo e
fallback em texto puro. O código de acesso informa sua expiração exata, possui
uso único e inclui orientações para que o cliente não compartilhe o código.

A autenticação customer não reutiliza:

- o formulário `/employee/login`;
- a sessão MVC dos funcionários;
- controllers ou templates Thymeleaf;
- papéis administrativos do employee.
