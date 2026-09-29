package br.com.pasteldahora;

import br.com.pasteldahora.catalog.application.port.in.CategoryUseCase;
import br.com.pasteldahora.catalog.application.port.in.CreateCategoryCommand;
import br.com.pasteldahora.catalog.application.port.in.CreateIngredientCommand;
import br.com.pasteldahora.catalog.application.port.in.CreateProductCommand;
import br.com.pasteldahora.catalog.application.port.in.IngredientUseCase;
import br.com.pasteldahora.catalog.application.port.in.ProductSearchQuery;
import br.com.pasteldahora.catalog.application.port.in.ProductUseCase;
import br.com.pasteldahora.catalog.domain.model.InventoryPolicy;
import br.com.pasteldahora.catalog.domain.model.ProductIngredient;
import br.com.pasteldahora.catalog.domain.model.UnitOfMeasure;
import br.com.pasteldahora.employee.application.port.out.EmployeeRepositoryPort;
import br.com.pasteldahora.customer.application.port.in.CustomerUseCase;
import br.com.pasteldahora.customer.application.port.out.CustomerAuthenticationGeneratorPort;
import br.com.pasteldahora.inventory.application.port.in.InventoryUseCase;
import br.com.pasteldahora.order.application.port.in.OrderUseCase;
import br.com.pasteldahora.order.domain.model.OrderStatus;
import br.com.pasteldahora.reporting.application.port.in.ManagementReportQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc
@Import(MvcApiPastelDaHoraApplicationTests.CustomerAuthenticationTestConfig.class)
@SpringBootTest(properties = {
        "app.employee.bootstrap.name=Administrador Teste",
        "app.employee.bootstrap.cpf=52998224725",
        "app.employee.bootstrap.email=admin@test.local",
        "app.employee.bootstrap.password=TestPassword@123",
        "app.notification.email.enabled=false",
        "mercadopago.access-token=TEST-token-for-context-only"
})
class MvcApiPastelDaHoraApplicationTests {

    private static final String CUSTOMER_ACCESS_CODE = "123456";
    private static final String CUSTOMER_ACCESS_TOKEN = "test-customer-access-token";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepositoryPort employeeRepository;

    @Autowired
    private CategoryUseCase categoryUseCase;

    @Autowired
    private ProductUseCase productUseCase;

    @Autowired
    private IngredientUseCase ingredientUseCase;

    @Autowired
    private InventoryUseCase inventoryUseCase;

    @Autowired
    private OrderUseCase orderUseCase;

    @Autowired
    private ManagementReportQuery managementReportQuery;

