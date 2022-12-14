package com.aidansaull.crazyEights;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler
{
    private static Integer id = 0;
    Game game;

    public CustomHandshakeHandler(Game game)
    {
        this.game = game;
    }

    // Custom class for storing principal
    @Override
    protected Principal determineUser
    (
            ServerHttpRequest request,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    )
    {
        //StompPrincipal principal = new StompPrincipal(id.toString());
        StompPrincipal principal = new StompPrincipal(Integer.toString(game.players.size()));
        id++;
        return principal;
    }
}
