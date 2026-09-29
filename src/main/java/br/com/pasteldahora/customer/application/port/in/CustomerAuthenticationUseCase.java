package br.com.pasteldahora.customer.application.port.in;

import br.com.pasteldahora.customer.domain.model.Customer;

public interface CustomerAuthenticationUseCase {

    void requestAccessCode(RequestCustomerAccessCodeCommand command);

    CustomerAuthentication verifyAccessCode(VerifyCustomerAccessCodeCommand command);

    Customer authenticate(String accessToken);

    void logout(String accessToken);
}
