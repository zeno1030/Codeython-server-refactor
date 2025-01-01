package clofi.codeython.problem.core.dto.request;

import java.util.List;

import org.hibernate.validator.constraints.Range;

import clofi.codeython.problem.core.domain.Language;
import clofi.codeython.problem.core.domain.LanguageType;
import clofi.codeython.problem.core.domain.Problem;
import clofi.codeython.problem.core.domain.Testcase;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProblemRequest {

    @NotBlank(message = "문제제목은 공백일 수 없습니다.")
    private String title;

    @NotBlank(message = "문제설명은 공백일 수 없습니다.")
    private String content;

    @NotEmpty(message = "제한사항은 공백일 수 없습니다.")
    private List<String> limitFactors;

    @NotNull(message = "제한시간은 공백일 수 없습니다.")
    @Min(value = 10, message = "제한시간은 최소 10분입니다.")
    private Integer limitTime;

    @NotNull(message = "난이도는 공백일 수 없습니다.")
    @Range(min = 1, max = 5, message = "난이도는 최소 1에서 최대 5입니다.")
    private Integer difficulty;

    @NotEmpty(message = "입력값 타입은 공백일 수 없습니다.")
    private List<String> type;

    private List<BaseCodeRequest> baseCodes;

    private List<TestcaseRequest> testcase;

    public Problem toProblem() {
        return new Problem(
            title,
            content,
            limitFactors,
            limitTime,
            difficulty,
            type
        );
    }

    public Language toLanguage(Problem problemNo, LanguageType language, String code) {
        return new Language(
            problemNo,
            language,
            code
        );
    }

    public Testcase toTestcase(
        Problem problemNo, List<String> inputCase, String outputCase, String description) {
        return new Testcase(
            problemNo,
            inputCase,
            outputCase,
            description
        );
    }

}
