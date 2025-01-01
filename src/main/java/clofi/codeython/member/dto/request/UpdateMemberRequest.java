package clofi.codeython.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateMemberRequest {

    @NotBlank(message = "닉네임은 공백일 수 없습니다.")
    @Size(min = 2, max = 10, message = "닉네임은 최소 2글자에서 최대 10글자입니다.")
    private String nickname;

    public UpdateMemberRequest(String nickname) {
        this.nickname = nickname;
    }
}
