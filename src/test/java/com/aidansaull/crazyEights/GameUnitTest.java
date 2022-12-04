package com.aidansaull.crazyEights;

import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.lang.model.util.Types;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GameUnitTest
{
    @Autowired
    Game game;

    private void addPlayersToGame()
    {
        Player player = new Player();
        Player player2 = new Player();
        Player player3 = new Player();
        Player player4 = new Player();

        game.addPlayer(player);
        game.addPlayer(player2);
        game.addPlayer(player3);
        game.addPlayer(player4);
    }

    @Test
    public void testGameStarts()
    {
        game.newGame();
        Player player = new Player();
        Player player2 = new Player();
        Player player3 = new Player();
        Player player4 = new Player();

        game.addPlayer(player);
        game.addPlayer(player2);
        assertFalse(game.isStarted());
        game.addPlayer(player3);
        game.addPlayer(player4);

        assertTrue(game.isStarted());
    }

    @Test
    public void testDrawCard()
    {
        game.newGame();
        Card card = game.drawCard();
        List<Character> ranks = Arrays.asList('A', '2', '3', '4', '5', '6', '7', '8', '9', 'J', 'Q', 'K');

        assertTrue(Objects.equals(card.suit, 'C')
                || Objects.equals(card.suit, 'H')
                || Objects.equals(card.suit, 'D')
                || Objects.equals(card.suit, 'S'));
        assertTrue(ranks.contains(card.rank));
    }

    @Test
    public void testDealHands() throws InterruptedException
    {
        game.newGame();
        addPlayersToGame();
        for (Player player : game.players)
        {
            assertNotNull(player.hand);
            assertTrue(player.hand.size() == 5);
        }
    }

    @Test
    public void testDiscardPile() throws InterruptedException
    {
        game.newGame();
        addPlayersToGame();
        assertNotNull(game.discard);
        assertEquals(1, game.discard.size());
    }

    @Test
    public void testReshuffleEight() throws InterruptedException
    {
        game.newGame();
        addPlayersToGame();
        // This will cause the game to draw an 8 twice in a row
        game.deck.push(new Card('8', 'H'));
        game.deck.push(new Card('8', 'C'));
        for(int i = 0; i<20; i++)
        {
            // dirty way to put 20 cards for the 4 players
            game.deck.push(new Card('A', 'S'));
        }
        // assert that the 8 has been reshuffled
        Card card = game.discard.peek();
        assertNotEquals('8', card.rank);
    }

    @Test
    public void testInitialScores()
    {
        game.newGame();
        addPlayersToGame();
        for (Player player : game.players)
        {
            assertNotNull(player.score);
            assertEquals(0, player.score);
        }
    }

    @Test
    public void testInitialDirection()
    {
        game.newGame();
        assertNotNull(game.direction);
        assertTrue(game.direction);
    }
}