    @Autowired
    private CustomerUseCase customerUseCase;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldExposeOnlyPublicSiteAndEmployeeMvcLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("/employee/login")
                )))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("/customer/login")
                )));

        mockMvc.perform(get("/employee/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee/login"));

        mockMvc.perform(get("/customer/login"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRedirectAnonymousEmployeeToEmployeeLogin() throws Exception {
        mockMvc.perform(get("/employee/dashboard"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/employee/login"));
    }

    @Test
    void shouldAuthenticateEmployeeUsingDedicatedMvcLogin() throws Exception {
        mockMvc.perform(post("/employee/login")
                        .param("username", "admin@test.local")
                        .param("password", "TestPassword@123")
                        .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/employee/dashboard"));
    }

    @Test
    void shouldRenderEmployeeManagementForManager() throws Exception {
        mockMvc.perform(get("/employee/employees")
                        .with(user("manager@test.local").roles("MANAGER")))
                .andExpect(status().isOk())
                .andExpect(view().name("employee/list"));
    }

    @Test
    void shouldManageCompleteEmployeeLifecycleThroughMvc() throws Exception {
        mockMvc.perform(get("/employee/employees/new")
                        .with(user("admin@test.local").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("employee/create"));

        mockMvc.perform(post("/employee/employees")
                        .with(user("admin@test.local").roles("ADMIN"))
                        .with(csrf())
                        .param("name", "Ana Oliveira")
                        .param("cpf", "11144477735")
                        .param("email", "ana@pasteldahora.com")
                        .param("password", "AnaPassword@123")
                        .param("position", "COOK")
                        .param("accessRole", "OPERATOR"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/employee/employees"));

        var employee = employeeRepository.findByEmail("ana@pasteldahora.com").orElseThrow();

        mockMvc.perform(post("/employee/employees/" + employee.getId())
                        .with(user("admin@test.local").roles("ADMIN"))
                        .with(csrf())
                        .param("name", "Ana Oliveira Atualizada")
                        .param("email", "ana@pasteldahora.com")
                        .param("password", "")
                        .param("position", "MANAGER")
                        .param("accessRole", "MANAGER"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/employee/employees"));

        employee = employeeRepository.findByEmail("ana@pasteldahora.com").orElseThrow();
        assertEquals("Ana Oliveira Atualizada", employee.getName());
        assertEquals("MANAGER", employee.getPosition().name());

        mockMvc.perform(post("/employee/employees/" + employee.getId() + "/deactivate")
                        .with(user("admin@test.local").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/employee/employees"));

        assertFalse(employeeRepository.findByEmail("ana@pasteldahora.com").orElseThrow().isActive());

        mockMvc.perform(post("/employee/employees/" + employee.getId() + "/reactivate")
                        .with(user("admin@test.local").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/employee/employees"));

        assertTrue(employeeRepository.findByEmail("ana@pasteldahora.com").orElseThrow().isActive());
    }

    @Test
    void shouldPreventAdministratorFromDeactivatingOwnAccountThroughMvc() throws Exception {
        var administrator = employeeRepository.findByEmail("admin@test.local").orElseThrow();

        mockMvc.perform(post("/employee/employees/" + administrator.getId() + "/deactivate")
                        .with(user("admin@test.local").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/employee/employees"))
                .andExpect(flash().attribute(
                        "errorMessage",
                        "Não é permitido desativar a própria conta."
                ));

        assertTrue(employeeRepository.findByEmail("admin@test.local").orElseThrow().isActive());
    }

    @Test
    void shouldProtectAdministrativeDocumentation() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/employee/login"));
    }

    @Test
    void shouldForbidOperatorFromEmployeeManagement() throws Exception {
        mockMvc.perform(get("/employee/employees")
                        .with(user("operator@test.local").roles("OPERATOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldManageCatalogLifecycleThroughMvc() throws Exception {
        String suffix = java.util.UUID.randomUUID().toString().substring(0, 8);
        String categoryName = "Bebidas " + suffix;
        String sku = "REFRI-" + suffix.toUpperCase();

        mockMvc.perform(post("/employee/catalog/categories")
                        .with(user("manager@test.local").roles("MANAGER"))
                        .with(csrf())
                        .param("name", categoryName))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/employee/catalog/categories"));

        var category = categoryUseCase.findAll().stream()
                .filter(item -> categoryName.equals(item.getName()))
                .findFirst()
                .orElseThrow();

        mockMvc.perform(post("/employee/catalog/products")
                        .with(user("manager@test.local").roles("MANAGER"))
                        .with(csrf())
                        .param("sku", sku)
                        .param("name", "Refrigerante em lata")
                        .param("description", "Produto de revenda")
                        .param("categoryId", category.getId().toString())
                        .param("salePrice", "6.50")
                        .param("inventoryPolicy", "DIRECT_STOCK"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/employee/catalog/products"));

        var product = productUseCase.search(
                new ProductSearchQuery(
                        sku,
                        null,
                        null,
                        null,
                        null,
                        br.com.pasteldahora.catalog.application.port.in.ProductSort.NAME_ASC,
                        0,
                        10
                )
        ).content().stream().findFirst().orElseThrow();

        mockMvc.perform(post("/employee/catalog/products/" + product.getId())
                        .with(user("manager@test.local").roles("MANAGER"))
                        .with(csrf())
                        .param("sku", sku)
                        .param("name", "Refrigerante em lata atualizado")
                        .param("description", "Produto de revenda atualizado")
                        .param("categoryId", category.getId().toString())
                        .param("salePrice", "7.00")
                        .param("inventoryPolicy", "DIRECT_STOCK"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/employee/catalog/products"));

        assertEquals(
                "Refrigerante em lata atualizado",
                productUseCase.findById(product.getId()).getName()
        );
        mockMvc.perform(get("/employee/catalog/products")
                        .with(user("operator@test.local").roles("OPERATOR")))
                .andExpect(status().isOk())
                .andExpect(view().name("catalog/list-products"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(sku)));

        mockMvc.perform(post("/employee/catalog/categories/" + category.getId() + "/deactivate")
                        .with(user("manager@test.local").roles("MANAGER"))
                        .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(flash().attribute(
                        "errorMessage",
                        "Não é possível desativar uma categoria que possui produtos ativos."
                ));

        assertTrue(categoryUseCase.findById(category.getId()).isActive());

        mockMvc.perform(post("/employee/catalog/products/" + product.getId() + "/deactivate")
                        .with(user("manager@test.local").roles("MANAGER"))
                        .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/employee/catalog/products"));

        assertFalse(productUseCase.findById(product.getId()).isActive());

        mockMvc.perform(post("/employee/catalog/categories/" + category.getId() + "/deactivate")
                        .with(user("manager@test.local").roles("MANAGER"))
                        .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/employee/catalog/categories"));

        assertFalse(categoryUseCase.findById(category.getId()).isActive());

        mockMvc.perform(post("/employee/catalog/products/" + product.getId() + "/reactivate")
                        .with(user("manager@test.local").roles("MANAGER"))
                        .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(flash().attribute("errorMessage", "A categoria informada está inativa."));

        assertFalse(productUseCase.findById(product.getId()).isActive());
    }

    @Test
    void shouldProtectCatalogManagement() throws Exception {
        mockMvc.perform(get("/employee/catalog/products"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/employee/login"));

        mockMvc.perform(get("/employee/catalog/products/new")
                        .with(user("operator@test.local").roles("OPERATOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldIntegrateCatalogWithInventoryThroughMvc() throws Exception {
        String suffix = java.util.UUID.randomUUID().toString().substring(0, 8);
        var category = categoryUseCase.create(new CreateCategoryCommand(
                "Estoque " + suffix,
                "manager@test.local"
        ));
        var product = productUseCase.create(new CreateProductCommand(
                "EST-" + suffix.toUpperCase(),
                "Produto de estoque " + suffix,
                "Produto para integração",
                category.getId(),
                new java.math.BigDecimal("9.90"),
                InventoryPolicy.DIRECT_STOCK,
                false,
                java.util.List.of(),
                "manager@test.local"
        ));

        mockMvc.perform(post("/employee/inventory")
                        .with(user("manager@test.local").roles("MANAGER"))
                        .with(csrf())
                        .param("itemReference", "PRODUCT:" + product.getId())
                        .param("minimumQuantity", "5"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/employee/inventory"));

        var stockItem = inventoryUseCase.findAll().stream()
                .filter(item -> product.getId().equals(item.itemId()))
                .findFirst()
                .orElseThrow();

        mockMvc.perform(post("/employee/inventory/" + stockItem.stockItemId() + "/movements")
                        .with(user("manager@test.local").roles("MANAGER"))
                        .with(csrf())
                        .param("type", "PURCHASE_ENTRY")
                        .param("quantity", "10")
                        .param("unitPrice", "5.00")
                        .param("reason", "Compra inicial"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(
                        "/employee/inventory/" + stockItem.stockItemId()
                ));

        mockMvc.perform(post("/employee/inventory/" + stockItem.stockItemId() + "/movements")
                        .with(user("manager@test.local").roles("MANAGER"))
                        .with(csrf())
                        .param("type", "MANUAL_EXIT")
                        .param("quantity", "3")
                        .param("reason", "Ajuste de saída"))
                .andExpect(status().isFound());

        assertEquals(
                new java.math.BigDecimal("7.000"),
                inventoryUseCase.findById(stockItem.stockItemId()).currentQuantity()
        );
        assertEquals(2, inventoryUseCase.findMovements(stockItem.stockItemId()).size());

        mockMvc.perform(post("/employee/inventory/" + stockItem.stockItemId() + "/movements")
                        .with(user("manager@test.local").roles("MANAGER"))
                        .with(csrf())
                        .param("type", "LOSS_EXIT")
                        .param("quantity", "8")
                        .param("reason", "Perda"))
                .andExpect(status().isOk())
                .andExpect(view().name("inventory/details"))
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("Estoque insuficiente")
                ));

        assertEquals(
                new java.math.BigDecimal("7.000"),
                inventoryUseCase.findById(stockItem.stockItemId()).currentQuantity()
        );

        mockMvc.perform(get("/employee/inventory")
                        .with(user("operator@test.local").roles("OPERATOR")))
                .andExpect(status().isOk())
                .andExpect(view().name("inventory/list"))
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(product.getSku())
                ));
    }

    @Test
    void shouldCompleteOrderAndRegisterSaleInInventory() throws Exception {
        String suffix = java.util.UUID.randomUUID().toString().substring(0, 8);
        var category = categoryUseCase.create(new CreateCategoryCommand(
                "Pedidos " + suffix,
                "manager@test.local"
        ));
        var product = productUseCase.create(new CreateProductCommand(
                "PED-" + suffix.toUpperCase(),
                "Produto para pedido " + suffix,
                "Produto vendido pelo módulo Order",
                category.getId(),
                new java.math.BigDecimal("12.00"),
                InventoryPolicy.DIRECT_STOCK,
                false,
                java.util.List.of(),
                "manager@test.local"
        ));
        var stockItem = inventoryUseCase.createStockItem(
                new br.com.pasteldahora.inventory.application.port.in.CreateStockItemCommand(
                        product.getId(),
                        br.com.pasteldahora.catalog.domain.model.CatalogItemType.PRODUCT,
                        java.math.BigDecimal.ZERO,
                        "manager@test.local"
                )
        );
        inventoryUseCase.registerMovement(
                new br.com.pasteldahora.inventory.application.port.in.RegisterStockMovementCommand(
                        stockItem.stockItemId(),
                        br.com.pasteldahora.inventory.domain.model.StockMovementType.PURCHASE_ENTRY,
                        new java.math.BigDecimal("5"),
                        new java.math.BigDecimal("6.00"),
                        "Compra para teste",
                        "manager@test.local"
                )
        );
        var salesRevenueBefore = managementReportQuery.getDashboard().salesRevenue();

        String redirect = mockMvc.perform(post("/employee/orders")
                        .with(user("operator@test.local").roles("OPERATOR"))
                        .with(csrf()))
                .andExpect(status().isFound())
                .andReturn()
                .getResponse()
                .getRedirectedUrl();
        java.util.UUID orderId = java.util.UUID.fromString(
                redirect.substring(redirect.lastIndexOf('/') + 1)
        );

        mockMvc.perform(post("/employee/orders/" + orderId + "/items")
                        .with(user("operator@test.local").roles("OPERATOR"))
                        .with(csrf())
                        .param("productId", product.getId().toString())
                        .param("quantity", "2"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/employee/orders/" + orderId));

        mockMvc.perform(post("/employee/orders/" + orderId + "/complete")
                        .with(user("operator@test.local").roles("OPERATOR"))
                        .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/employee/orders/" + orderId));

        var completedOrder = orderUseCase.findById(orderId);
        assertEquals(OrderStatus.COMPLETED, completedOrder.getStatus());
        assertEquals(new java.math.BigDecimal("24.00"), completedOrder.getTotalAmount());
        assertEquals(
                new java.math.BigDecimal("3.000"),
                inventoryUseCase.findById(stockItem.stockItemId()).currentQuantity()
        );
        var sale = inventoryUseCase.findMovements(stockItem.stockItemId()).stream()
                .filter(movement ->
                        movement.getType()
                                == br.com.pasteldahora.inventory.domain.model.StockMovementType.SALE_EXIT
                )
                .findFirst()
                .orElseThrow();
        assertEquals(new java.math.BigDecimal("24.00"), sale.getTotalValue());
        assertEquals(new java.math.BigDecimal("12.00"), sale.getTotalCost());
        assertEquals(
                new java.math.BigDecimal("24.00"),
                managementReportQuery.getDashboard()
                        .salesRevenue()
                        .subtract(salesRevenueBefore)
        );

        mockMvc.perform(get("/employee/orders/" + orderId)
                        .with(user("operator@test.local").roles("OPERATOR")))
                .andExpect(status().isOk())
                .andExpect(view().name("order/details"))
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("Produto para pedido")
                ));
    }

    @Test
    void shouldProtectOrdersFromAnonymousAccess() throws Exception {
        mockMvc.perform(get("/employee/orders"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/employee/login"));
    }

    @Test
    void shouldExposePublicCatalogWithFiltersAndOnlyActiveProducts() throws Exception {
        String suffix = java.util.UUID.randomUUID().toString().substring(0, 8);
        var category = categoryUseCase.create(new CreateCategoryCommand(
                "Categoria API " + suffix,
                "manager@test.local"
        ));
        var cheaperProduct = productUseCase.create(new CreateProductCommand(
                "API-" + suffix + "-01",
                "Pastel Especial " + suffix,
                "Pastel preparado para o teste da API pública.",
                category.getId(),
                new java.math.BigDecimal("12.50"),
                InventoryPolicy.NOT_CONTROLLED,
                false,
                java.util.List.of(),
                "manager@test.local"
        ));
        var expensiveProduct = productUseCase.create(new CreateProductCommand(
                "API-" + suffix + "-02",
                "Combo Especial " + suffix,
                "Combo preparado para o teste da API pública.",
                category.getId(),
                new java.math.BigDecimal("25.00"),
                InventoryPolicy.NOT_CONTROLLED,
                false,
                java.util.List.of(),
                "manager@test.local"
        ));
        var inactiveProduct = productUseCase.create(new CreateProductCommand(
                "API-" + suffix + "-03",
                "Produto Inativo " + suffix,
                null,
                category.getId(),
                new java.math.BigDecimal("18.00"),
                InventoryPolicy.NOT_CONTROLLED,
                false,
                java.util.List.of(),
                "manager@test.local"
        ));
        productUseCase.deactivate(inactiveProduct.getId(), "manager@test.local");

        mockMvc.perform(get("/api/catalog/products")
                        .param("name", "especial " + suffix)
                        .param("categoryId", category.getId().toString())
                        .param("minPrice", "10.00")
                        .param("maxPrice", "30.00")
                        .param("sort", "PRICE_DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].id")
                        .value(expensiveProduct.getId().toString()))
                .andExpect(jsonPath("$.content[0].price").value(25.00))
                .andExpect(jsonPath("$.content[0].currency").value("BRL"))
                .andExpect(jsonPath("$.content[0].category.id")
                        .value(category.getId().toString()))
                .andExpect(jsonPath("$.content[0].active").doesNotExist())
                .andExpect(jsonPath("$.content[0].createdBy").doesNotExist())
                .andExpect(jsonPath("$.content[1].id")
                        .value(cheaperProduct.getId().toString()));

        mockMvc.perform(get("/api/catalog/products/" + cheaperProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(cheaperProduct.getName()))
                .andExpect(jsonPath("$.description").value(cheaperProduct.getDescription()));

        mockMvc.perform(get("/api/catalog/products/" + inactiveProduct.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Item do catálogo não encontrado"));

        mockMvc.perform(get("/api/catalog/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id").value(
                        org.hamcrest.Matchers.hasItem(category.getId().toString())
                ));

        mockMvc.perform(get("/api/catalog/categories/" + category.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(category.getName()));

        mockMvc.perform(get("/api/catalog/products")
                        .param("minPrice", "30.00")
                        .param("maxPrice", "10.00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Filtro inválido"));
    }

    @Test
    void shouldExposeCustomizableIngredientsAndControlTheirStock() throws Exception {
        String suffix = java.util.UUID.randomUUID().toString().substring(0, 8);
        var ingredient = ingredientUseCase.create(new CreateIngredientCommand(
                "ING-" + suffix.toUpperCase(),
                "Queijo " + suffix,
                "Ingrediente opcional",
                UnitOfMeasure.GRAM,
                "manager@test.local"
        ));
        var category = categoryUseCase.create(new CreateCategoryCommand(
                "Customizáveis " + suffix,
                "manager@test.local"
        ));
        var product = productUseCase.create(new CreateProductCommand(
                "CUS-" + suffix.toUpperCase(),
                "Pastel customizável " + suffix,
                "Cliente seleciona os ingredientes.",
                category.getId(),
                new java.math.BigDecimal("14.00"),
                InventoryPolicy.RECIPE_BASED,
                true,
                java.util.List.of(new ProductIngredient(
                        ingredient.getId(),
                        new java.math.BigDecimal("50.000")
                )),
                "manager@test.local"
        ));

        mockMvc.perform(get("/api/catalog/products/" + product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customizable").value(true))
                .andExpect(jsonPath("$.ingredients[0].id")
                        .value(ingredient.getId().toString()))
                .andExpect(jsonPath("$.ingredients[0].quantity").value(50.0))
                .andExpect(jsonPath("$.ingredients[0].includedByDefault").value(false))
                .andExpect(jsonPath("$.ingredients[0].customerSelectable").value(true));

        var stockItem = inventoryUseCase.createStockItem(
                new br.com.pasteldahora.inventory.application.port.in.CreateStockItemCommand(
                        ingredient.getId(),
                        br.com.pasteldahora.catalog.domain.model.CatalogItemType.INGREDIENT,
                        new java.math.BigDecimal("100.000"),
                        "manager@test.local"
                )
        );
        assertEquals(ingredient.getId(), stockItem.itemId());
        assertEquals(
                br.com.pasteldahora.catalog.domain.model.CatalogItemType.INGREDIENT,
                stockItem.itemType()
        );

        assertThrows(
                br.com.pasteldahora.catalog.domain.exception.ActiveProductsUsingIngredientException.class,
                () -> ingredientUseCase.deactivate(ingredient.getId(), "manager@test.local")
        );
    }

    @Test
    void shouldManageCustomerLifecycleThroughApi() throws Exception {
        String suffix = java.util.UUID.randomUUID().toString().substring(0, 8);
        String email = "customer." + suffix + "@example.com";

        mockMvc.perform(post("/api/customers")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Cliente API",
                                  "email": "%s",
                                  "phone": "(11) 99999-9999"
                                }
                                """.formatted(email)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.cpf").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist());

        var customer = customerUseCase.findAll().stream()
                .filter(item -> email.equals(item.getEmail()))
                .findFirst()
                .orElseThrow();
        mockMvc.perform(get("/api/customers/" + customer.getId()))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/customers/" + customer.getId())
                        .with(user("operator@test.local").roles("OPERATOR")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/customers/" + customer.getId())
                        .with(user("manager@test.local").roles("MANAGER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));

        mockMvc.perform(post("/api/customers/auth/code")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "channel": "EMAIL"
                                }
                                """.formatted(email)))
                .andExpect(status().isAccepted());

        mockMvc.perform(post("/api/customers/auth/verify")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "code": "000000"
                                }
                                """.formatted(email)))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/customers/auth/verify")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "code": "%s"
                                }
                                """.formatted(email, CUSTOMER_ACCESS_CODE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(CUSTOMER_ACCESS_TOKEN))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.customer.email").value(email));

        mockMvc.perform(get("/api/customers/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + CUSTOMER_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));

        String updatedEmail = "updated." + suffix + "@example.com";
        mockMvc.perform(put("/api/customers/" + customer.getId())
                        .with(user("manager@test.local").roles("MANAGER"))
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Cliente Atualizado",
                                  "email": "%s",
                                  "phone": "11988888888"
                                }
                                """.formatted(updatedEmail)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cliente Atualizado"))
                .andExpect(jsonPath("$.email").value(updatedEmail));

        mockMvc.perform(patch("/api/customers/" + customer.getId() + "/deactivate")
                        .with(user("manager@test.local").roles("MANAGER")))
                .andExpect(status().isNoContent());
        assertFalse(customerUseCase.findById(customer.getId()).isActive());

        mockMvc.perform(patch("/api/customers/" + customer.getId() + "/reactivate")
                        .with(user("manager@test.local").roles("MANAGER")))
                .andExpect(status().isNoContent());
        assertTrue(customerUseCase.findById(customer.getId()).isActive());

        mockMvc.perform(post("/api/customers/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + CUSTOMER_ACCESS_TOKEN))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/customers/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + CUSTOMER_ACCESS_TOKEN))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnApiProblemForInvalidCustomerRegistration() throws Exception {
        mockMvc.perform(post("/api/customers")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "email": "invalid",
                                  "phone": "1"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Requisição inválida"));
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class CustomerAuthenticationTestConfig {

        @Bean
        @Primary
        CustomerAuthenticationGeneratorPort customerAuthenticationGeneratorPort() {
            return new CustomerAuthenticationGeneratorPort() {
                @Override
                public String generateCode() {
                    return CUSTOMER_ACCESS_CODE;
                }

                @Override
                public String generateToken() {
                    return CUSTOMER_ACCESS_TOKEN;
                }
            };
        }
    }

    @Test
    void shouldProtectInventoryManagement() throws Exception {
        mockMvc.perform(get("/employee/inventory"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/employee/login"));

        mockMvc.perform(get("/employee/inventory/new")
                        .with(user("operator@test.local").roles("OPERATOR")))
                .andExpect(status().isForbidden());
    }
}
