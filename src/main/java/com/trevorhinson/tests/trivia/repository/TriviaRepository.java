package com.trevorhinson.tests.trivia.repository;

import com.trevorhinson.tests.trivia.domain.Trivia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TriviaRepository extends JpaRepository<Trivia, Long> {

}
