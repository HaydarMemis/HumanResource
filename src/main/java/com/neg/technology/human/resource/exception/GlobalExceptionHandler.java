package com.neg.technology.human.resource.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MissingRequestValueException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, ServerWebExchange exchange) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, exchange.getRequest().getPath().value());
    }

    @ExceptionHandler(InvalidLeaveRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidLeaveRequest(InvalidLeaveRequestException ex, ServerWebExchange exchange) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, exchange.getRequest().getPath().value());
    }

    @ExceptionHandler(DuplicateEmployeeException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateEmployee(DuplicateEmployeeException ex, ServerWebExchange exchange) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, exchange.getRequest().getPath().value());
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Object> handleValidationErrors(WebExchangeBindException ex, ServerWebExchange exchange) {
        Map<String, String> errors = new HashMap<>();
        ex.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Doğrulama Hatası");
        body.put("details", errors);
        body.put("path", exchange.getRequest().getPath().value());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex, ServerWebExchange exchange) {
        return buildErrorResponse("Geçersiz parametre: " + ex.getMessage(), HttpStatus.BAD_REQUEST, exchange.getRequest().getPath().value());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, ServerWebExchange exchange) {
        return buildErrorResponse("Veritabanı bütünlüğü ihlali: " + ex.getMostSpecificCause().getMessage(),
                HttpStatus.CONFLICT, exchange.getRequest().getPath().value());
    }

    @ExceptionHandler(MissingRequestValueException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingRequestParam(MissingRequestValueException ex, ServerWebExchange exchange) {
        String message = "Eksik istek parametresi veya gövde değeri: " + ex.getReason();
        return buildErrorResponse(message, HttpStatus.BAD_REQUEST, exchange.getRequest().getPath().value());
    }

    @ExceptionHandler(UnsupportedMediaTypeStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleUnsupportedMediaType(UnsupportedMediaTypeStatusException ex, ServerWebExchange exchange) {
        return buildErrorResponse("Desteklenmeyen içerik türü: " + ex.getMessage(),
                HttpStatus.UNSUPPORTED_MEDIA_TYPE, exchange.getRequest().getPath().value());
    }

    @ExceptionHandler(ServerErrorException.class)
    public ResponseEntity<ApiErrorResponse> handleServerError(ServerErrorException ex, ServerWebExchange exchange) {
        log.error("Sunucu hatası oluştu", ex);
        return buildErrorResponse("Sunucu hatası: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR, exchange.getRequest().getPath().value());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex, ServerWebExchange exchange) {
        log.error("Beklenmeyen bir hata oluştu", ex);
        return buildErrorResponse("Beklenmeyen bir hata oluştu: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR, exchange.getRequest().getPath().value());
    }

    private ResponseEntity<ApiErrorResponse> buildErrorResponse(String message, HttpStatus status, String path) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );
        return new ResponseEntity<>(response, status);
    }
}
