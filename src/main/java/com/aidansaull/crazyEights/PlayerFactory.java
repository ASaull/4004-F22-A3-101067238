package com.aidansaull.crazyEights;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class PlayerFactory
{
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    public Player createInstance(String username)
    {
        return new Player(username, simpMessagingTemplate);
    }
}
