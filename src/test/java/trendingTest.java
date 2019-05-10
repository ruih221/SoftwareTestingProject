import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import project.Util;

import static project.Constants.*;
import static org.junit.Assert.*;

public class trendingTest {
    static WebDriver driver;
    static WebDriverWait wait;
    static private Util util;

    @BeforeClass
    public static void setup() {
//        ChromeOptions opt = new ChromeOptions();
//        opt.setHeadless(true);
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
    public void clearImg() { util.deleteAll(); }

    @Test
    public void trendingTitleTest()
    {
        // testing the web page title was generated correctly by server
        driver.get(trendingUrl);
        assertEquals("StreamShare! | Trending" ,driver.getTitle());
    }

    @Test
    public void testTrendingUpdate() {
        driver.get(trendingUrl);
        wait.until(util.pageLoadedCondition());
        WebElement top = driver.findElement(By.xpath("//*[@id=\"img-pane\"]/div[1]/div[1]"));
        WebElement second = driver.findElement(By.xpath("//*[@id=\"img-pane\"]/div[2]/div[1]"));
        WebElement third = driver.findElement(By.xpath("//*[@id=\"img-pane\"]/div[3]/div[1]"));
        // get how many times the top three streams has been viewed
        int top_view = Integer.parseInt(top.getText().split(" ", 2)[0]);
        int second_view = Integer.parseInt(second.getText().split(" ", 2)[0]);
        int third_view = Integer.parseInt(third.getText().split(" ", 2)[0]);

        String secondUrl = driver.findElement(By.xpath("//*[@id=\"img-pane\"]/div[2]/a")).getAttribute("href");
        // test that the trending list is listed in order or view numbers
        assertTrue(top_view >= second_view);
        assertTrue(second_view >= third_view);

        // testing that the view update is working, and second most trending stream is promoted to the top most trending
        // stream
        for (int i = 0; i <= top_view - second_view; i++) {
            driver.get(secondUrl);
        }

        driver.get(trendingUrl);
        wait.until(util.pageLoadedCondition());
        top = driver.findElement(By.xpath("//*[@id=\"img-pane\"]/div[1]/div[1]"));
        assertEquals(top_view + 1, Integer.parseInt(top.getText().split(" ", 2)[0]));
    }

    @Test
    public void mailingListTest() {
        driver.get(trendingUrl);
        wait.until(util.pageLoadedCondition());
        driver.findElement(By.xpath("//*[@id=\"img-pane\"]/div[4]/form/div[2]/input")).click();
        driver.findElement(By.xpath("//*[@id=\"img-pane\"]/div[4]/form/button")).click();
        wait.until(util.pageLoadedCondition());

        driver.findElement(By.xpath("//*[@id=\"img-pane\"]/div[4]/form/div[3]/input")).click();
        driver.findElement(By.xpath("//*[@id=\"img-pane\"]/div[4]/form/button")).click();
        wait.until(util.pageLoadedCondition());

        driver.findElement(By.xpath("//*[@id=\"img-pane\"]/div[4]/form/div[4]/input")).click();
        driver.findElement(By.xpath("//*[@id=\"img-pane\"]/div[4]/form/button")).click();
        wait.until(util.pageLoadedCondition());

        driver.findElement(By.xpath("//*[@id=\"img-pane\"]/div[4]/form/div[1]/input")).click();
        driver.findElement(By.xpath("//*[@id=\"img-pane\"]/div[4]/form/button")).click();
        wait.until(util.pageLoadedCondition());

        driver.findElement(By.xpath("//*[@id=\"img-pane\"]/div[4]/form/div[1]/input")).click();
        driver.findElement(By.xpath("//*[@id=\"img-pane\"]/div[4]/form/button")).click();
        wait.until(util.pageLoadedCondition());
    }
}
