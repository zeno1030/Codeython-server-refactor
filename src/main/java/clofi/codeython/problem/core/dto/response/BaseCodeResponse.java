package clofi.codeython.problem.core.dto.response;

import clofi.codeython.problem.core.domain.LanguageType;

public record BaseCodeResponse(
    LanguageType language, String code) {
}
