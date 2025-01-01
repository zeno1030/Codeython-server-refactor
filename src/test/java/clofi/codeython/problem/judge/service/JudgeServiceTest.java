package clofi.codeython.problem.judge.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import clofi.codeython.member.domain.Member;
import clofi.codeython.member.repository.MemberRepository;
import clofi.codeython.problem.core.domain.Language;
import clofi.codeython.problem.core.domain.LanguageType;
import clofi.codeython.problem.core.domain.Problem;
import clofi.codeython.problem.core.domain.Record;
import clofi.codeython.problem.core.domain.Testcase;
import clofi.codeython.problem.core.repository.LanguageRepository;
import clofi.codeython.problem.core.repository.ProblemRepository;
import clofi.codeython.problem.core.repository.RecordRepository;
import clofi.codeython.problem.core.repository.TestcaseRepository;
import clofi.codeython.problem.judge.dto.request.ExecutionRequest;
import clofi.codeython.problem.judge.dto.request.SubmitRequest;
import clofi.codeython.problem.judge.dto.response.ExecutionResponse;
import clofi.codeython.problem.judge.dto.response.SubmitResponse;

@SpringBootTest
class JudgeServiceTest {
    @Autowired
    JudgeService judgeService;

    @Autowired
    ProblemRepository problemRepository;

    @Autowired
    TestcaseRepository testcaseRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RecordRepository recordRepository;

    @Autowired
    LanguageRepository languageRepository;

    @AfterEach
    void tearDown() {
        recordRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        languageRepository.deleteAllInBatch();
        testcaseRepository.deleteAllInBatch();
        problemRepository.deleteAllInBatch();
    }

    @DisplayName("자바 코드로 채점을 진행할 수 있다.")
    @Test
    void javaCodeSubmitTest() {
        // given
        Member member = memberRepository.save(new Member("username", "password", "nickname"));
        Problem problem = problemRepository.save(new Problem(
            "title", "content", List.of("제한 사항"), 1, 1, List.of("int", "int[]")
        ));
        testcaseRepository.save(new Testcase(problem, List.of("3", "[1, 2, 3]"),
            "[2,4,6]", "description"));
        languageRepository.save(new Language(problem, LanguageType.JAVA, "base code..."));
        SubmitRequest submitRequest = new SubmitRequest("java", """
            class Solution {
                public int[] solution(int N, int[] values) {
                    int[] answer = new int[values.length];
                    for (int i = 0; i < N; i++) {
                        answer[i] = values[i] * 2;
                    }
                    return answer;
                }
            }
            """, null);

        // when
        SubmitResponse response = judgeService.submit(submitRequest, problem.getProblemNo(), member);

        // then
        assertThat(response.accuracy()).isEqualTo(100);
    }

    @DisplayName("자바스크립트 코드로 채점을 진행할 수 있다.")
    @Test
    void javascriptCodeSubmitTest() {
        // given
        Member member = memberRepository.save(new Member("username", "password", "nickname"));
        Problem problem = problemRepository.save(new Problem(
            "title", "content", List.of("제한 사항"), 1, 1, List.of("int", "int[]")
        ));
        testcaseRepository.save(new Testcase(problem, List.of("3", "[1, 2, 3]"),
            "[2,4,6]", "description"));
        languageRepository.save(new Language(problem, LanguageType.JAVA, "base code..."));
        SubmitRequest submitRequest = new SubmitRequest("javascript", """
            function solution(N, values) {
              return values.map(v => v * 2)
            }
            """, null);

        // when
        SubmitResponse response = judgeService.submit(submitRequest, problem.getProblemNo(), member);

        // then
        assertThat(response.accuracy()).isEqualTo(100);
    }

