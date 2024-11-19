package com.trevorhinson.tests.trivia.client.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class ApiResponse {

    private int responseCode;
    private List<Result> results;

}
