package com.aidansaull.crazyEights;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.MessageMapping;

import java.security.Principal;

@Controller
public class MessageController
{
    @Autowired
    Game game;

    @MessageMapping("/hello")
    public void hello()
    {
        game.addPlayer(new Player());
    }

}
