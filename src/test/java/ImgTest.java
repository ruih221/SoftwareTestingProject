import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import project.Util;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.junit.Assert.*;
import static project.Constants.*;


public class ImgTest {
    static WebDriver driver;
    static WebDriverWait wait;
    static private Util util;

    @BeforeClass
    public static void setup() {
//        ChromeOptions opt = new ChromeOptions();
//        opt.setHeadless(true);
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

    @Before
    public void clearImg() { util.deleteAll(); }

    @Test
    public void onePassUploadTest() {
        // upload all item in one run
        util.createNewStream("cat");
        WebElement cat = driver.findElement(By.xpath("//td/a[contains(text(), 'cat')]"));
        driver.get(cat.getAttribute("href"));
        wait.until(util.pageLoadedCondition());

        // check that only elements related to owner is available
        assertTrue(driver.findElements(By.id("mydrop")).size() > 0);
        assertTrue(driver.findElements(By.id("submit-image")).size() > 0);
        assertTrue(driver.findElements(By.name("subscribe")).size() == 0);
        assertTrue(driver.findElements(By.name("unscribe")).size() == 0);
        // check facebook button rendered
        wait.until(util.facebookLoadCondition("fb-share-button"));

        // check that after upload, redirect to the same view stream page and verify more button and num of images
        util.upLoadImages(utColoredDir);
        assertEquals(Url + "/view?streamid=cat", driver.getCurrentUrl());

        // now upload additional files
        util.upLoadImages(utSepiaDir);
        assertEquals(Url + "/view?streamid=cat", driver.getCurrentUrl());

        // test more image work
        WebElement moreImg = driver.findElement(By.id("more-image"));
        assertEquals(4, driver.findElements(By.xpath("//*[@id=\"img-pane\"]/div")).size());
        moreImg.click();
        wait.until(util.moreLoaded((String) ((JavascriptExecutor) driver).executeScript("return returnCursor()"), (boolean) ((JavascriptExecutor) driver).executeScript("return returnMore()")));
        assertEquals(8, driver.findElements(By.xpath("//*[@id=\"img-pane\"]/div")).size());
        moreImg.click();
        wait.until(util.moreLoaded((String) ((JavascriptExecutor) driver).executeScript("return returnCursor()"), (boolean) ((JavascriptExecutor) driver).executeScript("return returnMore()")));
        assertEquals(12, driver.findElements(By.xpath("//*[@id=\"img-pane\"]/div")).size());
        moreImg.click();
        // test that additional click won't load more image
        wait.until(util.moreLoaded((String) ((JavascriptExecutor) driver).executeScript("return returnCursor()"), (boolean) ((JavascriptExecutor) driver).executeScript("return returnMore()")));
        assertEquals(12, driver.findElements(By.xpath("//*[@id=\"img-pane\"]/div")).size());

        // check information in manage is correct
        driver.get(manageUrl);
        wait.until(util.pageLoadedCondition());
        Format f = new SimpleDateFormat("MM/dd/yy");
        String NumberPic = driver.findElement(By.xpath("/html/body/div[1]/div/div/div[1]/form/table/tbody/tr/td[3]")).getText();
        String lastNewPicTime = driver.findElement(By.xpath("/html/body/div[1]/div/div/div[1]/form/table/tbody/tr/td[2]")).getText();
        assertEquals(f.format(new Date()) ,lastNewPicTime);
        assertEquals("12", NumberPic);
    }

    @Test
    public void deletionTest() throws IOException {
        // test that deletion of the stream will have strong consistency (after redirect, deleted stream won't show up)
        // also test for deletion remove images's serving url. However, since google's image cdn caches images, the serving
        // url may still work after deletion.
        util.createNewStream("dog", "#dog#animal#puppy");
        util.createNewStream("cat", "#cat#animal#kitten");

        driver.get(Url + "/view?streamid=cat");
        wait.until(util.pageLoadedCondition());
        util.upLoadImages(utColoredDir);
        String cdnImg = driver.findElement(By.xpath("//*[@id=\"img-pane\"]/div[1]/a")).getAttribute("href");
        util.deleteAll();

        // all streams should now be deleted
        assertEquals(0, driver.findElements(By.xpath("//*[@id=\"img-pane\"]/div")).size());
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse rsp = client.execute(new HttpGet(cdnImg));
        // sometimes google cache cdn image, link may still be valid after deletion. Works on local server
        assertEquals(SC_NOT_FOUND, rsp.getStatusLine().getStatusCode());
    }

    @Test
    public void nonexistStreamTest() {
        driver.get(Url + "/view?streamid=owl");
        wait.until(util.pageLoadedCondition());
        assertEquals(manageUrl, driver.getCurrentUrl());
    }
}
