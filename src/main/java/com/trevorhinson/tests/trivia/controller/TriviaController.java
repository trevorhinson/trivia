package com.trevorhinson.tests.trivia.controller;

import com.trevorhinson.tests.trivia.dto.ReplyRequest;
import com.trevorhinson.tests.trivia.dto.ReplyResponse;
import com.trevorhinson.tests.trivia.dto.TriviaResponse;
import com.trevorhinson.tests.trivia.service.TriviaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.trevorhinson.tests.trivia.dto.Answer.*;

@RestController
@RequestMapping("/trivia")
@AllArgsConstructor
@Slf4j
public class TriviaController {

    private TriviaService triviaService;

    @PostMapping("/start")
    public ResponseEntity<TriviaResponse> startTrivia() {
        return ResponseEntity.ok(triviaService.startTrivia());
    }

    @PutMapping(path = "/reply/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ReplyResponse> replyTrivia(@PathVariable Long id, @RequestBody ReplyRequest answerRequest) {
        validateId(id);
        validateReplyRequest(answerRequest);
        final ReplyResponse replyResponse = triviaService.reply(id, answerRequest);
        return applyResponse(replyResponse);
    }

    ResponseEntity<ReplyResponse> applyResponse(ReplyResponse replyResponse) {
        log.info("Applying response: {}", replyResponse);
        if (replyResponse.getResult().equalsIgnoreCase(ATTEMPTS_EXCEEDED.getMessage())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(replyResponse);
        } else if (replyResponse.getResult().equalsIgnoreCase(WRONG.getMessage())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(replyResponse);
        } else if (replyResponse.getResult().equalsIgnoreCase(NOT_FOUND.getMessage())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(replyResponse);
        } else if (replyResponse.getResult().equalsIgnoreCase(RIGHT.getMessage())) {
            return ResponseEntity.ok(replyResponse);
        } else {
            log.error("Unexpected result from trivia service: {}", replyResponse.getResult());
            throw new IllegalStateException("Unexpected result from trivia service.");
        }
    }

    void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid trivia id.");
        }
    }

    void validateReplyRequest(ReplyRequest replyRequest) {
        if (replyRequest == null) {
            throw new IllegalArgumentException("Reply request must include an answer.");
        }
        replyRequest.validate();
    }

}
