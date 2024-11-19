package com.trevorhinson.tests.trivia.controller;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TriviaControllerSteps {

    private final WebTestClient webTestClient;
    private WebTestClient.ResponseSpec response;

    @LocalServerPort
    private int port;

    public TriviaControllerSteps(WebTestClient webTestClient) {
        this.webTestClient = webTestClient;
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

}
