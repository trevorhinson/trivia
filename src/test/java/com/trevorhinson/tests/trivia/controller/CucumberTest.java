package com.trevorhinson.tests.trivia.controller;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.trevorhinson.tests.trivia.controller"
)
@ComponentScan(basePackages = {"com.trevorhinson.tests.trivia"})
public class CucumberTest {
}