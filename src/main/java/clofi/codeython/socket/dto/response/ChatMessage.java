package clofi.codeython.socket.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessage {
    private String from;
    private String message;

    public ChatMessage(String from, String message) {
        this.from = from;
        this.message = message;
    }
}
