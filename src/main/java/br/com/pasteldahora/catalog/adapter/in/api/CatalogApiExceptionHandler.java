package br.com.pasteldahora.catalog.adapter.in.api;

import br.com.pasteldahora.catalog.domain.exception.CategoryNotFoundException;
import br.com.pasteldahora.catalog.domain.exception.ProductNotFoundException;
import br.com.pasteldahora.catalog.domain.exception.IngredientNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = CatalogController.class)
public class CatalogApiExceptionHandler {

    @ExceptionHandler({
            ProductNotFoundException.class,
            CategoryNotFoundException.class,
            IngredientNotFoundException.class
    })
    ProblemDetail handleNotFound(RuntimeException exception) {
        return problem(HttpStatus.NOT_FOUND, "Item do catálogo não encontrado", exception);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleInvalidFilter(IllegalArgumentException exception) {
        return problem(HttpStatus.BAD_REQUEST, "Filtro inválido", exception);
    }

    private static ProblemDetail problem(
            HttpStatus status,
            String title,
            RuntimeException exception
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                status,
                exception.getMessage()
        );
        problem.setTitle(title);
        return problem;
    }
}
