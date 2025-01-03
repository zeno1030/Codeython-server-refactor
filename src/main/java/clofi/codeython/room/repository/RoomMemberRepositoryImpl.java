package clofi.codeython.room.repository;

import clofi.codeython.member.domain.Member;
import clofi.codeython.room.domain.Room;
import clofi.codeython.room.domain.RoomMember;
import clofi.codeython.room.repository.role.RoomMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class RoomMemberRepositoryImpl implements RoomMemberRepository {
    @Override
    public List<RoomMember> findAllByRoom(Room room) {
        return List.of();
    }

    @Override
    public List<RoomMember> findAllByRoomRoomNo(Long roomNo) {
        return List.of();
    }

    @Override
    public void deleteByRoomAndUser(Room room, Member user) {

    }

    @Override
    public boolean existsRoomMemberByRoomAndUser(Room room, Member member) {
        return false;
    }

    @Override
    public RoomMember findByUser(Member member) {
        return null;
    }

    @Override
    public Optional<RoomMember> findByRoomAndUser(Room room, Member member) {
        return Optional.empty();
    }
}
