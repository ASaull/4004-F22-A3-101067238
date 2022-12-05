package com.aidansaull.crazyEights.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Initially coped from selenium cucumber demo code
 */
@Service
public class MockUserFactory
{
    {
        WebDriverManager.edgedriver().setup();
    }

    public static WebDriver buildNewUser(final String location) {
        //final DesiredCapabilities capabilities = DesiredCapabilities.edge();
        //capabilities.setJavascriptEnabled(true);

        final WebDriver edgeDriver = new EdgeDriver();
        edgeDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        edgeDriver.get(location);
        return edgeDriver;
    }
}
