package clofi.codeython.room.repository.role;


import clofi.codeython.room.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository{
    boolean existsByRoomName(String roomName);

    Room findByInviteCode(String inviteCode);

    Room findById(Long id);
}
