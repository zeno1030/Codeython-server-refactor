package clofi.codeython.problem.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LanguageType {
    JAVA("javaExecutionFileCreator", "javaCodeRunner"),
    JAVASCRIPT("javascriptExecutionFileCreator", "javascriptCodeRunner");

    private final String creatorName;
    private final String codeRunnerName;

    LanguageType(String creatorName, String codeRunnerName) {
        this.creatorName = creatorName;
        this.codeRunnerName = codeRunnerName;
    }

    @JsonCreator
    public static LanguageType forValue(String value) {
        for (LanguageType languageType : LanguageType.values()) {
            if (languageType.name().equalsIgnoreCase(value)) {
                return languageType;
            }
        }
        throw new IllegalArgumentException(value + "(은)는 지원하지 않는 언어 종류입니다.");
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }

    public static String getCreatorName(String type) {
        LanguageType languageType = forValue(type.toUpperCase());
        return languageType.creatorName;
    }

    public static String getCodeRunnerName(String type) {
        LanguageType languageType = forValue(type.toUpperCase());
        return languageType.codeRunnerName;
    }
}
