package clofi.codeython.socket.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clofi.codeython.socket.service.role.SocketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import clofi.codeython.member.domain.Member;
import clofi.codeython.member.repository.role.MemberRepository;
import clofi.codeython.room.domain.Room;
import clofi.codeython.room.domain.RoomMember;
import clofi.codeython.room.repository.role.RoomMemberRepository;
import clofi.codeython.room.repository.role.RoomRepository;
import clofi.codeython.socket.dto.response.SocketUserResponse;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SocketServiceImpl implements SocketService {

    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;

    public List<SocketUserResponse> joinRoom(Long roomId) {
        Room room = roomRepository.findById(roomId);
        List<RoomMember> roomMemberList = roomMemberRepository.findAllByRoomRoomNo(room.getRoomNo());

        return getSocketUserResponses(roomMemberList);
    }

    public List<SocketUserResponse> leaveRoom(Long roomId, String nickName) {
        Room room = roomRepository.findById(roomId);
        Member member = memberRepository.findByNickname(nickName);
        RoomMember roomMemberUser = roomMemberRepository.findByUser(member);
        List<RoomMember> roomMemberList = roomMemberRepository.findAllByRoomRoomNo(room.getRoomNo());

        updateNewOwner(roomId, roomMemberUser, room, member, roomMemberList);

        roomMemberList.removeIf(m -> m.getUser().equals(member));

        return getSocketUserResponses(roomMemberList);
    }

    public List<SocketUserResponse> getSocketUserResponses(List<RoomMember> roomMemberList) {
        return roomMemberList.stream().map(m -> {
            Member member = m.getUser();
            Map<String, Integer> levelAndExp = calculateLevelAndExp(member);
            return SocketUserResponse.of(member, levelAndExp.get("level"), levelAndExp.get("exp"), m.isOwner());
        }).toList();
    }

    private Map<String, Integer> calculateLevelAndExp(Member member) {
        int exp = member.getExp();
        int level = 1;
        if (exp >= 100) {
            level = exp / 100 + 1;
            exp = exp % 100;
        }
        Map<String, Integer> result = new HashMap<>();
        result.put("level", level);
        result.put("exp", exp);
        return result;
    }

    private void updateNewOwner(Long roomId, RoomMember roomMemberUser, Room room, Member member, List<RoomMember> roomMemberList) {
        if (roomMemberUser.isOwner()) {
            roomMemberRepository.deleteByRoomAndUser(room, member);
            if (roomMemberList.size() > 1) {
                switchOwner(member, roomMemberList);
            } else {
                roomRepository.deleteById(roomId);
            }
        } else {
            roomMemberRepository.deleteByRoomAndUser(room, member);
        }
    }

    private void switchOwner(Member member, List<RoomMember> roomMemberList) {
        RoomMember newOwner =
                roomMemberList.get(0).getUser().equals(member) ? roomMemberList.get(1) : roomMemberList.get(0);
        newOwner.updateOwner(true);
        roomMemberRepository.save(newOwner);
    }
}
