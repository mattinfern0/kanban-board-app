package io.mattinfern0.kanbanboardapi.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
    ) {

        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        List<ObjectError> globalErrors = result.getGlobalErrors();

        List<FieldErrorItem> errorItems = new ArrayList<>();
        fieldErrors.forEach((fieldError) -> {
            errorItems.add(new FieldErrorItem(fieldError));
        });

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Bad Request");
        problemDetail.setProperty("globalErrors", globalErrors);
        problemDetail.setProperty("fieldErrors", errorItems);
        return this.handleExceptionInternal(ex, problemDetail, headers, status, request);
    }
}

class FieldErrorItem {
    @JsonProperty("field")
    String field;

    @JsonProperty("message")
    String message;

    public FieldErrorItem(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public FieldErrorItem(FieldError error) {
        this(error.getField(), error.getDefaultMessage());
    }


}
