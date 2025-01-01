package clofi.codeython.room.dto.request;

import clofi.codeython.problem.core.domain.Problem;
import clofi.codeython.room.domain.Room;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoomRequest {

    @NotBlank(message = "경기장 제목은 공백일 수 없습니다.")
    private String roomName;

    @NotNull(message = "문제 아이디는 공백일 수 없습니다.")
    private Long problemId;

    @NotNull(message = "인원 제한 수는 공백일 수 없습니다.")
    private int limitMemberCnt;

    private Boolean isSecret;

    private String password;

    private Boolean isSoloPlay;

    public Room toRoom(Problem problem, String inviteCode) {
        return new Room(
            roomName,
            problem,
            limitMemberCnt,
            isSecret,
            password,
            isSoloPlay,
            inviteCode
        );
    }

}
