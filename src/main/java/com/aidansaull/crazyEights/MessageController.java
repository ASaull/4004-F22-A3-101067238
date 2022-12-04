package com.aidansaull.crazyEights;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.MessageMapping;

@Controller
public class MessageController
{
    @Autowired
    Game game;

    @Autowired
    PlayerFactory playerFactory;

    @MessageMapping("/hello")
    public void hello(Message message)
    {
        game.addPlayer(playerFactory.createInstance(message.getContent()));
    }

}
