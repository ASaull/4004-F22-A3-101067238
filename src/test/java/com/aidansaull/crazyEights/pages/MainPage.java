package com.aidansaull.crazyEights.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

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

    @FindBy(id = "leave")
    public WebElement leaveButton;

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

    public void playCard(String cardString)
    {
//        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
//                .withTimeout(Duration.ofSeconds(3))
//                .pollingEvery(Duration.ofMillis(100))
//                .ignoring(Exception.class);
//
//        WebElement button = null;
//        System.out.println("starting to look for " + cardString + "!");
//        button = wait.until(new Function<>()
//        {
//            public WebElement apply(WebDriver driver)
//            {
//                return driver.findElement(By.id(cardString));
//            }
//        });
//        if (button != null)
//            System.out.println("fuond button ");
        // we find all the possible cards
        WebElement button = driver.findElement(By.id(cardString));


        //for (WebElement card : cards)
        //{
            //System.out.println("comparing " + card.getText() + " with " + cardString);
            //if (Objects.equals(card.getText(), cardString))
            //{
                // This is the one, play it!
                //System.out.println("found card");
                //WebElement button = card.findElement(By.xpath(".//button"));
            //}
        //}
        button.click();
    }
}
