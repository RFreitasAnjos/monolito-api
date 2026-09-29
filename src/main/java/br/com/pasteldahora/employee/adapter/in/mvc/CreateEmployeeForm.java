package br.com.pasteldahora.employee.adapter.in.mvc;

import br.com.pasteldahora.employee.domain.model.EmployeeAccessRole;
import br.com.pasteldahora.employee.domain.model.EmployeePosition;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public class CreateEmployeeForm {

    @NotBlank
    @Size(max = 120)
    private String name;

    @NotBlank
    @CPF
    private String cpf;

    @NotBlank
    @Email
    @Size(max = 160)
    private String email;

    @NotBlank
    @Size(min = 10, max = 72)
    private String password;

    @NotNull
    private EmployeePosition position;

    @NotNull
    private EmployeeAccessRole accessRole;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public EmployeePosition getPosition() {
        return position;
    }

    public void setPosition(EmployeePosition position) {
        this.position = position;
    }

    public EmployeeAccessRole getAccessRole() {
        return accessRole;
    }

    public void setAccessRole(EmployeeAccessRole accessRole) {
        this.accessRole = accessRole;
    }
}
