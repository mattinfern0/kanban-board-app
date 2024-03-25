package io.mattinfern0.kanbanboardapi.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mattinfern0.kanbanboardapi.core.exceptions.ResourceNotFoundException;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        HttpHeaders headers = new HttpHeaders();
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        return this.handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {

        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        List<ObjectError> globalErrors = result.getGlobalErrors();

        List<FieldErrorSummary> errorItems = new ArrayList<>();
        fieldErrors.forEach((fieldError) -> {
            errorItems.add(new FieldErrorSummary(fieldError));
        });

        List<NonFieldErrorSummary> globalErrorSummaries = globalErrors
                .stream()
                .map(NonFieldErrorSummary::new)
                .toList();

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Bad Request");
        problemDetail.setProperty("globalErrors", globalErrorSummaries);
        problemDetail.setProperty("fieldErrors", errorItems);
        return this.handleExceptionInternal(ex, problemDetail, headers, status, request);
    }
}

class FieldErrorSummary {
    @JsonProperty("field")
    String field;

    @JsonProperty("code")
    String code;

    @JsonProperty("message")
    String message;

    public FieldErrorSummary(String field, String code, String message) {
        this.field = field;
        this.code = code;
        this.message = message;
    }

    public FieldErrorSummary(FieldError error) {
        this(error.getField(), error.getCode(), error.getDefaultMessage());
    }
}

class NonFieldErrorSummary {
    @JsonProperty("code")
    String code;

    @JsonProperty("message")
    String message;

    public NonFieldErrorSummary(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public NonFieldErrorSummary(ObjectError error) {
        this(error.getCode(), error.getDefaultMessage());
    }
}
