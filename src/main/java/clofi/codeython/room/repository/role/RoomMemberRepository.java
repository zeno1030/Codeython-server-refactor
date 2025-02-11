package clofi.codeython.room.repository.role;

import clofi.codeython.member.domain.Member;
import clofi.codeython.room.domain.Room;
import clofi.codeython.room.domain.RoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomMemberRepository{
    List<RoomMember> findAllByRoom(Room room);

    List<RoomMember> findAllByRoomRoomNo(Long roomNo);

    void deleteByRoomAndUser(Room room, Member user);

    boolean existsRoomMemberByRoomAndUser(Room room, Member member);

    RoomMember findByUser(Member member);

    RoomMember findByRoomAndUser(Room room, Member member);

    void save(RoomMember roomMember);
}
