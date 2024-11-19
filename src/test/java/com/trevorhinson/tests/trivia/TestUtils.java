package com.trevorhinson.tests.trivia;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trevorhinson.tests.trivia.client.dto.ApiResponse;
import com.trevorhinson.tests.trivia.client.dto.Result;
import lombok.SneakyThrows;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;

public class TestUtils {

    private static final String RESULT_JSON = "{\n" +
            "            \"category\": \"Sports\",\n" +
            "            \"type\": \"multiple\",\n" +
            "            \"difficulty\": \"medium\",\n" +
            "            \"question\": \"Which soccer team won the Copa America 2015 Championship ?\",\n" +
            "            \"correct_answer\": \"Chile\",\n" +
            "            \"incorrect_answers\": [\"Argentina\", \"Brazil\", \"Paraguay\"]\n" +
            "          }";

    public static void setFieldValue(Object instance, String name, Object value) throws NoSuchFieldException {
        final Field declaredField = instance.getClass().getDeclaredField(name);
        declaredField.setAccessible(true);
        ReflectionUtils.setField(declaredField, instance, value);
    }

    @SneakyThrows
    public static Result createResult() {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(RESULT_JSON, Result.class);
    }

    public static ApiResponse createApiResponse() {
        ApiResponse response = new ApiResponse();
        response.setResponseCode(200);
        response.setResults(List.of(createResult()));
        return response;
    }

}
