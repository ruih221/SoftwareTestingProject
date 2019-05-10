import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import project.Util;

import java.util.List;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static project.Constants.*;
import static org.junit.Assert.*;

public class newStreamTest {
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
    public void login() {
    }


    @Test
    public void titleTest() {
        String prevUrl = driver.getCurrentUrl();
        driver.get(newStreamUrl);
        wait.until(util.redirectionCondition(prevUrl));
        assertEquals("StreamShare! | New Stream", driver.getTitle());
    }

    @Test
    public void createStreamTest() {
        // basic new stream creation test
        driver.get(newStreamUrl);
        wait.until(elementToBeClickable(By.name("stream-name")));
        driver.findElement(By.name("stream-name")).sendKeys("cat");
        driver.findElement(By.name("tags")).sendKeys("#cat#animal#kitten");
        driver.findElement(By.id("create")).submit();
        wait.until(util.pageLoadedCondition());

        assertEquals(manageUrl, driver.getCurrentUrl());
        List<WebElement> newStreamList = driver.findElements(By.xpath("//td/a[contains(text(), 'cat')]"));
        assertEquals(1, newStreamList.size());
        assertEquals("cat", newStreamList.get(0).getText());
        // test the stream url is correct
        assertEquals(Url + "/view?streamid=cat", newStreamList.get(0).getAttribute("href"));

        // create another stream
        driver.get(newStreamUrl);
        wait.until(elementToBeClickable(By.name("stream-name")));
        driver.findElement(By.name("stream-name")).sendKeys("dog");
        driver.findElement(By.name("tags")).sendKeys("#dog#animal#puppy");
        driver.findElement(By.id("create")).submit();
        wait.until(util.pageLoadedCondition());
        newStreamList = driver.findElements(By.xpath("//td/a"));
        assertEquals(2, newStreamList.size());
        WebElement dog = driver.findElement(By.xpath("//td/a[contains(text(), 'dog')]"));
        assertEquals("dog", dog.getText());
        assertEquals(Url + "/view?streamid=dog",dog.getAttribute("href"));

        util.deleteAll();

    }

    @Test
    public void spacedStreamNameTest() {
        // test that stream name like "british shorthair" works. Sometimes these kind of name with space character doesn't
        // work because the server forgot to escape the space character.

        driver.get(newStreamUrl);
        wait.until(elementToBeClickable(By.name("stream-name")));
        driver.findElement(By.name("stream-name")).sendKeys("british shorthair");
        driver.findElement(By.name("tags")).sendKeys("#cat#animal#kitten");
        driver.findElement(By.id("create")).submit();
        wait.until(util.pageLoadedCondition());

        assertEquals(manageUrl, driver.getCurrentUrl());
        List<WebElement> newStreamList = driver.findElements(By.xpath("//td/a[contains(text(), 'british') and contains(text(), 'shorthair')]"));
        assertEquals(1, newStreamList.size());
        assertEquals("british shorthair", newStreamList.get(0).getText());
        assertEquals(Url + "/view?streamid=british%20shorthair",newStreamList.get(0).getAttribute("href"));

        util.deleteAll();
    }

    @Test
    public void nonDefaultStreamCoverSubscriberTest() {
        // basic new stream creation test
        driver.get(newStreamUrl);
        wait.until(elementToBeClickable(By.name("stream-name")));
        driver.findElement(By.name("stream-name")).sendKeys("maine coon");
        driver.findElement(By.name("tags")).sendKeys("#cat#animal#kitten");
        driver.findElement(By.name("cover")).sendKeys("http://www.pethealthnetwork.com/sites/default/files/maine-coon-cat-484757920.jpg");
        driver.findElement(By.name("Subscribers")).sendKeys("ruhan@utexas.edu");
        driver.findElement(By.name("optional-message")).sendKeys("maine coon");
        driver.findElement(By.id("create")).submit();
        wait.until(util.pageLoadedCondition());

        assertEquals(manageUrl, driver.getCurrentUrl());
        driver.get(ViewAllUrl);
        wait.until(util.pageLoadedCondition());
        WebElement birman = driver.findElement(By.xpath("//a[contains(@href, \"maine\")]/img"));
        assertEquals("http://www.pethealthnetwork.com/sites/default/files/maine-coon-cat-484757920.jpg", birman.getAttribute("src"));
        util.deleteAll();
    }

    @Test
    public void duplicateStreamTest(){

        driver.get(newStreamUrl);
        wait.until(elementToBeClickable(By.name("stream-name")));
        driver.findElement(By.name("stream-name")).sendKeys("cat");
        driver.findElement(By.name("tags")).sendKeys("#cat#animal#kitten");
        driver.findElement(By.id("create")).submit();
        wait.until(util.pageLoadedCondition());
        // create duplicate stream
        driver.get(newStreamUrl);
        wait.until(elementToBeClickable(By.name("stream-name")));
        driver.findElement(By.name("stream-name")).sendKeys("cat");
        driver.findElement(By.name("tags")).sendKeys("");
        driver.findElement(By.id("create")).submit();
        wait.until(util.pageLoadedCondition());
        String prevUrl = driver.getCurrentUrl();
        System.err.println(prevUrl);
        assertEquals("Error Adding Stream", driver.getTitle());
        wait.until(util.redirectionCondition(prevUrl));
        // test that after error page, user redirected to manage page
        assertEquals(manageUrl, driver.getCurrentUrl());

        util.deleteAll();
        assertEquals(0, driver.findElements(By.xpath("//*[@id=\"main_content\"]/div/div/div[2]/form/table/tbody/tr")).size());
    }
}
