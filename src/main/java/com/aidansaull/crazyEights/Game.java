package com.aidansaull.crazyEights;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class Game
{
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    List<Player> players;
    private boolean started = false;

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
        Message greeting = new Message(message);
        simpMessagingTemplate.convertAndSendToUser(id.toString(), destination, greeting);
    }

    void sendToAll(String destination, String message)
    {
        Message greeting = new Message(message);
        simpMessagingTemplate.convertAndSend(destination, greeting);
    }

    public void newGame()
    {
        players = new ArrayList<Player>();
    }

    public void addPlayer(Player player)
    {
        players.add(player);
        if (players.size() == 4)
            started = true;
    }

    public boolean isStarted()
    {
        return started;
    }
}
