package com.trevorhinson.tests.trivia.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class TriviaItem {

    private Long triviaId;
    private String question;
    private List<String> possibleAnswers;

}
