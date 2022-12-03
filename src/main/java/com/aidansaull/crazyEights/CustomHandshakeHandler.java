package com.aidansaull.crazyEights;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler
{
    private static Integer id = 0;

    // Custom class for storing principal
    @Override
    protected Principal determineUser(
            ServerHttpRequest request,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        StompPrincipal principal = new StompPrincipal(id.toString());
        id++;
        return principal;
    }
}
