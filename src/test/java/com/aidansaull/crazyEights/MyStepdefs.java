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

    //@Autowired
    //private List<WebDriver> users = new ArrayList<>();

    private MockUserFactory userFactory;

    @After
    public void cleanUp()
    {
        for (MainPage mainPage : userMainPages)
        {
            mainPage.quit();
        }
    }

    @Given("the website is running")
    public void theWebsiteIsRunning()
    {
        //System.setProperty("webdriver.edge.driver", "C:\\Users\\saull\\Documents\\COMP4004\\Web_Driver\\msedgedriver.exe");
        //mainPage.open();
    }

    @Given("I am on the main page")
    public void iAmOnTheMainPage()
    {

    }

    @When("Player {int} connects to the websocket")
    public void playerConnectsToTheWebsocket(int arg0)
    {
        WebDriver webDriver = MockUserFactory.buildNewUser("http://localhost:8080");
        userMainPages.add(new MainPage(webDriver));
        MainPage mainPage = userMainPages.get(arg0-1);
        mainPage.clickConnect();
    }

    @When("Player {int} sends the name {string}")
    public void playerSendsTheNameAidan(int arg0, String name)
    {
        MainPage mainPage = userMainPages.get(arg0-1);
        mainPage.sendName(name);
    }

    @Then("Player {int} sees {string}")
    public void playerSeesHelloAidan(int arg0, String text)
    {
        MainPage mainPage = userMainPages.get(arg0-1);
        assertTrue(mainPage.hasText(text));
    }

    @And("Player {int} does not see {string}")
    public void playerDoesNotSeeHelloAidanYouArePlayer(int arg0, String text)
    {
        MainPage mainPage = userMainPages.get(arg0-1);
        assertFalse(mainPage.hasText(text));
    }
}
