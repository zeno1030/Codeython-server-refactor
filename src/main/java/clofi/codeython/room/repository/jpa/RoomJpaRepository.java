package clofi.codeython.room.repository.jpa;

import clofi.codeython.room.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomJpaRepository extends JpaRepository<Room, Long> {
    boolean existsByRoomName(String roomName);

    Room findByInviteCode(String inviteCode);
}
