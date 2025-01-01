package clofi.codeython.problem.core.dto.response;

import java.util.List;

public record TestcaseResponse(
    List<String> inputCase, String outputCase, String description
) {
}
