package br.com.pasteldahora.employee.application.port.out;

public interface PasswordHashPort {

    String hash(String rawPassword);
}
