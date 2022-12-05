package com.aidansaull.crazyEights.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

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
        // we find all the possible cards
        List<WebElement> cards = driver.findElements(By.className("card"));
        for (WebElement card : cards)
        {
            if (Objects.equals(card.getText(), cardString))
            {
                // This is the one, play it!
                WebElement button = card.findElement(By.xpath(".//button"));
            }
        }
    }
}
