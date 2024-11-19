package com.trevorhinson.tests.trivia.dto;

import lombok.Getter;

@Getter
public enum Answer {
    
    RIGHT("right!"),
    WRONG("wrong!"),
    NOT_FOUND("No such question!"),
    ATTEMPTS_EXCEEDED("Max attempts reached!");

    private final String message;

    Answer(String message) {
        this.message = message;
    }
    
}
