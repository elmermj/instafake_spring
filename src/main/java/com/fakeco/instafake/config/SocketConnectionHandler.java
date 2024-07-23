package com.fakeco.instafake.config;

import com.fakeco.instafake.controllers.PostController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SocketConnectionHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(SocketConnectionHandler.class);
    List<WebSocketSession> webSocketSessions = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void
    afterConnectionEstablished(WebSocketSession session) throws Exception {

        super.afterConnectionEstablished(session);
        logger.debug(session.getId() + " Connected");

        webSocketSessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session,CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        logger.debug(session.getId() + " DisConnected");

        webSocketSessions.remove(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

        super.handleMessage(session, message);

        for (WebSocketSession webSocketSession : webSocketSessions) {
            if (session == webSocketSession) continue;

            webSocketSession.sendMessage(message);
        }
    }
}