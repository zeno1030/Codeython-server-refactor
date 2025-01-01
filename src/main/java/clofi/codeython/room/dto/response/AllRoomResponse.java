package clofi.codeython.room.dto.response;

import clofi.codeython.room.domain.Room;

public record AllRoomResponse(
    Long roomId,
    String roomName,
    String problemTitle,
    int limitMemberCnt,
    int playMemberCnt,
    boolean isSecret,
    boolean isSoloPlay
) {
    public static AllRoomResponse of(Room room, int playMemberCnt) {
        return new AllRoomResponse(
            room.getRoomNo(),
            room.getRoomName(),
            room.getProblem().getTitle(),
            room.getLimitMemberCnt(),
            playMemberCnt,
            room.isSecret(),
            room.isSoloPlay()
        );
    }
}
