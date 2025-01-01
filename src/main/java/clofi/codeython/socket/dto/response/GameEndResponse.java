package clofi.codeython.socket.dto.response;

public record GameEndResponse(
        Long userNo,
        String nickname,
        Integer grade,
        Integer gainExp
) {
}
