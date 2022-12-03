package com.aidansaull.crazyEights.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * I Borrowed most of this from the blackjack selenium demo code.
 */
@Component
@Lazy
public abstract class AbstractPage<T extends AbstractPage<T>>
{

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPage.class);

    protected WebDriver webDriver;

    public AbstractPage(final WebDriver webDriver)
    {
        this.webDriver = webDriver;
        LOG.warn("Initializing elements for page {}.", this.getClass());
        PageFactory.initElements(this.webDriver, this);
    }

    public void refreshWithNewDriver(WebDriver webDriver)
    {
        this.webDriver = webDriver;
        LOG.warn("Refreshing elements for page {}.", this.getClass());
        PageFactory.initElements(this.webDriver, this);
    }

    /*protected abstract String getPageName();

    @PostConstruct
    public void init() {
        LOG.warn("Initializing elements for page {}.", this.getClass());
        PageFactory.initElements(this.webDriver, this);
    }



    public AbstractPage(final WebDriver webDriver) {
        this.webDriver = webDriver;
        PageFactory.initElements(this.webDriver, this);
    }

    public void open() {
        this.webDriver.get("http://localhost:8080/");
    }

    public String getTitle() {
        return this.webDriver.getTitle();
    }

    public String getUrl() {
        return this.webDriver.getCurrentUrl();
    }

    public boolean hasText(final String searchKey) {
        final List<WebElement> result = this.webDriver.findElements(By.xpath("//*[contains(text(),'" + searchKey + "')]"));
        return result != null && result.size() > 0;
    }*/
}

