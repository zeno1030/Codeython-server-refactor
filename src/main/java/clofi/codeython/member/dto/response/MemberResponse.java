package clofi.codeython.member.dto.response;

import clofi.codeython.member.domain.Member;

public record MemberResponse(
    String nickname,
    Integer level,
    Integer exp) {
    public static MemberResponse of(Member member, Integer level, Integer exp) {
        return new MemberResponse(
            member.getNickname(),
            level,
            exp
        );
    }
}