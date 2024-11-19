Feature: Trivia API

  Scenario: Get trivia question
    Given the client calls /trivia/start
    Then the client receives status code 200
    And the client receives trivia question with trivia ID 1

