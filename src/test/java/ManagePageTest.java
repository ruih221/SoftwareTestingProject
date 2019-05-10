import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import project.Util;

import static org.junit.Assert.*;
import static project.Constants.*;

public class ManagePageTest {
    static WebDriver driver;
    static WebDriverWait wait;
    private static boolean logged_in;
    static private Util util;

    @BeforeClass
    public static void setup() {
        System.setProperty("webdriver.chrome.driver",driverPath);
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        util = new Util(driver, wait);
        util.login(emailAddress, pwd);
    }

    @AfterClass
    public static void tearDown() {
        driver.quit();
    }

    @Test
    public void manageTitleTest()
    {
        // testing the web page title was generated correctly by server
        driver.get(manageUrl);
        assertEquals("StreamShare! | Management" ,driver.getTitle());
    }

    @Test
    public void loggedInNavBarRedirectionTest() {
        driver.findElement(By.xpath("/html/body/header/nav/ul/li[1]/a")).click();
        wait.until(util.pageLoadedCondition());
        assertEquals(manageUrl, driver.getCurrentUrl());

        String prevUrl = driver.getCurrentUrl();
        driver.findElement(By.xpath("/html/body/header/nav/ul/li[2]/a")).click();
        wait.until(util.redirectionCondition(prevUrl));
        assertEquals(newStreamUrl, driver.getCurrentUrl());

        prevUrl = driver.getCurrentUrl();
        driver.findElement(By.xpath("/html/body/header/nav/ul/li[3]/a")).click();
        wait.until(util.redirectionCondition(prevUrl));
        assertEquals(ViewAllUrl, driver.getCurrentUrl());

        prevUrl = driver.getCurrentUrl();
        driver.findElement(By.xpath("/html/body/header/nav/ul/li[4]/a")).click();
        wait.until(util.redirectionCondition(prevUrl));
        assertEquals(trendingUrl, driver.getCurrentUrl());

        prevUrl = driver.getCurrentUrl();
        driver.findElement(By.xpath("/html/body/header/nav/ul/li[5]/a")).click();
        wait.until(util.redirectionCondition(prevUrl));
        assertEquals(socialUrl, driver.getCurrentUrl());
    }
}
