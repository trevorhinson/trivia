package com.trevorhinson.tests.trivia.controller;

import com.trevorhinson.tests.trivia.dto.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> illegalArgumentException(Exception ex, WebRequest request) {
        log.error("IllegalArgumentException encountered: ", ex);
        return new ResponseEntity<>(new ErrorMessage(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExhaustedRetryException.class)
    public ResponseEntity<ErrorMessage> exhaustedRetryException(ExhaustedRetryException ex, WebRequest request) {
        log.error("ExhaustedRetryException encountered: ", ex);
        return new ResponseEntity<>(new ErrorMessage("Retry attempts exhausted. Please try again later."),
                HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorMessage> illegalStateException(Exception ex, WebRequest request) {
        log.error("IllegalStateException encountered: ", ex);
        return new ResponseEntity<>(new ErrorMessage(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> unexpectedException(Exception ex, WebRequest request) {
        log.error("An unexpected error occurred: ", ex);
        return new ResponseEntity<>(new ErrorMessage("An unexpected error occurred. Please try again later."), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}