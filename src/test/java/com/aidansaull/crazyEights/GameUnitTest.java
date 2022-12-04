package com.aidansaull.crazyEights;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;

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
}