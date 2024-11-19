package com.trevorhinson.tests.trivia.dto;

public enum Answer {
    
    RIGHT("right!"),
    WRONG("wrong!"),
    NOT_FOUND("No such question!"),
    ATTEMPTS_EXCEEDED("Max attempts reached!");

    private String message;

    public String getMessage() {
        return message;
    }
    Answer(String message) {
        this.message = message;
    }
    
}
