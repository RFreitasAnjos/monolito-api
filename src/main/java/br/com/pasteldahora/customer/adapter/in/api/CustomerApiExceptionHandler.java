package br.com.pasteldahora.customer.adapter.in.api;

import br.com.pasteldahora.customer.domain.exception.CustomerAccessChannelUnavailableException;
import br.com.pasteldahora.customer.domain.exception.CustomerNotFoundException;
import br.com.pasteldahora.customer.domain.exception.DuplicateCustomerEmailException;
import br.com.pasteldahora.customer.domain.exception.InvalidCustomerAccessCodeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = CustomerController.class)
public class CustomerApiExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    ProblemDetail handleNotFound(CustomerNotFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, "Cliente não encontrado", exception.getMessage());
    }

    @ExceptionHandler(DuplicateCustomerEmailException.class)
    ProblemDetail handleConflict(RuntimeException exception) {
        return problem(HttpStatus.CONFLICT, "Cliente já cadastrado", exception.getMessage());
    }

    @ExceptionHandler(InvalidCustomerAccessCodeException.class)
    ProblemDetail handleInvalidAccessCode(InvalidCustomerAccessCodeException exception) {
        return problem(HttpStatus.UNAUTHORIZED, "Código de acesso inválido", exception.getMessage());
    }

    @ExceptionHandler(CustomerAccessChannelUnavailableException.class)
    ProblemDetail handleUnavailableChannel(CustomerAccessChannelUnavailableException exception) {
        return problem(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Canal de envio indisponível",
                exception.getMessage()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleInvalidDomainData(IllegalArgumentException exception) {
        return problem(HttpStatus.BAD_REQUEST, "Dados inválidos", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException exception) {
        String detail = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("A requisição possui campos inválidos.");
        return problem(HttpStatus.BAD_REQUEST, "Requisição inválida", detail);
    }

    private static ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        return problem;
    }
}
