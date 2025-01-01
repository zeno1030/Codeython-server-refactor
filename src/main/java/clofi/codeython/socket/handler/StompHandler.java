package clofi.codeython.socket.handler;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import clofi.codeython.security.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {
    private static final String SIMP_SESSION_ID = "simpSessionId";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final JWTUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        log.info("message: {}", message);
        log.info("accessor: {}", accessor);
        log.info("channel: {}", channel);
        if (StompCommand.CONNECT == accessor.getCommand()) {
            validateToken(accessor);
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            String sessionId = message.getHeaders().get(SIMP_SESSION_ID, String.class);
            String token = validateToken(accessor);
            log.info("SUBSCRIBED {}, {}", sessionId, token);
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            String sessionId = message.getHeaders().get(SIMP_SESSION_ID, String.class);
            String token = validateToken(accessor);
            log.info("DISCONNECTED {}, {}", sessionId, token);
        }
        return message;
    }

    private String validateToken(StompHeaderAccessor accessor) {
        String bearerToken = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER);
        String token = extractToken(bearerToken);
        jwtUtil.isExpired(token);
        return token;
    }

    public String extractToken(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
