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
        Thread.sleep(100);
    }

    @Then("player {int} sees {string}")
    public void playerSeesYouArePlayer(int id, String string)
    {
        MainPage mainPage = userMainPages.get(id-1);
        assertTrue(mainPage.hasText(string));
    }

    @Given("top card is {string}")
    public void topCardIs(String cardString)
    {
        Character rank = cardString.charAt(0);
        Character suit = cardString.charAt(1);
        game.discard.push(new Card(rank, suit, false));
        game.sendScore();
    }

    @When("player {int} plays {string}")
    public void playerPlaysC(int id, String cardString)
    {
        MainPage mainPage = userMainPages.get(id-1);
        assertTrue(mainPage.playCard(cardString));
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
        game.players.get(id-1).addCard(new Card(rank, suit, false));
    }

    @And("player {int} is notified that they missed their turn")
    public void playerIsNotifiedThatTheyMissedTheirTurn(int id)
    {
        MainPage mainPage = userMainPages.get(id-1);
        assertTrue(mainPage.hasText("Your turn was skipped!"));
    }

    @And("it is player {int}s turn")
    public void itIsPlayerSTurn(int id)
    {
        game.currentPlayer = id-1;
        game.sendScore();
    }

    @Then("top card shows {string}")
    public void topCardShows(String topCardString)
    {
        MainPage mainPage = userMainPages.get(0); //can be anybody
        assertEquals(topCardString, mainPage.getTopCard());
    }

    @And("interface prompts player {int} for a new suit")
    public void interfacePromptsForANewSuit(int id)
    {
        MainPage mainPage = userMainPages.get(id-1);
        assertTrue(mainPage.hasText("Please select the new suit"));
    }

    @Then("player {int} cannot play {string}")
    public void playerCannotPlayS(int id, String cardString)
    {
        MainPage mainPage = userMainPages.get(id);
        assertFalse(mainPage.playCard(cardString));
    }

    @When("player {int} has exactly {string} as their hand")
    public void playerHasExactlyHAsTheirHand(int id, String handStrings) throws InterruptedException
    {
        List<Card> hand = new ArrayList<>();
        Player player = game.players.get(id-1);
        player.hand = new ArrayList<>();
        // we tell the player to empty their hand
        player.emptyHand();
        Thread.sleep(100);
        for(String handString : handStrings.split(","))
        {
            Card card = new Card(handString.charAt(0), handString.charAt(1), false);
            player.addCard(card);
        }
    }

    @Then("player {int} must draw, gets {string}")
    public void playerMustDraw(int id, String cardString) throws InterruptedException
    {
        MainPage mainPage = userMainPages.get(id-1);
        Player player = game.players.get(id-1);

        assertTrue(mainPage.mustDraw());

        List<Card> oldHand = new ArrayList<>();
        oldHand.addAll(player.hand);
        mainPage.draw();
        Thread.sleep(100); //wait for draw to occur
        player.hand = new ArrayList<>();
        boolean first = true;
        for(Card card : oldHand)
        {
            card.resetHand = first;
            player.addCard(card);
            first = false;
        }
        player.addCard(new Card(cardString.charAt(0), cardString.charAt(1), false));
        System.out.println("Hand is now " + player.hand);
    }

    @Then("player {int} must play {string}")
    public void playerMustPlayD(int id, String cardString)
    {
        MainPage mainPage = userMainPages.get(id-1);
        assertTrue(mainPage.mustPlay(cardString));
    }

    @Then("player {int} must pass")
    public void playerMustPass(int id)
    {
        MainPage mainPage = userMainPages.get(id-1);
        assertTrue(mainPage.mustPass());
    }

    @When("player {int} passes")
    public void playerPasses(int id)
    {
        MainPage mainPage = userMainPages.get(id-1);
        mainPage.pass();
    }

    @And("player {int} draws, gets {string}")
    public void playerDrawsGetsD(int id, String cardString)
    {
        MainPage mainPage = userMainPages.get(id-1);
        Player player = game.players.get(id-1);

        List<Card> oldHand = new ArrayList<>();
        oldHand.addAll(player.hand);
        mainPage.draw();
        player.hand = oldHand;
        player.addCard(new Card(cardString.charAt(0), cardString.charAt(1), false));
    }

    @Then("player {int} must draw 2 cards, gets {string}")
    public void playerMustDrawCardsGetsCD(int id, String cardString)
    {

    }

    @Then("the game is over with scores {int} {int} {int} {int}")
    public void theGameIsOverWithScores(int p1Score, int p2Score, int p3Score, int p4Score) throws InterruptedException
    {
        MainPage mainPage = userMainPages.get(0); // doesn't matter
        Thread.sleep(100);
        assertTrue(mainPage.hasText("Game over!"));
        assertTrue(mainPage.checkScores(p1Score,p2Score,p3Score,p4Score));
    }

    @Then("the round is over with scores {int} {int} {int} {int}")
    public void theRoundIsOverWithScores(int p1Score, int p2Score, int p3Score, int p4Score) throws InterruptedException
    {
        MainPage mainPage = userMainPages.get(0); // doesn't matter
        Thread.sleep(100);
        assertTrue(mainPage.hasText("Round over!"));
        assertTrue(mainPage.checkScores(p1Score,p2Score,p3Score,p4Score));
    }

    @And("player {int} selects suit {string}")
    public void playerSelectsSuitD(int id, String suit)
    {
        MainPage mainPage = userMainPages.get(id-1);
        mainPage.selectSuit(suit);
    }

    @Then("player {int} shows hand {string}")
    public void playerShowsHandDDTH(int id, String handString)
    {
        MainPage mainPage = userMainPages.get(id-1);
        String[] cardStrings = handString.split(",");
        for(String cardString : cardStrings)
        {
            if (cardString.charAt(0) == 'T')
            {
                cardString = "10" + cardString.charAt(1);
            }
            assertTrue(mainPage.hasText(cardString));
        }
        assertEquals(cardStrings.length, mainPage.getHandSize());
    }

    @And("the game states that player {int} is the winner")
    public void theGameStatesThatPlayerIsTheWinner(Integer id)
    {
        MainPage mainPage = userMainPages.get(0); // doesn't matter
        assertTrue(mainPage.hasText("Player " + id.toString() + " is the winner!"));
    }
}
