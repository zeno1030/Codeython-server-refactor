package clofi.codeython.room.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import clofi.codeython.room.dto.request.ChangeProblemRequest;
import clofi.codeython.room.dto.request.CreateRoomRequest;
import clofi.codeython.room.dto.request.WaitRoomRequest;
import clofi.codeython.room.dto.response.AllRoomResponse;
import clofi.codeython.room.dto.response.CreateRoomResponse;
import clofi.codeython.room.dto.response.RoomResponse;
import clofi.codeython.room.service.RoomService;
import clofi.codeython.security.CustomMemberDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/api/rooms/{roomId}")
    public RoomResponse joinRoomWithPassword(
        @RequestBody WaitRoomRequest request,
        @PathVariable("roomId") Long roomId,
        @AuthenticationPrincipal CustomMemberDetails userDetails) {
        return roomService.joinRoomWithPassword(request, roomId, userDetails);
    }

    @PostMapping("/api/rooms/direct/{inviteCode}")
    public RoomResponse joinRoomWithInviteCode(
        @PathVariable("inviteCode") String inviteCode,
        @AuthenticationPrincipal CustomMemberDetails userDetails) {
        return roomService.joinRoomWithInviteCode(inviteCode, userDetails);
    }

    @PostMapping("/api/rooms")
    public ResponseEntity<CreateRoomResponse> createRoom(
        @RequestBody @Valid CreateRoomRequest createRoomRequest) {

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(roomService.createRoom(createRoomRequest));
    }

    @GetMapping("/api/rooms")
    public ResponseEntity<List<AllRoomResponse>> getAllRoom() {

        return ResponseEntity.ok(roomService.getAllRoom());

    }

    @PatchMapping("/api/rooms/{roomId}")
    public ResponseEntity<Long> changeProblem(
        @PathVariable("roomId") Long roomId,
        @RequestBody ChangeProblemRequest changeProblemRequest
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(roomService.changeProblem(roomId, changeProblemRequest));
    }
}
