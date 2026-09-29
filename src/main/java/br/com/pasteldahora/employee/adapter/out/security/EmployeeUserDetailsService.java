package br.com.pasteldahora.employee.adapter.out.security;

import br.com.pasteldahora.employee.application.port.out.EmployeeRepositoryPort;
import br.com.pasteldahora.employee.domain.model.Employee;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("employeeUserDetailsService")
public class EmployeeUserDetailsService implements UserDetailsService {

    private final EmployeeRepositoryPort repository;

    public EmployeeUserDetailsService(EmployeeRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Employee employee = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas."));

        return User.builder()
                .username(employee.getEmail())
                .password(employee.getPasswordHash())
                .roles(employee.getAccessRole().name())
                .disabled(!employee.isActive())
                .build();
    }
}
