package clofi.codeython.problem.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", nullable = false)
    private Long languageNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_no", nullable = false)
    private Problem problem;

    @Column(name = "language", nullable = false)
    @Enumerated(EnumType.STRING)
    private LanguageType language;

    @Column(name = "base_code", nullable = false, columnDefinition = "TEXT")
    private String baseCode;

    public Language(Problem problem, LanguageType language, String baseCode) {
        this.problem = problem;
        this.language = language;
        this.baseCode = baseCode;
    }
}
