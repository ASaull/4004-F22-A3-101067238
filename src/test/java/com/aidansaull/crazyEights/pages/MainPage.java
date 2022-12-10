package com.aidansaull.crazyEights.pages;

import jdk.jfr.Timespan;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Component
@Lazy
public class MainPage extends AbstractPage<MainPage>
{
    public MainPage(WebDriver webDriver)
    {
        super(webDriver);
    }

    @FindBy(id = "join")
    public WebElement joinButton;

    @FindBy(id = "hand-list")
    public WebElement handList;


    @FindBy(id = "leave")
    public WebElement leaveButton;

    @FindBy(id = "top-card-header")
    public WebElement topCardHeader;

    @FindBy(id = "pass-button")
    public WebElement passButton;

    @FindBy(id = "draw-button")
    public WebElement drawButton;

    @FindBy(id = "p1-score")
    public WebElement p1ScoreText;
    @FindBy(id = "p2-score")
    public WebElement p2ScoreText;
    @FindBy(id = "p3-score")
    public WebElement p3ScoreText;
    @FindBy(id = "p4-score")
    public WebElement p4ScoreText;

    public void clickJoin()
    {
        joinButton.click();
    }

    public void clickLeave()
    {
        leaveButton.click();
    }

    // I copied this from the blackjack demo
    public boolean hasText(String string)
    {
        final List<WebElement> result = this.driver.findElements(By.xpath("//*[contains(text(),\"" + string + "\")]"));
        return result != null && result.size() > 0;
    }

    public void quit()
    {
        driver.quit();
    }

    public boolean playCard(String cardString)
    {
        try
        {
            WebElement button = driver.findElement(By.id(cardString));
            button.click();
        }
        catch (Exception e)
        {
            System.out.println(e);
            return false;
        }
        return true;
    }

    public String getTopCard()
    {
        String fullText = topCardHeader.getText();
        return fullText.substring(fullText.length() - 2); // could hardcode, but whatever
    }

    public boolean mustDraw()
    {
        List<WebElement> buttons = driver.findElements(By.className("card-button"));
        buttons.removeIf(n-> (!joinButton.isDisplayed()));
        return buttons.size() == 0;
    }

    public boolean mustPlay(String cardString)
    {
        List<WebElement> buttons = driver.findElements(By.className("card-button"));
        buttons.removeIf(n-> (!n.isDisplayed()));
        return buttons.size() == 1 && Objects.equals(buttons.get(0).getAttribute("id"), cardString);
    }

    public boolean mustPass()
    {
        return passButton.isEnabled();
    }

    public void pass()
    {
        passButton.click();
    }

    public void draw()
    {
        drawButton.click();
    }

    public boolean checkScores(int p1Score, int p2Score, int p3Score, int p4Score)
    {
        return Integer.parseInt(p1ScoreText.getText().split(" ")[2]) == p1Score
                && Integer.parseInt(p2ScoreText.getText().split(" ")[2]) == p2Score
                && Integer.parseInt(p3ScoreText.getText().split(" ")[2]) == p3Score
                && Integer.parseInt(p4ScoreText.getText().split(" ")[2]) == p4Score;
    }

    public void selectSuit(String suit)
    {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(500));
        WebElement suitButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(suit + "-button")));
        suitButton.click();
    }

    public int getHandSize()
    {
        return driver.findElements(By.className("card-button")).size();
    }
}
