package clofi.codeython.room.domain;

import clofi.codeython.problem.core.domain.Problem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_no", nullable = false)
    private Long roomNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_no", nullable = false)
    private Problem problem;

    @Column(name = "room_name", unique = true, length = 50)
    private String roomName;

    @Column(name = "password", length = 4)
    private String password;

    @Column(name = "is_secret")
    private boolean isSecret;

    @Column(name = "is_solo_play")
    private boolean isSoloPlay;

    @Column(name = "invite_code", unique = true, nullable = false, length = 50)
    private String inviteCode;

    @Column(name = "limit_member_cnt", nullable = false)
    private int limitMemberCnt;

    @Column(name = "player_count", nullable = false)
    private int playerCount;

    @Column(name = "is_play", nullable = false)
    private boolean isPlay;

    @Column(name = "correct_player_count", nullable = false)
    private int correctPlayerCount;

    public Room(String roomName, Problem problem, int limitMemberCnt, boolean isSecret, String password,
        boolean isSoloPlay, String inviteCode) {
        this.roomName = roomName;
        this.problem = problem;
        this.limitMemberCnt = limitMemberCnt;
        this.isSecret = isSecret;
        this.password = password;
        this.isSoloPlay = isSoloPlay;
        this.inviteCode = inviteCode;
        this.playerCount = 0;
        this.isPlay = false;
        this.correctPlayerCount = 0;
    }

    public void changeProblem(Problem problem) {
        this.problem = problem;
    }

    public void gameEnd() {
        this.isPlay = false;
    }

    public void gameStart(int playerCount) {
        this.playerCount = playerCount;
        this.isPlay = true;
        this.correctPlayerCount = 0;
    }

    public void increaseCorrectPlayerCount() {
        this.correctPlayerCount += 1;
    }
}
