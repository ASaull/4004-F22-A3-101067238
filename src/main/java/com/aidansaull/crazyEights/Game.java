package com.aidansaull.crazyEights;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.List;

@Component
public class Game
{
    @Autowired
    @Lazy
    SimpMessagingTemplate simpMessagingTemplate;

    List<Player> players;
    boolean started = false;
    Stack<Card> deck;
    Stack<Card> discard;
    boolean direction;
    Integer currentPlayer;
    boolean isEight;
    Character eightSuit;
    Integer skipped = -1;
    boolean justPassed = false;

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
        direction = true;
        // We can now tell the players that the game has started
        sendScore(false);
        dealHands();
        drawTopCard();
        sendScore(false); // We have to do this again to update cards remaining
    }

    private void drawTopCard()
    {
        discard.push(drawNonEight());
    }

    public void sendScore()
    {
        sendScore(false);
    }

    public void sendScore(boolean reset)
    {
        List<Integer> scores = new ArrayList<>();
        for (Player player : players)
        {
            scores.add(player.score);
        }
        Score score = new Score(direction, scores, currentPlayer, discard.size()==0 ? null : discard.peek(), deck.size(), reset, skipped);
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
        isEight = false;
        skipped = -1;
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

    public void removePlayers()
    {
        System.out.println("Oops! One player left, resetting the game!");
        sendScore(true);
        //players.removeAll(players);
        newGame();
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

    public void nextTurn()
    {
        nextTurn(false);
    }

    public void nextTurn(boolean justPassed)
    {
        int change = direction ? 1 : -1;;
        if (justPassed) // if we just passed, then we do not apply card effects again
            justPassed = false; //resetting
        else
        {
            if (discard.peek().rank == 'A') // Switch direction, we have an ace
            {
                direction = !direction;
            }
            change = direction ? 1 : -1;
            if (discard.peek().rank == 'Q') // Someone's turn was skipped, let them know
            {
                skipped = Math.floorMod((currentPlayer + change), 4);
                change *= 2;
            } else
            {
                skipped = -1;
            }
        }
        currentPlayer = Math.floorMod((currentPlayer+change), 4);
        sendScore();

        // tell players that it is a new turn
        for (Player player : players)
            player.nextTurn();
    }
}
