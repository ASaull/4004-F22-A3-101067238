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
    Integer currentPlayerCounter;
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
        for(Player player : players)
        {
            player.hand = new ArrayList<>();
        }
        started = false;
        currentPlayer = currentPlayerCounter;
        currentPlayerCounter = Math.floorMod((currentPlayerCounter+1), 4);;
        deck = new Stack<Card>();
        discard = new Stack<Card>();
        direction = true;
        isEight = false;
        skipped = -1;
        shuffleDeck();
        // We can now tell the players that the game has started
        sendScore(false);
        dealHands();
        drawTopCard();
        started = true;
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
        boolean roundOver = false;
        boolean gameOver = false;
        for (Player player : players)
        {
            System.out.println(player.hand.size());
            if (player.hand.size() == 0 && started) // This player has won the round
            {
                roundOver = true;
                break;
            }
        }

        if (deck.size() == 0 && started)
            roundOver = true;

        if (roundOver)
        {
            for (Player player : players)
            {
                scores.add(player.score());
                if (player.score >= 100)
                    gameOver = true;
            }
        }
        else
        {
            for (Player player : players)
            {
                scores.add(player.score);
            }
        }
        String winner = "";
        if (gameOver)
        {
            int winnerScore = 1000; //gotta be high enough
            for (Player player : players)
            {
                if (player.score < winnerScore)
                    winner = player.username;
            }
        }

        Score score = new Score(direction, scores, currentPlayer, discard.size()==0 ? null : discard.peek(), deck.size(), reset, skipped, roundOver, gameOver, winner);
        String destination = "/topic/score";
        simpMessagingTemplate.convertAndSend(destination, score);

        if (roundOver && !gameOver)
            startGame();
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
        currentPlayer = 0;
        currentPlayerCounter = 0;
        started = false;
        players = new ArrayList<Player>();
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
                Card card = new Card(rank, suit, false);
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
        sendScore();
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
