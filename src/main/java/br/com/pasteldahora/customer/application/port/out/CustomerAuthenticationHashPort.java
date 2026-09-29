package br.com.pasteldahora.customer.application.port.out;

public interface CustomerAuthenticationHashPort {

    String hashCode(String code);

    boolean matchesCode(String code, String hash);

    String hashToken(String token);
}
