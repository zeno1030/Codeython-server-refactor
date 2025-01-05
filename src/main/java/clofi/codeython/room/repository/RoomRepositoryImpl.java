package clofi.codeython.room.repository;

import clofi.codeython.room.domain.Room;
import clofi.codeython.room.repository.jpa.RoomJpaRepository;
import clofi.codeython.room.repository.role.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class RoomRepositoryImpl implements RoomRepository {

    private final RoomJpaRepository roomJpaRepository;

    @Override
    public boolean existsByRoomName(String roomName) {
        return false;
    }

    @Override
    public Room findByInviteCode(String inviteCode) {
        return null;
    }

    @Override
    public Room findById(Long id) {
        return roomJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("방이 존재하지 않습니다."));
    }

    @Override
    public void deleteById(Long id) {
        roomJpaRepository.deleteById(id);
    }
}
