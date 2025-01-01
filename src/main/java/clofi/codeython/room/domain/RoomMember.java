package clofi.codeython.room.domain;

import clofi.codeython.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class RoomMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_member_no", nullable = false)
    private Long roomMemberNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_no", nullable = false)
    private Room room;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    private Member user;

    @Column(name = "is_owner")
    private boolean isOwner;

    @Column(name = "accuracy")
    private int accuracy;

    @Column(name = "grade")
    private int grade;

    @Column(name = "gain_exp")
    private int gainExp;

    public RoomMember(Room room, Member user, boolean isOwner) {
        this.room = room;
        this.user = user;
        this.isOwner = isOwner;
    }

    public void updateOwner(boolean b) {
        this.isOwner = b;
    }

    public void updateAccuracy(int accuracy) {
        this.accuracy = Math.max(this.accuracy, accuracy);
    }

    public void resetGameStatus() {
        this.accuracy = 0;
        this.grade = 0;
        this.gainExp = 0;
    }

    public boolean isAlreadyCorrected() {
        return this.accuracy == 100;
    }

    public void updateGradeAndGainExp(int grade, int gainExp) {
        this.grade = grade;
        this.gainExp = gainExp;
    }

    public void updateRoomAndIsOwner(Room room, Boolean isOwner) {
        this.room = room;
        this.isOwner = isOwner;
    }
}
