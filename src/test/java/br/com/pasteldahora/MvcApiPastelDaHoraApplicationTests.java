package br.com.pasteldahora;

import br.com.pasteldahora.employee.application.port.out.EmployeeRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@AutoConfigureMockMvc
@SpringBootTest(properties = {
        "app.employee.bootstrap.name=Administrador Teste",
        "app.employee.bootstrap.cpf=52998224725",
        "app.employee.bootstrap.email=admin@test.local",
        "app.employee.bootstrap.password=TestPassword@123",
        "mercadopago.access-token=TEST-token-for-context-only"
})
class MvcApiPastelDaHoraApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepositoryPort employeeRepository;

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
}
