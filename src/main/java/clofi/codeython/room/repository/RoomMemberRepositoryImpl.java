package clofi.codeython.room.repository;

import clofi.codeython.member.domain.Member;
import clofi.codeython.room.domain.Room;
import clofi.codeython.room.domain.RoomMember;
import clofi.codeython.room.repository.jpa.RoomMemberJpaRepository;
import clofi.codeython.room.repository.role.RoomMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class RoomMemberRepositoryImpl implements RoomMemberRepository {

    private final RoomMemberJpaRepository roomMemberJpaRepository;
    @Override
    public List<RoomMember> findAllByRoom(Room room) {
        return roomMemberJpaRepository.findAllByRoom(room);
    }

    @Override
    public List<RoomMember> findAllByRoomRoomNo(Long roomNo) {
        return roomMemberJpaRepository.findAllByRoomRoomNo(roomNo);
    }

    @Override
    public void deleteByRoomAndUser(Room room, Member user) {
        roomMemberJpaRepository.deleteByRoomAndUser(room, user);
    }

    @Override
    public boolean existsRoomMemberByRoomAndUser(Room room, Member member) {
        return roomMemberJpaRepository.existsRoomMemberByRoomAndUser(room, member);
    }

    @Override
    public RoomMember findByUser(Member member) {
        return roomMemberJpaRepository.findByUser(member);
    }

    @Override
    public RoomMember findByRoomAndUser(Room room, Member member) {
        return null;
    }

    @Override
    public void save(RoomMember roomMember) {
        roomMemberJpaRepository.save(roomMember);
    }
}
