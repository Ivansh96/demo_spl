package ru.test.demo.spl.demo_spl.validation;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<List<String>> validate(WebExchangeBindException e) {
        var errors = e.getBindingResult()
            .getAllErrors()
            .stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(EventDoesNotExistException.class)
    public ResponseEntity<Response> handleEventException(EventDoesNotExistException e) {
        Response response = new Response(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
