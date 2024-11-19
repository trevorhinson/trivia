package com.trevorhinson.tests.trivia.repository;

import com.trevorhinson.tests.trivia.domain.Trivia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TriviaRepositoryTest {

    @Autowired
    private TriviaRepository underTest;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
    }

    @Test
    void shouldSaveAndFindTrivia() {
        // Given
        Trivia trivia = createTrivia();

        // When
        Trivia savedTrivia = underTest.save(trivia);

        // Then
        assertThat(savedTrivia).isNotNull();
        assertThat(savedTrivia.getTriviaId()).isNotNull();
        assertThat(underTest.findById(savedTrivia.getTriviaId()).isPresent()).isTrue();
        assertThat(underTest.findById(savedTrivia.getTriviaId()).get()).isEqualTo(savedTrivia);
    }

    @Test
    void shouldDeleteById() {
        // Given
        Trivia trivia = createTrivia();
        Trivia saved = underTest.save(trivia);

        // When
        underTest.deleteById(saved.getTriviaId());

        // Then
        assertThat(underTest.findById(saved.getTriviaId())).isEmpty();
    }

    private Trivia createTrivia() {
        Trivia trivia = new Trivia();
        trivia.setQuestion("Which soccer team won the Copa America 2015 Championship?");
        trivia.setCorrectAnswer("Chile");
        trivia.setAnswerAttempts(1);
        return trivia;
    }

}

