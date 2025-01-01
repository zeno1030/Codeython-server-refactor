package clofi.codeython.problem.core.dto.response;

import clofi.codeython.problem.core.domain.Problem;

public record AllProblemResponse(
    Long problemId,
    String title,
    Integer difficulty,
    Integer accuracy,
    boolean isPlayed
) {
    public static AllProblemResponse of(Problem problem, Integer accuracy, Boolean isPlayed) {
        return new AllProblemResponse(
            problem.getProblemNo(),
            problem.getTitle(),
            problem.getDifficulty(),
            accuracy,
            isPlayed
        );
    }
}