    @DisplayName("지원하지 않는 언어를 제출하면 오류가 발생한다.")
    @Test
    void invalidLanguageSubmitTest() {
        // given
        Member member = memberRepository.save(new Member("username", "password", "nickname"));
        Problem problem = problemRepository.save(new Problem(
            "title", "content", List.of("제한 사항"), 1, 1, List.of("int", "int[]")
        ));
        testcaseRepository.save(new Testcase(problem, List.of("3", "[1, 2, 3]"),
            "[2,4,6]", "description"));
        languageRepository.save(new Language(problem, LanguageType.JAVA, "base code..."));
        SubmitRequest submitRequest = new SubmitRequest("go", """
            function solution(N, values) {
              return values.map(v => v * 2)
            }
            """, null);

        // when & then
        Assertions.assertThatIllegalArgumentException()
            .isThrownBy(() -> judgeService.submit(submitRequest, problem.getProblemNo(), member))
            .withMessage("GO(은)는 지원하지 않는 언어 종류입니다.");
    }

    @DisplayName("코드 채점 중 Exception이 발생하면 오류가 발생한다.")
    @Test
    void exceptionCodeSubmitTest() {
        // given
        Member member = memberRepository.save(new Member("username", "password", "nickname"));
        Problem problem = problemRepository.save(new Problem(
            "title", "content", List.of("제한 사항"), 1, 1, List.of("int", "int[]")
        ));
        testcaseRepository.save(new Testcase(problem, List.of("3", "[1, 2, 3]"),
            "[2,4,6]", "description"));
        languageRepository.save(new Language(problem, LanguageType.JAVA, "base code..."));
        SubmitRequest submitRequest = new SubmitRequest("java", """
            class Solution {
                public int[] solution(int N, int[] values) {
                    int[] answer = new int[values.length];
                    for (int i = 0; i < N; i++) {
                        answer[i] = values[i] * 2;
                    }
                    throw new IllegalArgumentException("예외 발생");
                    return answer;
                }
            }
            """, null);

        // when & then
        Assertions.assertThatIllegalArgumentException()
            .isThrownBy(() -> judgeService.submit(submitRequest, problem.getProblemNo(), member));
    }

    @DisplayName("자바 코드 실행결과 및 정답 여부를 확인할 수 있다.")
    @Test
    void javaCodeExecutionTest() {
        // given
        Problem problem = problemRepository.save(new Problem(
            "title", "content", List.of("제한 사항"), 1, 1, List.of("int", "int[]")
        ));
        testcaseRepository.save(new Testcase(problem, List.of("3", "[1, 2, 3]"),
            "[2,4,6]", "description"));
        languageRepository.save(new Language(problem, LanguageType.JAVA, "base code..."));
        ExecutionRequest executionRequest = new ExecutionRequest("java", """
            class Solution {
                public int[] solution(int N, int[] values) {
                    int[] answer = new int[values.length];
                    for (int i = 0; i < N; i++) {
                        answer[i] = values[i] * 2;
                    }
                    return answer;
                }
            }
            """);

        // when
        List<ExecutionResponse> actual = judgeService.execution(executionRequest, problem.getProblemNo());

        // then
        assertThat(actual).containsExactly(new ExecutionResponse(true, "[2,4,6]"));
    }

    @DisplayName("자바스크립트 코드 실행결과 및 정답 여부를 확인할 수 있다.")
    @Test
    void javascriptCodeExecutionTest() {
        // given
        Problem problem = problemRepository.save(new Problem(
            "title", "content", List.of("제한 사항"), 1, 1, List.of("int", "int[]")
        ));
        testcaseRepository.save(new Testcase(problem, List.of("3", "[1, 2, 3]"),
            "[2,4,6]", "description"));
        languageRepository.save(new Language(problem, LanguageType.JAVA, "base code..."));
        ExecutionRequest executionRequest = new ExecutionRequest("javascript", """
            function solution(N, values) {
              return values.map(v => v * 2)
            }
            """);

        // when
        List<ExecutionResponse> actual = judgeService.execution(executionRequest, problem.getProblemNo());

        // then
        assertThat(actual).containsExactly(new ExecutionResponse(true, "[2,4,6]"));
    }

