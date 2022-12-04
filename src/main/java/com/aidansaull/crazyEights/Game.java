package com.aidansaull.crazyEights;

import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class Game
{
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public List<Player> players;
    private boolean started = false;
    List<Card> deck;
    public Stack<Card> discard;

    @PostConstruct
    void init()
    {
        System.out.println("Created Game");
    }

    void startGame() throws InterruptedException
    {
        dealHands();
        discard.push(drawCard());
        // We can now tell the players that the game has started
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
        started = false;
        players = new ArrayList<Player>();
        deck = new ArrayList<Card>();
        discard = new Stack<Card>();
        shuffleDeck();
    }

    private void dealHands()
    {
        for (int i = 0; i < 5; i++)
        {
            for (Player player : players)
            {
                player.hand.add(drawCard());
            }
        }
    }

    private void shuffleDeck()
    {
        List<Character> ranks = Arrays.asList('A', '2', '3', '4', '5', '6', '7', '8', '9', 'J', 'Q', 'K');
        List<Character> suits = Arrays.asList('H', 'S', 'C', 'D');
        for (Character rank : ranks)
        {
            for (Character suit : suits)
            {
                Card card = new Card(rank, suit);
                deck.add(card);
            }
        }
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

    public Card drawCard()
    {
        int rand = ThreadLocalRandom.current().nextInt(0, deck.size());
        Card card = deck.remove(rand);
        return card;
    }
}
