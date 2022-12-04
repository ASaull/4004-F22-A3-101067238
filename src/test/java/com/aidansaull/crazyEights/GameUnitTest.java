package com.aidansaull.crazyEights;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.lang.model.util.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GameUnitTest
{
    @Autowired
    Game game;

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
}