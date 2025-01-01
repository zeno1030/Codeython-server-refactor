package clofi.codeython.member.domain;

import clofi.codeython.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_no", nullable = false)
    private Long userNo;

    @Column(name = "username", unique = true, nullable = false, length = 20)
    private String username;

    @Column(name = "password", nullable = false, length = 128)
    private String password;

    @Column(name = "nickname", unique = true, nullable = false, length = 10)
    private String nickname;

    @Column(name = "exp", nullable = false, columnDefinition = "int default 0")
    private Integer exp;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public Member(String username, String password, String nickname, Integer exp) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.exp = exp;
    }

    public Member(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }

    public Member(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void updateNickName(String nickname) {
        this.nickname = nickname;
    }

    public void increaseExp(int gainExp) {
        this.exp += gainExp;
    }
}
