package com.frandev.todosimple.exceptions;

import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.frandev.todosimple.services.exceptions.ObjectBindingViolationException;
import com.frandev.todosimple.services.exceptions.ObjectNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
@RestControllerAdvice
class GlobalExceptionHandle extends ResponseEntityExceptionHandler {
    @Value("${server.error.include-exception}")
    private boolean printStackTrace;
    
    //Override sobrescreve metodo já existente
    @Override
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException methodArgumentNotValidException,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Validation error. Check 'errors' field for details.");
        for (FieldError fieldError : methodArgumentNotValidException.getBindingResult().getFieldErrors()) {
            errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.unprocessableEntity().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllUncaughtException(
        Exception exception,
        WebRequest request) {
            final String errorMenssage = "UNknown error occurrend";
            log.error(errorMenssage, exception);
            return buildErrorResponse (
                exception,
                errorMenssage,
                HttpStatus.INTERNAL_SERVER_ERROR,
                request);
        }


    //usuarios com nomes iguais no banco
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDataIntegrityViolationException(
        DataIntegrityViolationException dataIntegrityViolationException,
        WebRequest request) {
    String errorMessage = dataIntegrityViolationException.getMostSpecificCause().getMessage();
    log.error("Failed to save entity with integrity problems: " + errorMessage, dataIntegrityViolationException);
    return buildErrorResponse(
            dataIntegrityViolationException,
            errorMessage,
            HttpStatus.CONFLICT,
            request);
    }


    // violação de senha
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<Object> handleConstraintViolationException(
            ConstraintViolationException constraintViolationException,
            WebRequest request) {
        log.error("Failed to validate element", constraintViolationException);
        return buildErrorResponse(
                constraintViolationException,
                HttpStatus.UNPROCESSABLE_ENTITY,
                request);
    }


    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<Object> handleObjectNotFounExpection (
        ObjectNotFoundException objectNotFoundExcepiton,
        WebRequest request) {
            log.error("Failed to find the request element", objectNotFoundExcepiton);
            return buildErrorResponse(
                objectNotFoundExcepiton,
                HttpStatus.NOT_FOUND,
                request);
        }
    
    @ExceptionHandler(ObjectBindingViolationException.class) 
        public ResponseEntity<Object> handleObjectBindingViolationException (
            ObjectBindingViolationException objectBindingViolationException,
            WebRequest request) {
                log.error("Failed to save entity with assoiciated data", objectBindingViolationException);
                return buildErrorResponse(objectBindingViolationException,  HttpStatus.CONFLICT, request);
            }

    private ResponseEntity<Object> buildErrorResponse(
            Exception exception,
            HttpStatus httpStatus,
            WebRequest request) {
        return buildErrorResponse(exception, exception.getMessage(), httpStatus, request);
    }

    private ResponseEntity<Object> buildErrorResponse(
            Exception exception,
            String message,
            HttpStatus httpStatus,
            WebRequest request) {
            ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message);
        if (this.printStackTrace) {
            errorResponse.setStackTrace(ExceptionUtils.getStackTrace(exception));
        }
        return ResponseEntity.status(httpStatus).body(errorResponse);
        }
}
