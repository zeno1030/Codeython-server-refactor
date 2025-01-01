package clofi.codeython.problem.core.domain;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
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
public class Testcase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "testcase_no", nullable = false)
    private Long testcaseNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_no", nullable = false)
    private Problem problem;

    @ElementCollection
    @Column(name = "input", nullable = false, columnDefinition = "TEXT")
    private List<String> input;

    @Column(name = "output", nullable = false, columnDefinition = "TEXT")
    private String output;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    public Testcase(Problem problem, List<String> input, String output, String description) {
        this.problem = problem;
        this.input = input;
        this.output = output;
        this.description = description;
    }

    public Testcase(Problem problem, List<String> input, String output) {
        this(problem, input, output, "");
    }
}
