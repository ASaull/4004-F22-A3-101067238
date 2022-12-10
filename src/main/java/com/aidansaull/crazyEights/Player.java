package com.aidansaull.crazyEights;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class Player
{
    List<Card> hand;
    Integer score;
    String username;

    SimpMessagingTemplate simpMessagingTemplate;

    Game game;
    Integer numDraws = 0;


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
        System.out.println(username + " adding card to player " + card);
        hand.add(card);
        sendCard(card);
        numDraws++;
    }

    private void sendCard(Card card)
    {
        String destination = "/queue/card";
        simpMessagingTemplate.convertAndSendToUser(username, destination, card);
    }

    private void play(Card card)
    {
        System.out.println("playing card " + card);
        // If eight, we remove any eight from their hand.
        // This causes no visual error since eight suits are rendered client side.
        if (card.rank == '8')
        {
            for (Card c : hand)
            {
                if (c.rank == '8')
                {
                    hand.remove(c);
                    break;
                }
            }
        }
        else // required so we don't remove two eights by accident
        {
            hand.remove(card);
        }
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

    public void nextTurn()
    {
        numDraws = 0;
    }

    public Integer score()
    {
        System.out.println("STARTING TO SCORE SCORE IS NOW " + score + "  -----------------------------------");
        for (Card card : hand)
        {
            System.out.println("card " + card);
            if (card.rank == '8')
                score += 50;
            else if (Arrays.asList(new Character[] {'K','Q','J'}).contains(card.rank))
                score += 10;
            else if (card.rank == 'A')
                score += 1;
            else if (card.rank == 'T')
                score += 10;
            else
                score += Integer.parseInt(card.rank.toString());
            System.out.println("score is now " + score);
        }
        return score;
    }
}
