package com.trevorhinson.tests.trivia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trevorhinson.tests.trivia.domain.Trivia;
import com.trevorhinson.tests.trivia.dto.ReplyRequest;
import com.trevorhinson.tests.trivia.repository.TriviaRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {
        "server.port=8081"
})
public class TriviaControllerSteps {

    private final WebTestClient webTestClient;
    private WebTestClient.ResponseSpec response;

    private final int port = 8081;

    @Autowired
    private TriviaRepository triviaRepository;

    public TriviaControllerSteps(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
    }

    @Before
    public void before() {
        triviaRepository.deleteAll();
    }

    @Given("the client calls \\/trivia\\/start")
    public void the_client_calls_trivia_start() {
        response = webTestClient.post().uri("http://localhost:" + port + "/trivia/start")
                .accept(APPLICATION_JSON)
                .exchange();
    }

    @Then("the client receives status code {int}")
    public void the_client_receives_status_code(int statusCode) {
        response.expectStatus().isEqualTo(statusCode);
    }

    @Then("the client receives trivia question with trivia ID {int}")
    public void the_client_receives_trivia_question_with_trivia_id(int id) {
        response.expectBody()
                .jsonPath("$.triviaId").isEqualTo(id);
    }

    @Given("a trivia question with ID {int} exists")
    public void a_trivia_question_with_ID_exists(long id) {
        Trivia trivia = new Trivia();
        trivia.setTriviaId(id);
        trivia.setQuestion("Test");
        trivia.setAnswerAttempts(0);
        trivia.setCorrectAnswer("Sample Answer");
        triviaRepository.save(trivia);
    }

    @When("I send a reply to trivia question with ID {int}")
    public void i_send_a_reply_to_trivia_question_with_ID(int id) throws Exception {
        String uri = "http://localhost:" + port + "/trivia/reply/" + id;
        ReplyRequest request = new ReplyRequest();
        request.setAnswer("Sample Answer");

        response = webTestClient.put().uri(uri)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .bodyValue(new ObjectMapper().writeValueAsString(request))
                .exchange();
    }

    @Then("I receive a successful reply status code {int}")
    public void i_receive_a_successful_reply_response(int statusCode) {
        response.expectStatus().isEqualTo(statusCode);
    }

    @Then("I receive a result of {string}")
    public void i_receive_a_result_of(String answer) {
        response.expectBody()
                .jsonPath("$.result").isEqualTo(answer);
    }

    @Given("a trivia with ID {int} exists")
    public void a_trivia_with_ID_exists(long id) {
        Trivia trivia = new Trivia();
        trivia.setTriviaId(id);
        trivia.setQuestion("Test");
        trivia.setAnswerAttempts(0);
        trivia.setCorrectAnswer("Answer");
        triviaRepository.save(trivia);
    }

    @When("I send a reply with ID {int}")
    public void i_send_a_reply_with_ID(int id) throws Exception {
        String uri = "http://localhost:" + port + "/trivia/reply/" + id;
        ReplyRequest request = new ReplyRequest();
        request.setAnswer("not correct answer");

        response = webTestClient.put().uri(uri)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .bodyValue(new ObjectMapper().writeValueAsString(request))
                .exchange();
    }

    @Then("I receive a failed reply status code {int}")
    public void i_receive_a_failed_reply_response(int statusCode) {
        response.expectStatus().isEqualTo(statusCode);
    }

    @Then("I receive a result body of {string}")
    public void i_receive_a_result_body_of(String answer) {
        response.expectBody()
                .jsonPath("$.result").isEqualTo(answer);
    }

    @Given("a trivia with ID {int} does not exist")
    public void a_trivia_with_ID_does_not_exist(long id) {
        final Optional<Trivia> byId = triviaRepository.findById(id);
        assertThat(byId).isEmpty();
    }

    @When("I send a reply with an incorrect ID {int}")
    public void i_send_a_reply_with_an_incorrect_ID(int id) throws Exception {
        String uri = "http://localhost:" + port + "/trivia/reply/" + id;
        ReplyRequest request = new ReplyRequest();
        request.setAnswer(UUID.randomUUID().toString());

        response = webTestClient.put().uri(uri)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .bodyValue(new ObjectMapper().writeValueAsString(request))
                .exchange();
    }

    @Then("I receive a not found status code {int}")
    public void i_receive_a_not_found_status_code(int statusCode) {
        response.expectStatus().isEqualTo(statusCode);
    }

}
