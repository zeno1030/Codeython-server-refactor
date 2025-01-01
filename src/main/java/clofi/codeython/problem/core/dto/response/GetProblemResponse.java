package clofi.codeython.problem.core.dto.response;

import java.util.List;

import clofi.codeython.problem.core.domain.Problem;

public record GetProblemResponse(
    String title,
    String content,
    List<String> limitFactors,
    int limitTime,
    List<BaseCodeResponse> baseCode,
    List<TestcaseResponse> testcase,
    int difficulty
) {
    public static GetProblemResponse of(
        Problem problem, List<BaseCodeResponse> baseCode, List<TestcaseResponse> testcase) {
        return new GetProblemResponse(
            problem.getTitle(),
            problem.getContent(),
            problem.getLimitFactor(),
            problem.getLimitTime(),
            baseCode,
            testcase,
            problem.getDifficulty()
        );
    }
}
