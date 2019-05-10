import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import project.Constants;
import project.Util;

import static org.junit.Assert.*;
import static project.Constants.*;

public class subscribeTest {
    static WebDriver driver;
    static WebDriverWait wait;
    static private Util util;

    @BeforeClass
    public static void setup() {
        System.setProperty("webdriver.chrome.driver", driverPath);
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        util = new Util(driver, wait);
        util.login(emailAddress, pwd);
    }

    @AfterClass
    public static void tearDown() {
        driver.quit();
    }

    @Before
    public void clearImg() {
//        util.deleteAll();
    }

    @Test
    public void subUnsubTest() {
        driver.get(Url + "/view?streamid=test1");
        wait.until(util.pageLoadedCondition());

        WebElement subscribe = driver.findElement(By.name("subscribe"));
        assertEquals("Subscribe", subscribe.getText());
        subscribe.click();
        wait.until(util.pageLoadedCondition());
        WebElement unscribe = driver.findElement(By.name("unsubscribe"));
        assertEquals("Unsubscribe", unscribe.getText());
        unscribe.click();
        wait.until(util.pageLoadedCondition());
        subscribe = driver.findElement(By.name("subscribe"));
        assertEquals("Subscribe", subscribe.getText());
    }

    @Test
    public void managePageUnsubTest() {
        driver.get(Url + "/view?streamid=test1");
        wait.until(util.pageLoadedCondition());

        WebElement subscribe = driver.findElement(By.name("subscribe"));
        assertEquals("Subscribe", subscribe.getText());
        subscribe.click();
        wait.until(util.pageLoadedCondition());

        driver.get(manageUrl);
        wait.until(util.pageLoadedCondition());
        WebElement test1 = driver.findElement(By.xpath("//a[contains(@href, \"test1\")]"));
        assertEquals("test1", test1.getText());

        driver.get(Url + "/view?streamid=test2");
        wait.until(util.pageLoadedCondition());
        subscribe = driver.findElement(By.name("subscribe"));
        assertEquals("Subscribe", subscribe.getText());
        subscribe.click();
        wait.until(util.pageLoadedCondition());

        driver.get(manageUrl);
        wait.until(util.pageLoadedCondition());
        WebElement test2 = driver.findElement(By.xpath("//a[contains(@href, \"test2\")]"));
        assertEquals("test2", test2.getText());

        // testing unsubscribe to all stream
        util.unSubAll();

        // make sure the subscribe is effective also in single view page
        driver.get(Url + "/view?streamid=test1");
        wait.until(util.pageLoadedCondition());
        assertEquals(0, driver.findElements(By.name("Unsubscribe")).size());
    }

    @Test
    public void loggedOutSubTest() {
        util.logout();
        driver.get(Url + "/view?streamid=test1");
        wait.until(util.pageLoadedCondition());
        driver.findElement(By.name("subscribe")).click();
        wait.until(util.pageLoadedCondition());
        assertEquals("Sign in - Google Accounts", driver.getTitle());
    }
}
