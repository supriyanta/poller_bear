package com.example.poller_bear.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@Setter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private String resource;
    private String field;
    private Object fieldValue;

    public ResourceNotFoundException(String resource, String field, Object fieldValue) {
        super(String.format("%s of %s with value %s not found", field, resource, fieldValue));
        this.resource = resource;
        this.field = field;
        this.fieldValue = fieldValue;
    }
}
