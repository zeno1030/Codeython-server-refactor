package clofi.codeython.socket.dto.response;

import clofi.codeython.member.domain.Member;

public record SocketUserResponse(
    String nickname,
    Integer level,
    Integer exp,
    Boolean isOwner
) {
    public static SocketUserResponse of(Member member, Integer level, Integer exp, Boolean isOwner) {
        return new SocketUserResponse(
            member.getNickname(),
            level,
            exp,
            isOwner
        );
    }
}
