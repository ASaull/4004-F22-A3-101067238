package com.aidansaull.crazyEights.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Initially copied from blackjack demo code
 */
@Service
@Lazy
public class WebDriverFactory
{
    {
        WebDriverManager.edgedriver().setup();
    }

    public WebDriverFactory() {}

    @Bean(destroyMethod = "quit")
    public WebDriver getWebDriver()
    {
        final DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setJavascriptEnabled(true);

        final WebDriver edgeDriver = new EdgeDriver(new EdgeOptions().merge(capabilities));
        //final WebDriver edgeDriver = new EdgeDriver();
        edgeDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        edgeDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(2));
        return edgeDriver;
    }
}
