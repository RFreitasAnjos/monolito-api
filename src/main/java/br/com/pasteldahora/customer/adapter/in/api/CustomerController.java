package br.com.pasteldahora.customer.adapter.in.api;

import br.com.pasteldahora.customer.application.port.in.CreateCustomerCommand;
import br.com.pasteldahora.customer.application.port.in.CustomerAuthenticationUseCase;
import br.com.pasteldahora.customer.application.port.in.CustomerUseCase;
import br.com.pasteldahora.customer.application.port.in.RequestCustomerAccessCodeCommand;
import br.com.pasteldahora.customer.application.port.in.UpdateCustomerCommand;
import br.com.pasteldahora.customer.application.port.in.VerifyCustomerAccessCodeCommand;
import br.com.pasteldahora.customer.domain.model.Customer;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerUseCase customerUseCase;
    private final CustomerAuthenticationUseCase authenticationUseCase;

    public CustomerController(
            CustomerUseCase customerUseCase,
            CustomerAuthenticationUseCase authenticationUseCase
    ) {
        this.customerUseCase = customerUseCase;
        this.authenticationUseCase = authenticationUseCase;
    }

    @PostMapping("/auth/code")
    public ResponseEntity<Void> requestAccessCode(
            @Valid @RequestBody RequestAccessCodeRequest request
    ) {
        authenticationUseCase.requestAccessCode(new RequestCustomerAccessCodeCommand(
                request.email(),
                request.channel()
        ));
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/auth/verify")
    public CustomerAuthenticationResponse verifyAccessCode(
            @Valid @RequestBody VerifyAccessCodeRequest request
    ) {
        return CustomerAuthenticationResponse.from(
                authenticationUseCase.verifyAccessCode(new VerifyCustomerAccessCodeCommand(
                        request.email(),
                        request.code()
                ))
        );
    }

    @GetMapping("/me")
    public CustomerResponse me(@AuthenticationPrincipal Customer customer) {
        return CustomerResponse.from(customer);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        authenticationUseCase.logout(authorization.substring("Bearer ".length()).trim());
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(
            @Valid @RequestBody CreateCustomerRequest request
    ) {
        var customer = customerUseCase.create(new CreateCustomerCommand(
                request.name(),
                request.email(),
                request.phone()
        ));
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(customer.getId())
                .toUri();
        return ResponseEntity.created(location).body(CustomerResponse.from(customer));
    }

    @GetMapping
    public List<CustomerResponse> findAll() {
        return customerUseCase.findAll().stream()
                .map(CustomerResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public CustomerResponse findById(@PathVariable UUID id) {
        return CustomerResponse.from(customerUseCase.findById(id));
    }

    @PutMapping("/{id}")
    public CustomerResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomerRequest request,
            Authentication authentication
    ) {
        return CustomerResponse.from(customerUseCase.update(new UpdateCustomerCommand(
                id,
                request.name(),
                request.email(),
                request.phone(),
                authentication.getName()
        )));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        customerUseCase.deactivate(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<Void> reactivate(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        customerUseCase.reactivate(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
