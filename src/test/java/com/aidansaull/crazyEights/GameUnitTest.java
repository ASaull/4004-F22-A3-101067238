package com.aidansaull.crazyEights;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GameUnitTest
{
    @Autowired
    Game game;

    @Autowired
    PlayerFactory playerFactory;

    private void addPlayersToGame()
    {
        Player player = playerFactory.createInstance("0");
        Player player2 = playerFactory.createInstance("1");
        Player player3 = playerFactory.createInstance("2");
        Player player4 = playerFactory.createInstance("3");

        game.addPlayer(player);
        game.addPlayer(player2);
        game.addPlayer(player3);
        game.addPlayer(player4);
    }

    @Test
    public void testGameStarts()
    {
        game.newGame();
        Player player = playerFactory.createInstance("0");
        Player player2 = playerFactory.createInstance("1");
        Player player3 = playerFactory.createInstance("2");
        Player player4 = playerFactory.createInstance("3");

        game.addPlayer(player);
        game.addPlayer(player2);
        assertFalse(game.isStarted());
        game.addPlayer(player3);
        game.addPlayer(player4);

        assertTrue(game.isStarted());
    }

    @Test
    public void testDeckCreation()
    {
        game.newGame();
        assertEquals(52, game.deck.size());
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
        game.startGame();
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

    @Test
    public void testPlayCard()
    {
        game.newGame();
        addPlayersToGame();
        game.discard.push(new Card('4', 'C'));
        assertEquals(2, game.discard.size());
        Player player = game.players.get(0);
        player.hand.removeAll(player.hand);
        player.addCard(new Card('5', 'C'));
        player.addCard(new Card('5', 'H'));
        player.addCard(new Card('8', 'D'));

        // Will be true because this card exists and is playable
        assertTrue(player.playCard('5', 'C'));

        // Will be false because this card does not exist
        assertFalse(player.playCard('5', 'C'));

        // Will be false because this card is not playable
        assertFalse(player.playCard('5', 'H'));

        // Will be true because we can always play an 8
        assertTrue(player.playCard('8', 'D'));

        //check that cards have been played
        assertEquals(4, game.discard.size());
        assertEquals(1, player.hand.size());
    }
}
