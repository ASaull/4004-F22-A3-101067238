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

    private void playCard(Card card)
    {
        hand.remove(card);
        game.discard.push(card);
    }

    public boolean playCard(Character rank, Character suit)
    {
        // First we check if we even have this card
        Card card = null;
        for (Card c : hand)
        {
            if (c.suit == suit && c.rank == rank)
            {
                card = c;
                break;
            }
        }
        if (card == null) // in this case, we do not have the card
            return false;
        Card topCard = game.discard.peek();
        if (rank == '8') // 8, we can play
        {
            playCard(card);
            return true;
        }
        if (game.isEight && card.suit == topCard.suit) // playable if we are playing on an 8
        {
            playCard(card);
            return true;
        }
        if (card.suit == topCard.suit || card.rank == topCard.rank)
        {
            playCard(card);
            return true;
        }
        return false;
    }
}
