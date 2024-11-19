Feature: Trivia API

  Scenario: Get trivia question
    Given the client calls /trivia/start
    Then the client receives status code 200
    And the client receives trivia question with trivia ID 1

  Scenario: Reply to a trivia question
    Given a trivia question with ID 2 exists
    When I send a reply to trivia question with ID 2
    Then I receive a successful reply status code 200
    And I receive a result of "right!"

  Scenario: Reply to a trivia question with wrong answer
    Given a trivia with ID 3 exists
    When I send a reply with ID 3
    Then I receive a failed reply status code 400
    And I receive a result body of "wrong!"

  Scenario: Reply to a trivia question with unknown id
    Given a trivia with ID 99 does not exist
    When I send a reply with an incorrect ID 99
    Then I receive a not found status code 404
