package clofi.codeython.room.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import clofi.codeython.problem.core.domain.Problem;
import clofi.codeython.problem.core.repository.ProblemRepository;
import clofi.codeython.room.dto.request.CreateRoomRequest;
import clofi.codeython.room.dto.response.AllRoomResponse;
import clofi.codeython.room.dto.response.CreateRoomResponse;
import clofi.codeython.room.repository.RoomRepository;

@SpringBootTest
class RoomServiceTest {

    @Autowired
    private ProblemRepository problemRepository;
    @Autowired
    private RoomService roomService;
    @Autowired
    private RoomRepository roomRepository;

    @AfterEach
    void afterEach() {
        roomRepository.deleteAllInBatch();
        problemRepository.deleteAllInBatch();
    }

    @DisplayName("경기장 생성")
    @Test
    void createRoomTest() {
        //given
        Problem problem = new Problem(
            "where is koreanCow",
            "koreanCow is delicious",
            List.of("Never eat dog"),
            60,
            1,
            List.of("String[][]", "int", "String")
        );
        Problem saveProblem = problemRepository.save(problem);

        //when
        CreateRoomRequest createRoomRequest = new CreateRoomRequest(
            "경기장",
            saveProblem.getProblemNo(),
            4,
            true,
            "0000",
            true
        );
        CreateRoomResponse room = roomService.createRoom(createRoomRequest);

        //then
        Assertions.assertThat(room.password()).isEqualTo("0000");
    }

    @DisplayName("비밀번호가 숫자가 아닐경우 예외 발생")
    @Test
    void createRoomExceptionTest() {
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
        CreateRoomRequest createRoomRequest = new CreateRoomRequest(
            "경기장",
            1L,
            4,
            true,
            "비밀번호",
            true
        );

        //then
        assertThatThrownBy(() ->
            roomService.createRoom(createRoomRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("비밀번호는 4자리 숫자여야 합니다.");
    }

    @DisplayName("비밀번호가 4자리가 아닐경우 예외 발생")
    @Test
    void createRoomPasswordExceptionTest() {
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
        CreateRoomRequest createRoomRequest = new CreateRoomRequest(
            "경기장",
            1L,
            4,
            true,
            "00000",
            true
        );

        //then
        assertThatThrownBy(() ->
            roomService.createRoom(createRoomRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("비밀번호는 4자리 숫자여야 합니다.");
    }

    @DisplayName("인원 제한 수가 2,4,6이 아닐 경우 예외")
    @Test
    void createRoomLimitMemberCntExceptionTest() {
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
        CreateRoomRequest createRoomRequest = new CreateRoomRequest(
            "경기장",
            1L,
            5,
            true,
            "0000",
            true
        );

        //then
        assertThatThrownBy(() ->
            roomService.createRoom(createRoomRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("인원 제한 수는 2, 4, 6 중 하나여야 합니다.");
    }

    @DisplayName("경기장 리스트 조회")
    @Test
    void getAllRoomsTest() {
        //given
        Problem problem1 = new Problem(
            "where is koreanCow",
            "koreanCow is delicious",
            List.of("Never eat dog"),
            60,
            1,
            List.of("String[][]", "int", "String")
        );
        Problem problem2 = new Problem(
            "where is koreanCow another",
            "koreanCow is delicious",
            List.of("Never eat dog"),
            60,
            1,
            List.of("String[][]", "int", "String")
        );
        Problem saveProblem1 = problemRepository.save(problem1);
        Problem saveProblem2 = problemRepository.save(problem2);

        CreateRoomRequest createRoomRequest1 = new CreateRoomRequest(
            "경기장1",
            saveProblem1.getProblemNo(),
            4,
            true,
            "0000",
            true
        );
        CreateRoomRequest createRoomRequest2 = new CreateRoomRequest(
            "경기장2",
            saveProblem2.getProblemNo(),
            4,
            true,
            "0000",
            true
        );

        //when
        roomService.createRoom(createRoomRequest1);
        roomService.createRoom(createRoomRequest2);

        List<AllRoomResponse> allRoom = roomService.getAllRoom();

        //then
        Assertions.assertThat(allRoom.size()).isEqualTo(2);

    }

}
