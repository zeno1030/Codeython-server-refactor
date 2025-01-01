package clofi.codeython.problem.judge.domain.runner;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JavascriptCodeRunner implements CodeRunner {

    @Override
    public String run(String route, List<String> inputs) {
        ArrayList<String> command = new ArrayList<>(
                List.of("node", route + "solution.js"));

        command.addAll(inputs);

        ProcessBuilder processBuilder = new ProcessBuilder(command);

        Process process;
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String outputMessage = outputReader.lines().collect(Collectors.joining(System.lineSeparator()));
        String errorMessage = errorReader.lines().collect(Collectors.joining(System.lineSeparator()));

        if (!errorMessage.isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }

        return outputMessage;
    }

}
