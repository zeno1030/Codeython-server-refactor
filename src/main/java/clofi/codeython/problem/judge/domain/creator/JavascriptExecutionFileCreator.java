package clofi.codeython.problem.judge.domain.creator;

import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Component
public class JavascriptExecutionFileCreator implements ExecutionFileCreator {

    @Override
    public void create(List<String> inputTypes, String code, String route) {
        String executionCode = getExecutionCode(inputTypes.size(), code);
        createFile(route, executionCode, "solution.js");
    }

    private String getExecutionCode(int inputTypeSize, String code) {
        StringBuilder sb = new StringBuilder(code);

        sb.append("console.log(JSON.stringify((solution(");
        for (int i = 0; i < inputTypeSize; i++) {
            sb.append(String.format("JSON.parse(process.argv[%d]),", i + 2));
        }
        sb.append("))));");

        return sb.toString();
    }

    private void createFile(String route, String content, String fileName) {
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(route + fileName))) {
            printWriter.print(content);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
