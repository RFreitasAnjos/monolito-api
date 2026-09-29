package br.com.pasteldahora.employee.adapter.in.mvc;

import br.com.pasteldahora.employee.application.port.in.CreateEmployeeCommand;
import br.com.pasteldahora.employee.application.port.in.EmployeeSearchQuery;
import br.com.pasteldahora.employee.application.port.in.EmployeeUseCase;
import br.com.pasteldahora.employee.application.port.in.UpdateEmployeeCommand;
import br.com.pasteldahora.employee.domain.exception.DuplicateEmployeeCpfException;
import br.com.pasteldahora.employee.domain.exception.DuplicateEmployeeEmailException;
import br.com.pasteldahora.employee.domain.exception.SelfDeactivationNotAllowedException;
import br.com.pasteldahora.employee.domain.model.Employee;
import br.com.pasteldahora.employee.domain.model.EmployeeAccessRole;
import br.com.pasteldahora.employee.domain.model.EmployeePosition;
import br.com.pasteldahora.reporting.application.port.in.ManagementReportQuery;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/employee")
public class EmployeeMvcController {

    private final EmployeeUseCase employeeUseCase;
    private final ManagementReportQuery managementReportQuery;

    public EmployeeMvcController(
            EmployeeUseCase employeeUseCase,
            ManagementReportQuery managementReportQuery
    ) {
        this.employeeUseCase = employeeUseCase;
        this.managementReportQuery = managementReportQuery;
    }

    @GetMapping("/login")
    public String login() {
        return "employee/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("report", managementReportQuery.getDashboard());
        return "employee/dashboard";
    }

    @GetMapping("/employees")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String list(
            @RequestParam(defaultValue = "") String term,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            Authentication authentication,
            Model model
    ) {
        model.addAttribute("employees", employeeUseCase.search(
                new EmployeeSearchQuery(term, active, page, 10)
        ));
        model.addAttribute("term", term);
        model.addAttribute("activeFilter", active);
        model.addAttribute("canManage", authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority())));
        return "employee/list";
    }

    @GetMapping("/employees/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newEmployee(Model model) {
        model.addAttribute("form", new CreateEmployeeForm());
        addOptions(model);
        return "employee/create";
    }

    @PostMapping("/employees")
    @PreAuthorize("hasRole('ADMIN')")
    public String create(
            @Valid @ModelAttribute("form") CreateEmployeeForm form,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            addOptions(model);
            return "employee/create";
        }
        try {
            employeeUseCase.create(new CreateEmployeeCommand(
                    form.getName(),
                    form.getCpf(),
                    form.getEmail(),
                    form.getPassword(),
                    form.getPosition(),
                    form.getAccessRole(),
                    authentication.getName()
            ));
        } catch (DuplicateEmployeeCpfException | DuplicateEmployeeEmailException exception) {
            bindingResult.reject("employee.duplicate", exception.getMessage());
            addOptions(model);
            return "employee/create";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Funcionário cadastrado com sucesso.");
        return "redirect:/employee/employees";
    }

    @GetMapping("/employees/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String edit(@PathVariable UUID id, Model model) {
        Employee employee = employeeUseCase.findById(id);
        model.addAttribute("employee", employee);
        model.addAttribute("form", UpdateEmployeeForm.from(employee));
        addOptions(model);
        return "employee/edit";
    }

    @PostMapping("/employees/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(
            @PathVariable UUID id,
            @Valid @ModelAttribute("form") UpdateEmployeeForm form,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("employee", employeeUseCase.findById(id));
            addOptions(model);
            return "employee/edit";
        }
        try {
            employeeUseCase.update(new UpdateEmployeeCommand(
                    id,
                    form.getName(),
                    form.getEmail(),
                    form.getPosition(),
                    form.getAccessRole(),
                    form.getPassword(),
                    authentication.getName()
            ));
        } catch (DuplicateEmployeeEmailException exception) {
            bindingResult.rejectValue("email", "employee.email.duplicate", exception.getMessage());
            model.addAttribute("employee", employeeUseCase.findById(id));
            addOptions(model);
            return "employee/edit";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Funcionário atualizado com sucesso.");
        return "redirect:/employee/employees";
    }

    @PostMapping("/employees/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public String deactivate(
            @PathVariable UUID id,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            employeeUseCase.deactivate(id, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Funcionário desativado.");
        } catch (SelfDeactivationNotAllowedException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/employee/employees";
    }

    @PostMapping("/employees/{id}/reactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public String reactivate(
            @PathVariable UUID id,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        employeeUseCase.reactivate(id, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Funcionário reativado.");
        return "redirect:/employee/employees";
    }

    private static void addOptions(Model model) {
        model.addAttribute("positions", EmployeePosition.values());
        model.addAttribute("accessRoles", EmployeeAccessRole.values());
    }
}
