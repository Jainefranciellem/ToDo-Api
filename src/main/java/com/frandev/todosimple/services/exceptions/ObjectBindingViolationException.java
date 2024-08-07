package com.frandev.todosimple.services.exceptions;



import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ObjectBindingViolationException extends DataIntegrityViolationException {
    public ObjectBindingViolationException(String message) {
        super(message);
    }
}
