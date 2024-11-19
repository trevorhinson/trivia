package com.trevorhinson.tests.trivia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TriviaResponse {

    private Long triviaId;
    private String question;
    private List<String> possibleAnswers;

}
