import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import project.Util;

import java.util.List;

import static project.Constants.*;
import static org.junit.Assert.*;


// the test stream we will be using is
// "birman cat" tag: #birman#cat#kitten
// "dragon li" tag: #dragon#li#cat#kitten
// "husky" tag: #dog#doge#puppy#husky
// auto complete will return either stream name or tag name
public class autoCompleteSearchTest {
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

    @Test
    public void autoCompleteTest() {
        driver.get(manageUrl);
        wait.until(util.pageLoadedCondition());

        // search for cat, auto complete should return birman cat and cat
        WebElement searchBox = driver.findElement(By.id("autocomplete_search"));
        searchBox.sendKeys("cat");

        // wait until auto complete box get results from cache or servers
        wait.until(util.searchLoadedCondition());

        List<WebElement> autoCompleteResult = driver.findElements(By.className("ui-menu-item-wrapper"));
        assertEquals(2, autoCompleteResult.size());
        assertEquals("birman cat", autoCompleteResult.get(0).getText());
        assertEquals("cat", autoCompleteResult.get(1).getText());
    }

    @Test
    public void autoCompleteTest2() {
        driver.get(manageUrl);
        wait.until(util.pageLoadedCondition());

        WebElement searchBox = driver.findElement(By.id("autocomplete_search"));
        searchBox.sendKeys("husky");
        wait.until(util.searchLoadedCondition());

        List<WebElement> autoCompleteResult = driver.findElements(By.className("ui-menu-item-wrapper"));
        assertEquals(1, autoCompleteResult.size());
        assertEquals("husky", autoCompleteResult.get(0).getText());

    }

    @Test
    public void searchResultTest() {
        driver.get(manageUrl);
        wait.until(util.pageLoadedCondition());

        WebElement searchBox = driver.findElement(By.id("autocomplete_search"));
        searchBox.sendKeys("cat");
        searchBox.sendKeys(Keys.ENTER);

        wait.until(util.pageLoadedCondition());

        // test that title and url is correct
        assertEquals("StreamShare! | Search Result", driver.getTitle());
        assertEquals(searchUrl, driver.getCurrentUrl());

        // test that search result is correct
        List<WebElement> searchResult = driver.findElements(By.xpath("//*[@id=\"img-pane\"]/div/a/div"));
        assertEquals(2, searchResult.size());
        assertEquals("birman cat", searchResult.get(0).getText());
        assertEquals("dragon li", searchResult.get(1).getText());
    }
}
