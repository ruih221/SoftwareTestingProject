import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import project.Util;

import java.util.Set;

import static project.Constants.*;
import static org.junit.Assert.*;

public class socialAndMapTest {
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
    public void mapTest() {
        driver.get(Url + "/view?streamid=birman%20cat%20");
        wait.until(util.pageLoadedCondition());

        driver.findElement(By.id("geo")).click();
        wait.until(util.pageLoadedCondition());

        assertEquals(1, driver.findElements(By.xpath("//*[@id=\"map\"]/div/div/iframe")).size());
    }

    @Test
    public void commentRenderedTest() {
        driver.get(socialUrl);
        wait.until(util.facebookLoadCondition("fb-comments"));
        assertEquals("rendered", driver.findElement(By.className("fb-comments")).getAttribute("fb-xfbml-state"));
    }

//    @Ignore
    @Test
    public void facebookLoginOutTest() {
        // test that facebook login works, put your own FB ID and password!
        driver.get(socialUrl);
        wait.until(util.facebookLoadCondition("fb-login-button"));
        WebElement fbLogin = driver.findElement(By.className("fb-login-button"));
        assertEquals("rendered", fbLogin.getAttribute("fb-xfbml-state"));

        // test that log in promt changes correctly depends on log in state
        assertEquals("Please Login with Facebook!", driver.findElement(By.id("LoginPromt")).getText());
        driver.findElement(By.xpath("//*[@id=\"main_content\"]/div/div[1]/span/iframe")).click();

        // wait until fb login pop up appear and switch to pop up
        wait.until(new ExpectedCondition<Boolean>() {
            @NullableDecl
            @Override
            public Boolean apply(WebDriver d) {
                return (driver.getWindowHandles().size() != 1);
            }
        });

        String mainWindow = driver.getWindowHandle();
        for (String child : driver.getWindowHandles()) {
            if (!child.equals(driver.getWindowHandle())) {
                driver.switchTo().window(child);
            }
        }

        driver.findElement(By.id("email")).sendKeys("your_email");
        driver.findElement(By.id("pass")).sendKeys("your_pwd");
        driver.findElement(By.name("login")).submit();

        // switch back to check that login promt changed correctly
        driver.switchTo().window(mainWindow);
        wait.until(new ExpectedCondition<Boolean>() {
            @NullableDecl
            @Override
            public Boolean apply(@NullableDecl WebDriver webDriver) {
                return !driver.findElement(By.id("LoginPromt")).getText().equals("Please Login with Facebook!");
            }
        });
        assertEquals("You have successfully logged in with Facebook!", driver.findElement(By.id("LoginPromt")).getText());

        // switch to iframe and wait until button updated to log out button before click it again
        driver.switchTo().frame(0);
        wait.until(util.pageLoadedCondition());
        // switch back to main content and click the log out button
        driver.switchTo().defaultContent();
        driver.findElement(By.xpath("//*[@id=\"main_content\"]/div/div[1]/span/iframe")).click();
        // check that login promt correctly updated
        wait.until(new ExpectedCondition<Boolean>() {
            @NullableDecl
            @Override
            public Boolean apply(@NullableDecl WebDriver webDriver) {
                return !driver.findElement(By.id("LoginPromt")).getText().equals("You have successfully logged in with Facebook!");
            }
        });
        assertEquals("Please Login with Facebook!", driver.findElement(By.id("LoginPromt")).getText());
    }

}
