package com.aidansaull.crazyEights;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;

@Controller
public class GreetingController
{
    @Autowired
    Game game;

    @MessageMapping("/hello")
    //@SendTo("/topic/greetings")
    public void greeting(Principal principal, HelloMessage message) throws Exception
    {
        String username = principal.getName();
        Thread.sleep(1000); // simulated delay
        game.startGame();
        //return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + ", you have username " + username);
    }

}
