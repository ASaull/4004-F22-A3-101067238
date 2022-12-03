package com.aidansaull.crazyEights;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Game
{
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @PostConstruct
    void init()
    {
        System.out.println("Created Game");
    }

    void startGame() throws InterruptedException
    {
        Thread.sleep(1000);
        sendToPlayer(0, "/queue/message", "Hello! -From the game <3");
        sendToAll("/topic/greetings", "Hello to all players");
        System.out.println("sent");
    }

    void sendToPlayer(Integer id, String destination, String message)
    {
        Greeting greeting = new Greeting(message);
        simpMessagingTemplate.convertAndSendToUser(id.toString(), destination, greeting);
    }

    void sendToAll(String destination, String message)
    {
        Greeting greeting = new Greeting(message);
        simpMessagingTemplate.convertAndSend(destination, greeting);
    }
}
