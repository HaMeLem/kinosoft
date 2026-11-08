package de.wps.ddd.kino.einlasskontrolle.infrastructure.web;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import de.wps.ddd.kino.common.error.RessourceNichtGefunden;

@RestControllerAdvice
public class FehlerHandler {

    @ExceptionHandler(RessourceNichtGefunden.class)
    public ResponseEntity<String> handle(RessourceNichtGefunden e)
    {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}