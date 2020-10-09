package com.example.poller_bear.exceptionHandler;

import com.example.poller_bear.dto.ErrorResponseDto;
import com.example.poller_bear.exception.InternalException;
import com.example.poller_bear.exception.BadRequestException;
import com.example.poller_bear.exception.ResourceNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

//@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<Object> handleBadRequestException(BadRequestException exception) {
        return new ResponseEntity(
                new ErrorResponseDto(exception.getMessage(), HttpStatus.BAD_REQUEST),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException exception) {
        return new ResponseEntity(
                new ErrorResponseDto(exception.getMessage(), HttpStatus.BAD_REQUEST),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(InternalException.class)
    public final ResponseEntity<?> handleApiException(InternalException exception) {
        return new ResponseEntity(
                new ErrorResponseDto(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }


}
