package clofi.codeython.problem.judge.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import clofi.codeython.problem.core.domain.LanguageType;
import clofi.codeython.problem.core.domain.Testcase;
import clofi.codeython.problem.judge.domain.runner.CodeRunner;
import clofi.codeython.problem.judge.dto.response.ExecutionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class ResultCalculator {
    private final Map<String, CodeRunner> codeRunnerMap;

    public int judge(String route, String language, List<Testcase> testcases) {
        CodeRunner codeRunner = codeRunnerMap.get(LanguageType.getCodeRunnerName(language));
        int total = testcases.size();
        int success = 0;

        for (Testcase testcase : testcases) {
            String result = codeRunner.run(route, testcase.getInput());
            if (isMatch(result, testcase.getOutput())) {
                success++;
            }
        }

        return success * 100 / total;
    }

    private boolean isMatch(String executionResult, String output) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode actual = mapper.readTree(executionResult);
            JsonNode expected = mapper.readTree(output);
            return actual.equals(expected);
        } catch (JsonProcessingException ignored) {
        }
        return false;
    }

    public List<ExecutionResponse> execution(String route, String language, List<Testcase> testcases) {
        CodeRunner codeRunner = codeRunnerMap.get(LanguageType.getCodeRunnerName(language));
        ObjectMapper mapper = new ObjectMapper();

        List<ExecutionResponse> list = new ArrayList<>();
        for (Testcase testcase : testcases) {
            if (testcase.getDescription() == null) {
                continue;
            }
            String result = codeRunner.run(route, testcase.getInput());

            JsonNode actual;
            JsonNode expected;
            try {
                actual = mapper.readTree(result);
                expected = mapper.readTree(testcase.getOutput());

                result = mapper.writeValueAsString(actual);
                list.add(new ExecutionResponse(actual.equals(expected), result));
            } catch (JsonProcessingException ignored) {
                list.add(new ExecutionResponse(false, result));
            }
        }

        return list;
    }
}
