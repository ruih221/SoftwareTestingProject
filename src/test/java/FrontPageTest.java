import static org.junit.Assert.*;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;
import static project.Constants.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import project.Util;

import java.util.concurrent.TimeUnit;

public class FrontPageTest
{

    static WebDriver driver;
    static WebDriverWait wait;
    private static Util util;

    @BeforeClass
    public static void setup() {
        System.setProperty("webdriver.chrome.driver",driverPath);
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        util = new Util(driver, wait);
    }

    @AfterClass
    public static void  teardown() {
        driver.quit();
    }

    @Test
    public void frontTitleTest()
    {
        // testing the web page title was generated correctly by server
        driver.get(Url);
        assertEquals("StreamShare!" ,driver.getTitle());
    }

    @Test
    public void viewAllRedirectTest() {
        driver.get(Url);
        driver.findElement(By.className("btn-secondary")).click();
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        wait.until(util.redirectionCondition(Url));
        assertEquals(ViewAllUrl, driver.getCurrentUrl());
    }

    @Test
    public void logInTest() {
        // test that login function is working as expected
        driver.get(Url);
        driver.findElement(By.className("btn-outline-light")).click();
        wait.until(util.redirectionCondition(Url));

        wait.until(elementToBeClickable(By.id("identifierId")));
        String prevUrl = driver.getCurrentUrl();
        driver.findElement(By.id("identifierId")).sendKeys(emailAddress);
        driver.findElement(By.id("identifierNext")).click();
        wait.until(util.redirectionCondition(prevUrl));

        wait.until(elementToBeClickable(By.xpath("//*[@id=\"password\"]/div[1]/div/div[1]/input")));
        prevUrl = driver.getCurrentUrl();
        driver.findElement(By.xpath("//*[@id=\"password\"]/div[1]/div/div[1]/input"))
                            .sendKeys(pwd);
        driver.findElement(By.xpath("//*[@id=\"passwordNext\"]/content")).click();
        wait.until(util.redirectionCondition(prevUrl));

        // test that after login user should be redirect to manage page
        assertEquals(manageUrl, driver.getCurrentUrl());

        // test that after login, accessing front page user should be redirected to manage page
        driver.get(Url);
        wait.until(util.redirectionCondition(Url));
        assertEquals(manageUrl, driver.getCurrentUrl());
    }
}
