package clofi.codeython.problem.core.service;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
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
import clofi.codeython.problem.core.dto.request.BaseCodeRequest;
import clofi.codeython.problem.core.dto.request.CreateProblemRequest;
import clofi.codeython.problem.core.dto.request.TestcaseRequest;
import clofi.codeython.problem.core.dto.response.AllProblemResponse;
import clofi.codeython.problem.core.dto.response.BaseCodeResponse;
import clofi.codeython.problem.core.dto.response.GetProblemResponse;
import clofi.codeython.problem.core.dto.response.RecordResponse;
import clofi.codeython.problem.core.repository.LanguageRepository;
import clofi.codeython.problem.core.repository.ProblemRepository;
import clofi.codeython.problem.core.repository.RecordRepository;
import clofi.codeython.problem.core.repository.TestcaseRepository;

@SpringBootTest
class ProblemServiceTest {
    @Autowired
    private ProblemService problemService;
    @Autowired
    private ProblemRepository problemRepository;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private TestcaseRepository testcaseRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RecordRepository recordRepository;

    @AfterEach
    void afterEach() {
        testcaseRepository.deleteAllInBatch();
        languageRepository.deleteAllInBatch();
        recordRepository.deleteAllInBatch();
        problemRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("문제 등록")
    @Test
    void createProblemTest() {
        //given
        List<BaseCodeRequest> baseCodeRequests = new ArrayList<>();
        baseCodeRequests.add(new BaseCodeRequest(
            LanguageType.JAVA,
            """
                    public class Main{
                    public static void main(String[] args){
                    System.out.println("Hello World!");}
                    }
                """));
        List<TestcaseRequest> testcaseRequests = new ArrayList<>();
        testcaseRequests.add(new TestcaseRequest(
            List.of("a, b", "3, 4", "5, 6"),
            "3",
            "a"
        ));

        CreateProblemRequest createProblemRequest =
            getCreateProblemRequest(baseCodeRequests, testcaseRequests);

        //when
        Long problemId = problemService.createProblem(createProblemRequest);
        Problem problem = problemRepository.findById(problemId).get();
        //then
        assertThat(problem.getTitle()).isEqualTo("where is koreanCow");
        assertThat(problem.getContent()).isEqualTo("koreanCow is delicious");
        assertThat(problem.getLimitFactor()).containsExactly("Never eat dog");
        assertThat(problem.getLimitTime()).isEqualTo(60);
        assertThat(problem.getDifficulty()).isEqualTo(1);

        Language language = languageRepository.findByProblem(problem).get(0);
        assertThat(language.getLanguage()).isEqualTo(LanguageType.JAVA);
        assertThat(language.getBaseCode()).isEqualTo(
            """
                    public class Main{
                    public static void main(String[] args){
                    System.out.println("Hello World!");}
                    }
                """);

        Testcase testcase = testcaseRepository.findByProblem(problem).get(0);
        assertThat(testcase.getInput()).containsExactly("a, b", "3, 4", "5, 6");
        assertThat(testcase.getOutput()).isEqualTo("3");
        assertThat(testcase.getDescription()).isEqualTo("a");
    }

    @DisplayName("문제 제목 중복")
    @Test
    void createProblemTitleExceptionTest() {
        //given
        Problem problem = new Problem(
            "where is koreanCow",
            "koreanCow is delicious",
            List.of("Never eat dog"),
            60,
            1,
            List.of("String[][]", "int", "String")
        );
        problemRepository.save(problem);

        //when
        List<BaseCodeRequest> baseCodeRequests = new ArrayList<>();
        baseCodeRequests.add(new BaseCodeRequest(
            LanguageType.JAVA,
            """
                    public class Main{
                    public static void main(String[] args){
                    System.out.println("Hello World!");}
                    }
                """));

        List<TestcaseRequest> testcaseRequests = new ArrayList<>();
        testcaseRequests.add(new TestcaseRequest(
            List.of("a, b", "3, 4", "5, 6"),
            "3",
            "a"
        ));

        CreateProblemRequest createProblemRequest = getCreateProblemRequest(
            baseCodeRequests, testcaseRequests);

        //then
        assertThatThrownBy(() ->
            problemService.createProblem(createProblemRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 만들어진 문제 제목입니다.");

    }

    @DisplayName("언어 지원")
    @Test
    void createProblemWithLanguageTypeTest() {
        //given
        List<BaseCodeRequest> baseCodeRequests = new ArrayList<>();

        //then
        assertThatThrownBy(() ->
            baseCodeRequests.add(new BaseCodeRequest(
                LanguageType.valueOf("MATLAB"),
                """
                        public class Main{
                        public static void main(String[] args){
                        System.out.println("Hello World!");}
                        }
                    """)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("No enum constant clofi.codeython.problem.core.domain.LanguageType.MATLAB");

    }

    @DisplayName("문제 목록 조회")
    @Test
    void getAllProblemTest() {
        //given
        Member member = memberRepository.save(new Member("username", "password", "nickname"));
        List<BaseCodeRequest> baseCodeRequests1 = new ArrayList<>();
        baseCodeRequests1.add(new BaseCodeRequest(
            LanguageType.JAVA,
            """
                    public class Main{
                    public static void main(String[] args){
                    System.out.println("Hello World!");}
                    }
                """));
        List<TestcaseRequest> testcaseRequests1 = new ArrayList<>();
        testcaseRequests1.add(new TestcaseRequest(
            List.of("a, b", "3, 4", "5, 6"),
            "3",
            "a"
        ));

        CreateProblemRequest createProblemRequest = getCreateProblemRequest(
            baseCodeRequests1, testcaseRequests1);
        Long problemId = problemService.createProblem(createProblemRequest);

        //when
        List<AllProblemResponse> allProblem = problemService.getAllProblem(member);

        //then
        AllProblemResponse problem = allProblem.get(0);
        Assertions.assertThat(1).isEqualTo(allProblem.size());
        Assertions.assertThat("where is koreanCow").isEqualTo(problem.title());

        //given
        List<BaseCodeRequest> baseCodeRequests2 = new ArrayList<>();
        baseCodeRequests2.add(new BaseCodeRequest(
            LanguageType.JAVA,
            """
                    public class Main{
                    public static void main(String[] args){
                    System.out.println("Hello World!");}
                    }
                """));
        List<TestcaseRequest> testcaseRequests2 = new ArrayList<>();
        testcaseRequests2.add(new TestcaseRequest(
            List.of("a, b", "3, 4", "5, 6"),
            "3",
            "a"
        ));

        CreateProblemRequest createProblemRequest2 = getCreateProblemRequest2(
            baseCodeRequests2, testcaseRequests2);
        Long problemId2 = problemService.createProblem(createProblemRequest2);

        //when
        List<AllProblemResponse> allProblem2 = problemService.getAllProblem(member);

        //then
        Assertions.assertThat(2).isEqualTo(allProblem2.size());

    }

    @DisplayName("문제조회 - 등록된 문제가 없으면 빈 리스트가 반환된다.")
    @Test
    void getAllProblemWithNotTest() {
        //given
        Member member = memberRepository.save(new Member("username", "password", "nickname"));
        //when
        List<AllProblemResponse> problems = problemService.getAllProblem(member);
        //then
        assertThat(problems).isEmpty();
    }

    @DisplayName("문제 상세 조회")
    @Test
    void getProblemTest() {
        //given
        Member member = memberRepository.save(new Member("username", "password", "nickname"));
        List<BaseCodeRequest> baseCodeRequests1 = new ArrayList<>();
        baseCodeRequests1.add(new BaseCodeRequest(
            LanguageType.JAVA,
            """
                    public class Main{
                    public static void main(String[] args){
                    System.out.println("Hello World!");}
                    }
                """));
        List<TestcaseRequest> testcaseRequests1 = new ArrayList<>();
        testcaseRequests1.add(new TestcaseRequest(
            List.of("a, b", "3, 4", "5, 6"),
            "3",
            "a"
        ));

        CreateProblemRequest createProblemRequest = getCreateProblemRequest(
            baseCodeRequests1, testcaseRequests1);
        Long problemId = problemService.createProblem(createProblemRequest);

        //when
        GetProblemResponse problem = problemService.getProblem(problemId, member);

        //then
        Assertions.assertThat("where is koreanCow").isEqualTo(problem.title());
    }

    private static CreateProblemRequest getCreateProblemRequest(
        List<BaseCodeRequest> baseCodeRequests,
        List<TestcaseRequest> testcaseRequests
    ) {
        return new CreateProblemRequest(
            "where is koreanCow",
            "koreanCow is delicious",
            List.of("Never eat dog"),
            60,
            1,
            List.of("String[][]", "int", "String"),
            baseCodeRequests,
            testcaseRequests
        );
    }

    private static CreateProblemRequest getCreateProblemRequest2(
        List<BaseCodeRequest> baseCodeRequests,
        List<TestcaseRequest> testcaseRequests
    ) {
        return new CreateProblemRequest(
            "where is koreanCow2",
            "koreanCow is delicious2",
            List.of("Never eat dog2"),
            60,
            1,
            List.of("String[][]", "int", "String"),
            baseCodeRequests,
            testcaseRequests
        );
    }

    @DisplayName("최근에 푼 문제의 기록을 갖고 올 수 있다.")
    @Test
    void getRecordTest() {
        //given
        Member member = new Member("test", "test123", "steet");
        memberRepository.save(member);

        Problem problem = new Problem("제목", "내용", List.of("제한사항"), 10, 10, List.of("타입"));
        problemRepository.save(problem);

        Record record = new Record("테스트코드", member, problem, "test언어", 40, null, null);
        recordRepository.save(record);

        //when
        List<RecordResponse> records = problemService.getRecord(member.getUsername());
        //then
        assertThat(records.getFirst().recordId()).isEqualTo(record.getRecordNo());
        assertThat(records.getFirst().title()).isEqualTo("제목");
        assertThat(records.getFirst().accuracy()).isEqualTo(40);
        assertThat(records.getFirst().grade()).isEqualTo(null);

    }

    @DisplayName("문제 상세 조회 시 가장 최근에 제출한 코드가 베이스 코드에 표시된다.")
    @Test
    void baseCodeTest() {
        // given
        Member member = memberRepository.save(new Member("username", "password", "nickname"));
        Problem problem = problemRepository.save(new Problem(
            "title", "content", List.of("제한 사항"), 1, 1, List.of("int", "int[]")
        ));
        testcaseRepository.save(new Testcase(problem, List.of("3", "[1, 2, 3]"),
            "[2,4,6]", "description"));
        languageRepository.save(new Language(problem, LanguageType.JAVA, "base code..."));
        languageRepository.save(new Language(problem, LanguageType.JAVASCRIPT, "base code..."));

        recordRepository.save(new Record("code1", member, problem, "JAVA", 100, null, null));
        recordRepository.save(new Record("code2", member, problem, "JAVA", 80, null, null));

        // when
        GetProblemResponse response = problemService.getProblem(problem.getProblemNo(), member);
        List<BaseCodeResponse> baseCodeResponses = response.baseCode();
        // then
        assertThat(baseCodeResponses).containsAll(List.of(
            new BaseCodeResponse(LanguageType.JAVA, "code2"),
            new BaseCodeResponse(LanguageType.JAVASCRIPT, "base code...")
        ));
    }

    @DisplayName("문제 목록 조회 시 플레이 여부와 가장 높은 정확도가 표시된다.")
    @Test
    void isPlayedAndAccuracyTest() {
        // given
        Member member = memberRepository.save(new Member("username", "password", "nickname"));
        Problem problem1 = problemRepository.save(new Problem(
            "문제 1번", "content", List.of("제한 사항"), 10, 1, List.of("int", "int[]")
        ));
        testcaseRepository.save(new Testcase(problem1, List.of("3", "[1, 2, 3]"),
            "[2,4,6]", "description"));
        languageRepository.save(new Language(problem1, LanguageType.JAVA, "base code..."));
        languageRepository.save(new Language(problem1, LanguageType.JAVASCRIPT, "base code..."));

        Problem problem2 = problemRepository.save(new Problem(
            "문제 2번", "content", List.of("제한 사항"), 10, 2, List.of("int", "int[]")
        ));
        testcaseRepository.save(new Testcase(problem1, List.of("3", "[1, 2, 3]"),
            "[2,4,6]", "description"));
        languageRepository.save(new Language(problem1, LanguageType.JAVA, "base code..."));

        recordRepository.save(new Record("code1", member, problem1, "JAVA", 60, null, null));
        recordRepository.save(new Record("code2", member, problem1, "JAVA", 100, null, null));
        recordRepository.save(new Record("code3", member, problem1, "JAVA", 80, null, null));

        // when
        List<AllProblemResponse> allProblem = problemService.getAllProblem(member);

        // then
        assertThat(allProblem).containsAll(List.of(
            new AllProblemResponse(problem1.getProblemNo(), "문제 1번", 1, 100, true),
            new AllProblemResponse(problem2.getProblemNo(), "문제 2번", 2, 0, false)
        ));
    }
}
