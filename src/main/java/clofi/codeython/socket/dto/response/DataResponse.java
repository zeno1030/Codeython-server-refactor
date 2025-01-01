package clofi.codeython.socket.dto.response;

public record DataResponse<T>(T data, DataType type) {
}
