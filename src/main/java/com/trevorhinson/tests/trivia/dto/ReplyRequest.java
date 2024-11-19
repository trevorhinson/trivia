package com.trevorhinson.tests.trivia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReplyRequest {

    private String answer;

    public void validate() {
        if (answer == null) {
            throw new IllegalArgumentException("Reply request must include an answer.");
        }
        if (answer.isEmpty()) {
            throw new IllegalArgumentException("Reply request answer cannot be empty.");
        }
        if (answer.length() > 50) {
            throw new IllegalArgumentException("Reply request answer cannot exceed 50 characters.");
        }
        String owaspPattern = ".*[<>{}\"/\\\\;\\(\\)\\&\\+].*";
        if (answer.matches(owaspPattern)) {
            throw new IllegalArgumentException("Reply request answer contains prohibited characters.");
        }
        String sqlInjectionPattern = "/[\\t\\r\\n]|(--[^\\r\\n]*)|(\\/\\*[\\w\\W]*?(?=\\*)\\*\\/)/gi";
        if (answer.matches(sqlInjectionPattern)) {
            throw new IllegalArgumentException("Reply request answer contains prohibited SQL injection characters.");
        }
    }

}
