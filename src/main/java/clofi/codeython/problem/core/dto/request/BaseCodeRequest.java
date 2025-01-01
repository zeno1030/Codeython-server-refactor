package clofi.codeython.problem.core.dto.request;

import clofi.codeython.problem.core.domain.LanguageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BaseCodeRequest {
    @NotNull(message = "언어는 공백일 수 없습니다.")
    private LanguageType language;

    @NotBlank(message = "코드는 공백일 수 없습니다.")
    private String code;

    public BaseCodeRequest(LanguageType language, String code) {
        this.language = language;
        this.code = code;
    }
}
