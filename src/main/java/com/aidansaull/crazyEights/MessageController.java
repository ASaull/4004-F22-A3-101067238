package com.aidansaull.crazyEights;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.MessageMapping;

import java.security.Principal;

@Controller
public class MessageController
{
    @Autowired
    Game game;

    @Autowired
    PlayerFactory playerFactory;

    @MessageMapping("/hello")
    public void hello(Principal principal) throws InterruptedException
    {
        game.addPlayer(playerFactory.createInstance(principal.getName()));
    }

    @MessageMapping("/goodbye")
    public void goodbye() throws InterruptedException
    {
        game.removePlayers();
    }

    @MessageMapping("/play")
    public void play(Card card, Principal principal) throws InterruptedException
    {
        Player player = game.players.get(Integer.parseInt(principal.getName()));
        if (player.playCard(card))
            System.out.println("played card " + card);
        else
            System.out.println("ERROR!! Player tried to play invalid card! " + card);
    }

    @MessageMapping("/draw")
    public void draw(Principal principal) throws InterruptedException
    {
        Player player = game.players.get(Integer.parseInt(principal.getName()));
        player.addCard(game.drawCard());
    }

    @MessageMapping("/pass")
    public void pass(Principal principal) throws InterruptedException
    {
        game.nextTurn(true);
    }
}
