package com.aidansaull.crazyEights;

import com.aidansaull.crazyEights.config.MockUserFactory;
import com.aidansaull.crazyEights.pages.MainPage;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
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

    private List<MainPage> userMainPages = new ArrayList<>();

    @Autowired
    Game game;

    private MockUserFactory userFactory;

    @After
    public void cleanUp()
    {
        for (MainPage mainPage : userMainPages)
        {
            mainPage.quit();
        }
    }

    void connectNewPlayer()
    {
        WebDriver webDriver = MockUserFactory.buildNewUser("http://localhost:8080");
        MainPage mainPage = new MainPage(webDriver);
        userMainPages.add(mainPage);
        mainPage.clickJoin();
    }

    @Given("The game has been started and all players have joined")
    public void theGameHasBeenStartedAndAllPlayersHaveJoined()
    {
        // We start by disconnecting all players

        for (int i = 0; i < 4; i++)
        {
            connectNewPlayer();
        }
    }

    @Then("player {int} sees {string}")
    public void playerSeesYouArePlayer(int id, String string)
    {
        MainPage mainPage = userMainPages.get(id-1);
        assertTrue(mainPage.hasText(string));
    }
}