    @DisplayName("실행결과 및 정답 여부를 확인할 수 있다.(오답 코드)")
    @Test
    void incorrectOutputCodeTest() {
        // given
        Problem problem = problemRepository.save(new Problem(
            "title", "content", List.of("제한 사항"), 1, 1, List.of("int", "int[]")
        ));
        testcaseRepository.save(new Testcase(problem, List.of("3", "[1, 2, 3]"),
            "[2,4,6]", "description"));
        languageRepository.save(new Language(problem, LanguageType.JAVA, "base code..."));
        ExecutionRequest executionRequest = new ExecutionRequest("java", """
            class Solution {
                public int[] solution(int N, int[] values) {
                    int[] answer = new int[values.length];
                    System.out.println("Test message");
                    for (int i = 0; i < N; i++) {
                        answer[i] = values[i] * 2;
                    }
                    return answer;
                }
            }
            """);

        // when
        List<ExecutionResponse> actual = judgeService.execution(executionRequest, problem.getProblemNo());

        // then
        assertThat(actual).containsExactly(new ExecutionResponse(false, String.format("Test message%n[2,4,6]")));
    }

    @DisplayName("코드 실행 중 Exception이 발생하면 오류가 발생한다.")
    @Test
    void exceptionCodeExecutionTest() {
        // given
        Problem problem = problemRepository.save(new Problem(
            "title", "content", List.of("제한 사항"), 1, 1, List.of("int", "int[]")
        ));
        testcaseRepository.save(new Testcase(problem, List.of("3", "[1, 2, 3]"),
            "[2,4,6]", "description"));
        languageRepository.save(new Language(problem, LanguageType.JAVA, "base code..."));
        ExecutionRequest executionRequest = new ExecutionRequest("java", """
            class Solution {
                public int[] solution(int N, int[] values) {
                    int[] answer = new int[values.length];
                    for (int i = 0; i < N; i++) {
                        answer[i] = values[i] * 2;
                    }
                    throw new IllegalArgumentException("예외 발생");
                    return answer;
                }
            }
            """);

        // when & then
        Assertions.assertThatIllegalArgumentException()
            .isThrownBy(() -> judgeService.execution(executionRequest, problem.getProblemNo()));
    }

    @DisplayName("description이 없는 테스트 케이스는 실행하지 않는다.")
    @Test
    void emptyDescriptionTestcaseTest() {
        // given
        Problem problem = problemRepository.save(new Problem(
            "title", "content", List.of("제한 사항"), 1, 1, List.of("int", "int[]")
        ));
        testcaseRepository.save(new Testcase(problem, List.of("3", "[1, 2, 3]"),
            "[2,4,6]", null));
        languageRepository.save(new Language(problem, LanguageType.JAVA, "base code..."));
        ExecutionRequest executionRequest = new ExecutionRequest("java", """
            class Solution {
                public int[] solution(int N, int[] values) {
                    int[] answer = new int[values.length];
                    for (int i = 0; i < N; i++) {
                        answer[i] = values[i] * 2;
                    }
                    return answer;
                }
            }
            """);

        // when
        List<ExecutionResponse> execution = judgeService.execution(executionRequest, problem.getProblemNo());

        // then
        assertThat(execution).isEmpty();
    }

    @DisplayName("혼자놀기 제출 시 채점 기록이 저장된다.")
    @Test
    void recordTest() {
        // given
        Member member = memberRepository.save(new Member("username", "password", "nickname"));
        Problem problem = problemRepository.save(new Problem(
            "title", "content", List.of("제한 사항"), 1, 1, List.of("int", "int[]")
        ));
        testcaseRepository.save(new Testcase(problem, List.of("3", "[1, 2, 3]"),
            "[2,4,6]", "description"));
        languageRepository.save(new Language(problem, LanguageType.JAVA, "base code..."));
        SubmitRequest submitRequest = new SubmitRequest("java", """
            class Solution {
                public int[] solution(int N, int[] values) {
                    int[] answer = new int[values.length];
                    for (int i = 0; i < N; i++) {
                        answer[i] = values[i] * 2;
                    }
                    return answer;
                }
            }
            """, null);

        // when
        judgeService.submit(submitRequest, problem.getProblemNo(), member);
        List<Record> records = recordRepository.findAllByProblemAndMemberOrderByCreatedAtDesc(problem, member);

        // then
        assertThat(records).hasSize(1);
    }
}
