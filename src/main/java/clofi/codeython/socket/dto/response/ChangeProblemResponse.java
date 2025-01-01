package clofi.codeython.socket.dto.response;

public record ChangeProblemResponse(
    Long problemNo,
    String problemTitle,
    Integer limitTime,
    Integer difficulty
) {
}
