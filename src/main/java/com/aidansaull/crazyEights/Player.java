package com.aidansaull.crazyEights;


import org.springframework.beans.factory.annotation.Autowired;
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

    Game game;


    public Player()
    {
        this.username = "test";
        hand = new ArrayList<Card>();
        score = 0;
    }
    public Player(String username, SimpMessagingTemplate simpMessagingTemplate, Game game)
    {
        this.username = username;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.game = game;
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

    private void play(Card card)
    {
        System.out.println("playing card " + card);
        hand.remove(card);
        game.discard.push(card);
        game.nextTurn();
    }

    public boolean playCard(Card card)
    {
        if (card.rank == '8') // 8, we can play
        {
            play(card);
            return true;
        }
        if (!hand.contains(card))
            return false;
        Card topCard = game.discard.peek();
        if (game.isEight && card.suit == topCard.suit) // playable if we are playing on an 8
        {
            play(card);
            return true;
        }
        if (card.suit == topCard.suit || card.rank == topCard.rank)
        {
            play(card);
            return true;
        }
        return false;
    }

    public void emptyHand()
    {
        String destination = "/queue/message";
        simpMessagingTemplate.convertAndSendToUser(username, destination, "empty");
    }
}
