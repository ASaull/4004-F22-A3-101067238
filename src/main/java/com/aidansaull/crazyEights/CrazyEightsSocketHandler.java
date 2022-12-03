package com.aidansaull.crazyEights;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class CrazyEightsSocketHandler extends TextWebSocketHandler
{
    /**
     * This is what will run when a player tries to connect
     */
    @Override
    public void afterConnectionEstablished(final WebSocketSession session) throws Exception
    {

    }
}
