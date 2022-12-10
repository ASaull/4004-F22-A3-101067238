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

    private Card drawNonEight() throws InterruptedException
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

    void startGame() throws InterruptedException
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
        //Thread.sleep(100);
        drawTopCard();
        started = true;
        sendScore(false); // We have to do this again to update cards remaining
    }

    private void drawTopCard() throws InterruptedException
    {
        discard.push(drawNonEight());
    }

    public void sendScore() throws InterruptedException
    {
        sendScore(false);
    }

    public void sendScore(boolean reset) throws InterruptedException
    {
        List<Integer> scores = new ArrayList<>();
        boolean roundOver = false;
        boolean gameOver = false;

        System.out.println("-------------------");
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
            System.out.println("round voer");
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

        Score score = new Score(direction, scores, currentPlayer, discard.size()==0 ? null : discard.peek(), deck.size(), reset, skipped, roundOver, gameOver);
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

    private void dealHands() throws InterruptedException
    {
        for (int i = 0; i < 2; i++)
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

    public void addPlayer(Player player) throws InterruptedException
    {
        players.add(player);
        if (players.size() == 4)
        {
            startGame();
        }
    }

    public void removePlayers() throws InterruptedException
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

    public Card drawCard() throws InterruptedException
    {
        Card card = deck.pop();
        sendScore();
        return card;
    }

    public void nextTurn() throws InterruptedException
    {
        nextTurn(false);
    }

    public void nextTurn(boolean justPassed) throws InterruptedException
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
