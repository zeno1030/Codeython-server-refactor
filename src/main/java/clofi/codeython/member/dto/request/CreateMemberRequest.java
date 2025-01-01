package clofi.codeython.member.dto.request;

import org.springframework.security.crypto.password.PasswordEncoder;

import clofi.codeython.member.domain.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateMemberRequest {

    @NotBlank(message = "닉네임은 공백일 수 없습니다.")
    @Size(min = 2, max = 10, message = "닉네임은 최소 2글자에서 최대 10글자입니다.")
    private String nickname;

    @NotBlank(message = "아이디는 공백일 수 없습니다.")
    @Size(min = 6, max = 20, message = "아이디는 최소 6글자에서 최대 20글자입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디는 영어 알파벳만 가능합니다.")
    private String username;

    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    @Size(min = 6, max = 20, message = "비밀번호는 최소 6글자에서 최대 20글자입니다.")
    private String password;

    public CreateMemberRequest(String nickname, String username, String password) {
        this.nickname = nickname;
        this.username = username;
        this.password = password;
    }

    public Member toMember(PasswordEncoder encoder) {
        return new Member(
            username,
            encoder.encode(password),
            nickname
        );
    }
}
