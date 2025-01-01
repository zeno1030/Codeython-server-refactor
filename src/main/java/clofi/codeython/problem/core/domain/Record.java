package clofi.codeython.problem.core.domain;

import clofi.codeython.common.domain.BaseEntity;
import clofi.codeython.member.domain.Member;
import jakarta.persistence.Column;
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
public class Record extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_no", nullable = false)
    private Long recordNo;

    @Column(name = "written_code", columnDefinition = "TEXT")
    private String writtenCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "problem_no", nullable = false)
    private Problem problem;

    @Column(name = "language", length = 20)
    private String language;

    @Column(name = "accuracy", nullable = false, columnDefinition = "int default 0")
    private int accuracy;

    @Column(name = "grade")
    private Integer grade;

    @Column(name = "member_cnt")
    private Integer memberCnt;

    public Record(String writtenCode, Member member, Problem problem, String language, int accuracy,
        Integer grade, Integer memberCnt) {
        this.writtenCode = writtenCode;
        this.member = member;
        this.problem = problem;
        this.language = language;
        this.accuracy = accuracy;
        this.grade = grade;
        this.memberCnt = memberCnt;
    }

    public static Record of(Member member, Problem problem, int accuracy, int grade, int memberCnt) {
        return new Record(null, member, problem, null, accuracy, grade, memberCnt);
    }

    public void update(String code, int accuracy) {
        this.writtenCode = code;
        this.accuracy = Math.max(this.accuracy, accuracy);
    }
}
