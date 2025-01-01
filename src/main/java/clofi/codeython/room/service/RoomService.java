package clofi.codeython.room.service;

import static clofi.codeython.socket.dto.response.DataType.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import clofi.codeython.problem.core.domain.Record;
import clofi.codeython.problem.core.repository.RecordRepository;
import clofi.codeython.socket.dto.response.GameEndResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import clofi.codeython.member.domain.Member;
import clofi.codeython.member.repository.MemberRepository;
import clofi.codeython.problem.core.domain.Problem;
import clofi.codeython.problem.core.repository.ProblemRepository;
import clofi.codeython.room.domain.Room;
import clofi.codeython.room.domain.RoomMember;
import clofi.codeython.room.dto.request.ChangeProblemRequest;
import clofi.codeython.room.dto.request.CreateRoomRequest;
import clofi.codeython.room.dto.request.WaitRoomRequest;
import clofi.codeython.room.dto.response.AllRoomResponse;
import clofi.codeython.room.dto.response.CreateRoomResponse;
import clofi.codeython.room.dto.response.RoomResponse;
import clofi.codeython.room.repository.RoomMemberRepository;
import clofi.codeython.room.repository.RoomRepository;
import clofi.codeython.security.CustomMemberDetails;
import clofi.codeython.socket.dto.response.ChangeProblemResponse;
import clofi.codeython.socket.dto.response.DataResponse;
import clofi.codeython.socket.dto.response.SocketUserResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^\\d{4}$");

    private final RoomRepository roomRepository;
    private final ProblemRepository problemRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final RecordRepository recordRepository;

    public RoomResponse joinRoomWithPassword(WaitRoomRequest request, Long roomId, CustomMemberDetails userDetails) {
        Member member = memberRepository.findByUsername(userDetails.getUsername());
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new IllegalArgumentException("방이 존재하지 않습니다."));

        List<RoomMember> roomMember = roomMemberRepository.findAllByRoomRoomNo(room.getRoomNo());
        if (room.isSecret()) {
            if (!room.getPassword().equals(request.password())) {
                throw new IllegalArgumentException("비밀번호가 틀립니다");
            }
        } else {
            if (request.password() != null) {
                throw new IllegalArgumentException("공개방은 비밀번호가 필요 없습니다");
            }
        }

        if (roomMember.size() >= room.getLimitMemberCnt()) {
            throw new IllegalArgumentException("방이 꽉 찼습니다");
        }
        if (roomMember.isEmpty()) {
            return processRoomJoin(room, member, true);
        }
        return processRoomJoin(room, member, false);
    }

    public RoomResponse joinRoomWithInviteCode(String inviteCode, CustomMemberDetails userDetails) {
        Member member = memberRepository.findByUsername(userDetails.getUsername());
        Room room = roomRepository.findByInviteCode(inviteCode);
        if (room == null) {
            throw new IllegalArgumentException("방이 존재하지 않습니다");
        }
        List<RoomMember> roomMember = roomMemberRepository.findAllByRoomRoomNo(room.getRoomNo());
        if (roomMember.size() >= room.getLimitMemberCnt()) {
            throw new IllegalArgumentException("방이 꽉 찼습니다");
        }
        return processRoomJoin(room, member, false);
    }

    public CreateRoomResponse createRoom(CreateRoomRequest createRoomRequest) {
        if (roomRepository.existsByRoomName(createRoomRequest.getRoomName())) {
            throw new IllegalArgumentException("이미 만들어진 경기장 이름입니다.");
        }

        if (createRoomRequest.getIsSecret()) {
            secretRoomPasswordValidate(createRoomRequest.getPassword());
        }

        if (!(createRoomRequest.getLimitMemberCnt() == 2 || createRoomRequest.getLimitMemberCnt() == 4
            || createRoomRequest.getLimitMemberCnt() == 6)) {
            throw new IllegalArgumentException("인원 제한 수는 2, 4, 6 중 하나여야 합니다.");
        }
        Problem problem = problemRepository.findByProblemNo(createRoomRequest.getProblemId());
        if (problem == null) {
            throw new IllegalArgumentException("해당 문제가 없습니다.");
        }

        UUID uuid = UUID.randomUUID();
        String inviteCode = uuid.toString().substring(0, uuid.toString().indexOf("-"));

        Room room = roomRepository.save(createRoomRequest.toRoom(problem, inviteCode));

        return CreateRoomResponse.of(room);
    }

    public List<AllRoomResponse> getAllRoom() {
        List<Room> rooms = roomRepository.findAll();

        return rooms.stream()
            .filter(room -> !room.isPlay())
            .map(room -> {
                List<RoomMember> roomMembers = roomMemberRepository.findAllByRoom(room);
                int playMemberCount = roomMembers.size();
                return AllRoomResponse.of(room, playMemberCount);
            })
            .collect(Collectors.toList());
    }

    public Long changeProblem(Long roomId, ChangeProblemRequest changeProblemRequest) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new IllegalArgumentException("방이 존재하지 않습니다."));
        Problem problem = problemRepository.findByProblemNo(changeProblemRequest.problemId());
        if (problem == null) {
            throw new IllegalArgumentException("문제가 존재하지 않습니다.");
        }
        room.changeProblem(problem);
        notifyChangeProblem(room, problem);
        return room.getRoomNo();
    }

    private void secretRoomPasswordValidate(String password) {
        if (!PASSWORD_PATTERN.matcher(password).find()) {
            throw new IllegalArgumentException("비밀번호는 4자리 숫자여야 합니다.");
        }
    }

    private RoomResponse processRoomJoin(Room room, Member member, Boolean isOwner) {
        Problem problem = problemRepository.findByProblemNo(room.getProblem().getProblemNo());
        RoomMember roomMember = roomMemberRepository.findByUser(member);
        if (roomMember != null) {
            roomMember.updateRoomAndIsOwner(room, isOwner);
        } else {
            roomMemberRepository.save(new RoomMember(room, member, isOwner));
        }

        notifyRoomParticipants(room, member);
        return RoomResponse.of(room, problem);
    }

    private void notifyRoomParticipants(Room room, Member member) {
        int level = 1;
        Integer exp = member.getExp();
        if (exp >= 100) {
            level = exp / 100 + 1;
            exp = exp % 100;
        }
        RoomMember roomMemberUser = roomMemberRepository.findByUser(member);
        SocketUserResponse socketUserResponse = new SocketUserResponse(member.getNickname(), level, exp,
            roomMemberUser.isOwner());
        DataResponse<SocketUserResponse> socketUserResponseDataResponse = new DataResponse<>(socketUserResponse,
            USER);
        messagingTemplate.convertAndSend("/sub/room/" + room.getRoomNo(), socketUserResponseDataResponse);
    }

    private void notifyChangeProblem(Room room, Problem problem) {
        ChangeProblemResponse changeProblemResponse = new ChangeProblemResponse(problem.getProblemNo(),
            problem.getTitle(), problem.getLimitTime(),
            problem.getDifficulty());
        DataResponse<ChangeProblemResponse> changeProblemResponseDataResponse = new DataResponse<>(
            changeProblemResponse, GAME_CHANGE);
        messagingTemplate.convertAndSend("/sub/room/" + room.getRoomNo(), changeProblemResponseDataResponse);

    }

    public void gameStart(Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new IllegalArgumentException("없는 방 번호입니다."));
        List<RoomMember> roomMembers = roomMemberRepository.findAllByRoom(room);
        if (roomMembers.size() == 1) {
            throw new IllegalArgumentException("혼자서 게임을 시작할 수 없습니다.");
        }
        room.gameStart(roomMembers.size());
        roomMembers.forEach(RoomMember::resetGameStatus);
    }

    public List<GameEndResponse> getGameResult(Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new IllegalArgumentException("없는 방 번호입니다."));
        if (!room.isPlay()) {
            return List.of();
        }
        room.gameEnd();
        roomRepository.saveAndFlush(room);

        List<RoomMember> roomMembers = roomMemberRepository.findAllByRoom(room);
        Problem problem = problemRepository.findByProblemNo(room.getProblem().getProblemNo());

        int totalPlayerCount = room.getPlayerCount();
        roomMembers.sort((rm1, rm2) -> rm2.getAccuracy() - rm1.getAccuracy());

        int preAccuracy = -1;
        int grade = room.getCorrectPlayerCount() + 1;
        int tie = 0;

        List<GameEndResponse> gameEndResponses = new ArrayList<>();
        for (RoomMember roomMember : roomMembers) {
            if (roomMember.isAlreadyCorrected()) {
                gameEndResponses.add(new GameEndResponse(roomMember.getUser().getUserNo(), roomMember.getUser().getNickname(), roomMember.getGrade(), roomMember.getGainExp()));
                continue;
            }

            int accuracy = roomMember.getAccuracy();
            int gainExp = (int) (accuracy * totalPlayerCount * (0.1 - (grade - 1) * 0.02));
            gameEndResponses.add(new GameEndResponse(roomMember.getUser().getUserNo(), roomMember.getUser().getNickname(), grade, gainExp));
            roomMember.getUser().increaseExp(gainExp);
            recordRepository.save(Record.of(roomMember.getUser(), problem, accuracy, grade, room.getPlayerCount()));

            if (preAccuracy == roomMember.getAccuracy()) {
                tie++;
            } else {
                grade = grade + tie + 1;
                tie = 0;
            }
            preAccuracy = accuracy;
        }

        return gameEndResponses;
    }
}
