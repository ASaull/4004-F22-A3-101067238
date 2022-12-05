package com.aidansaull.crazyEights;

import com.aidansaull.crazyEights.config.MockUserFactory;
import com.aidansaull.crazyEights.pages.MainPage;
import io.cucumber.core.cli.Main;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import javax.validation.constraints.AssertTrue;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@CucumberContextConfiguration
@SpringBootTest(properties = {"server.port=8080"}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class MyStepdefs
{
    @Autowired
    private ApplicationContext applicationContext;

    private static List<MainPage> userMainPages = new ArrayList<>();

    @Autowired
    Game game;

    private MockUserFactory userFactory;

    public void cleanUp()
    {
        for (MainPage mainPage : userMainPages)
        {
            mainPage.quit();
        }
    }

    void connectNewPlayer(int i)
    {
        if (userMainPages.size() != 4)
        {
            WebDriver webDriver = MockUserFactory.buildNewUser("http://localhost:8080");
            //Set the size of the window
            webDriver.manage().window().setSize(new Dimension(470, 1080));

            switch (userMainPages.size())
            {
                case 0:
                    webDriver.manage().window().setPosition(new Point(0,0));
                    break;
                case 1:
                    webDriver.manage().window().setPosition(new Point(500,0));
                    break;
                case 2:
                    webDriver.manage().window().setPosition(new Point(500*2,0));
                    break;
                case 3:
                    webDriver.manage().window().setPosition(new Point(500*3,0));
                    break;
            }

            MainPage mainPage = new MainPage(webDriver);
            userMainPages.add(mainPage);
        }
        userMainPages.get(i).clickJoin();
    }

    @Given("The game has been started and all players have joined")
    public void theGameHasBeenStartedAndAllPlayersHaveJoined() throws InterruptedException
    {
        if (userMainPages.size() == 4) // this is not the first test, we will make them all leave before reconnecting
        {
            userMainPages.get(0).clickLeave();
        }

        for (int i = 0; i < 4; i++)
        {
            connectNewPlayer(i);
        }
        Thread.sleep(1000);
    }

    @Then("player {int} sees {string}")
    public void playerSeesYouArePlayer(int id, String string)
    {
        MainPage mainPage = userMainPages.get(id-1);
        assertTrue(mainPage.hasText(string));
    }

    @Given("top card is {string}")
    public void topCardIs(String cardString) throws InterruptedException
    {
        Character rank = cardString.charAt(0);
        Character suit = cardString.charAt(1);
        game.discard.push(new Card(rank, suit));
        game.sendScore();
    }

    @When("player {int} plays {string}")
    public void playerPlaysC(int id, String cardString)
    {
        MainPage mainPage = userMainPages.get(id-1);
        mainPage.playCard(cardString);
    }

    @Then("current player is player {int}")
    public void currentPlayerIsPlayer(Integer id)
    {
        MainPage mainPage = userMainPages.get(0); //could be any one
        assertTrue(mainPage.hasText("It is currently Player " + id.toString() + "'s turn."));
    }

    @And("next player is player {int}")
    public void nextPlayerIsPlayer(Integer id)
    {
        MainPage mainPage = userMainPages.get(0); //could be any one
        assertTrue(mainPage.hasText("It will be Player " + id.toString() + "'s turn next."));
    }

    @And("player {int} has {string} in their hand")
    public void playerHasCInTheirHand(int id, String cardString)
    {
        Character rank = cardString.charAt(0);
        Character suit = cardString.charAt(1);
        game.players.get(id-1).addCard(new Card(rank, suit));
    }
}
