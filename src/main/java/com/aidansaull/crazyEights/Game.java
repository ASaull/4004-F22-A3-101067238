package com.aidansaull.crazyEights;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.List;

@Component
public class Game
{
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    List<Player> players;
    boolean started = false;
    Stack<Card> deck;
    Stack<Card> discard;
    boolean direction;
    Integer currentPlayer;

    @PostConstruct
    void init()
    {
        System.out.println("Created Game");
        newGame();
    }

    private Card drawNonEight()
    {
        Card card;
        card = drawCard();
        while(card.rank == '8')
        {
            deck.push(card);
            Collections.shuffle(deck);
            card = drawCard();
        }
        return card;
    }


    void startGame()
    {
        currentPlayer = 0;
        started = true;
        // We can now tell the players that the game has started
        drawTopCard();
        sendScore();
        dealHands();
    }

    private void drawTopCard()
    {
        discard.push(drawNonEight());
    }

    private void sendScore()
    {
        List<Integer> scores = new ArrayList<Integer>();
        for (Player player : players)
        {
            scores.add(player.score);
        }
        Score score = new Score(direction, scores, currentPlayer, discard.peek(), deck.size());
        String destination = "/topic/score";
        simpMessagingTemplate.convertAndSend(destination, score);
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
        deck = new Stack<Card>();
        discard = new Stack<Card>();
        direction = true;
        shuffleDeck();
    }

    private void dealHands()
    {
        for (int i = 0; i < 5; i++)
        {
            for (Player player : players)
            {
                player.addCard(drawCard());
            }
        }
    }

    private void shuffleDeck()
    {
        List<Character> ranks = Arrays.asList('A', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K');
        List<Character> suits = Arrays.asList('H', 'S', 'C', 'D');
        for (Character rank : ranks)
        {
            for (Character suit : suits)
            {
                Card card = new Card(rank, suit);
                deck.add(card);
            }
        }
        Collections.shuffle(deck);
    }

    public void addPlayer(Player player)
    {
        players.add(player);
        if (players.size() == 4)
        {
            startGame();
        }
    }

    public boolean isStarted()
    {
        return started;
    }

    public Card drawCard()
    {
        Card card = deck.pop();
        return card;
    }
}
