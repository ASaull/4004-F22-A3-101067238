package com.aidansaull.crazyEights.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Lazy
public class MainPage extends AbstractPage<MainPage>
{
    public MainPage(WebDriver webDriver)
    {
        super(webDriver);
    }

    @FindBy(id = "connect")
    public WebElement connectButton;

    @FindBy(id = "name")
    public WebElement nameField;

    @FindBy(id = "send")
    public WebElement sendButton;

    public void clickConnect()
    {
        connectButton.click();
    }

    public void sendName(String name)
    {
        nameField.sendKeys(name);
        sendButton.click();
    }

    // I copied this from the blackjack demo
    public boolean hasText(String string)
    {
        final List<WebElement> result = this.webDriver.findElements(By.xpath("//*[contains(text(),'" + string + "')]"));
        return result != null && result.size() > 0;
    }

    public void quit()
    {
        webDriver.quit();
    }

    /*@Override
    protected String getPageName()
    {
        return "Main Page";
    }*/
}
