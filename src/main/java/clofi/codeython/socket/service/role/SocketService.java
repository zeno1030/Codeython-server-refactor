package clofi.codeython.socket.service.role;

import clofi.codeython.room.domain.RoomMember;
import clofi.codeython.socket.dto.response.SocketUserResponse;

import java.util.List;

public interface SocketService {
    List<SocketUserResponse> joinRoom(Long roomId);

    List<SocketUserResponse> leaveRoom(Long roomId, String nickName);
    List<SocketUserResponse> getSocketUserResponses(List<RoomMember> roomMemberList);
}
