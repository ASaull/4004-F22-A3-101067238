package com.aidansaull.crazyEights;


import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Player
{
    List<Card> hand;
    Integer score;
    String username;

    SimpMessagingTemplate simpMessagingTemplate;


    public Player()
    {
        this.username = "test";
        hand = new ArrayList<Card>();
        score = 0;
    }
    public Player(String username, SimpMessagingTemplate simpMessagingTemplate)
    {
        this.username = username;
        this.simpMessagingTemplate = simpMessagingTemplate;
        hand = new ArrayList<Card>();
        score = 0;
    }

    public void addCard(Card card)
    {
        hand.add(card);
        sendCard(card);
    }

    private void sendCard(Card card)
    {
        String destination = "/queue/card";
        simpMessagingTemplate.convertAndSendToUser(username, destination, card);
    }

    public boolean playCard(Character rank, Character suit)
    {
        return false;
    }
}
