package project;

import net.bytebuddy.dynamic.scaffold.TypeInitializer;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static project.Constants.*;

public class Util {
    private WebDriver driver;
    private boolean logged_in;
    private WebDriverWait wait;

    public Util(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        logged_in = false;
    }
    public ExpectedCondition<Boolean> redirectionCondition (final String prevUrl){
        return new ExpectedCondition<Boolean>() {
            @NullableDecl
            @Override
            public Boolean apply(@NullableDecl WebDriver webDriver) {
                return !driver.getCurrentUrl().equals(prevUrl);
            }
        };
    }

    public ExpectedCondition<Boolean> pageLoadedCondition () {
        return new ExpectedCondition<Boolean>() {
            @NullableDecl
            @Override
            public Boolean apply(@NullableDecl WebDriver webDriver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
            }
        };
    }

    public ExpectedCondition<Boolean> searchLoadedCondition () {
        return new ExpectedCondition<Boolean>() {
            @NullableDecl
            @Override
            public Boolean apply(@NullableDecl WebDriver webDriver) {
                return driver.findElements(By.className("ui-menu-item")).size() > 0;
            }
        };
    }

    // may fail if FB changes their script
    public ExpectedCondition<Boolean> facebookLoadCondition (final String type) {
        return new ExpectedCondition<Boolean>() {
            @NullableDecl
            @Override
            public Boolean apply(@NullableDecl WebDriver webDriver) {
                return driver.findElement(By.className(type)).getAttribute("fb-xfbml-state").equals("rendered");
            }
        };
    }

    public ExpectedCondition<Boolean> imgUploaded (final String oldImg) {
        return new ExpectedCondition<Boolean>() {
            @NullableDecl
            @Override
            public Boolean apply(@NullableDecl WebDriver webDriver) {
                if (oldImg == null) {
                    return driver.findElements(By.xpath("//*[@id=\"img-pane\"]/div")).size() > 0;
                } else {
                    return !driver.findElement(By.xpath("//*[@id=\"img-pane\"]/div[1]/a")).getAttribute("href").equals(oldImg);
                }
            }
        };
    }

    public ExpectedCondition<Boolean> moreLoaded (String cursor, Boolean more) {
        return new ExpectedCondition<Boolean>() {
            @NullableDecl
            @Override
            public Boolean apply(@NullableDecl WebDriver webDriver) {
                if (more) {
                    return !((JavascriptExecutor) driver).executeScript("return returnCursor()").equals(cursor);
                } else {
                    return true;
                }
            }
        };
    }

    public void login(String email, String pwd) {
        // this may fail if Google changes their login page
        if (!logged_in) {
            driver.get(Url);
            driver.findElement(By.className("btn-outline-light")).click();
            wait.until(redirectionCondition(Url));

            wait.until(elementToBeClickable(By.id("identifierId")));
            String prevUrl = driver.getCurrentUrl();
            driver.findElement(By.id("identifierId")).sendKeys(email);
            driver.findElement(By.id("identifierNext")).click();
            wait.until(redirectionCondition(prevUrl));

            wait.until(elementToBeClickable(By.xpath("//*[@id=\"password\"]/div[1]/div/div[1]/input")));
            prevUrl = driver.getCurrentUrl();
            driver.findElement(By.xpath("//*[@id=\"password\"]/div[1]/div/div[1]/input"))
                    .sendKeys(pwd);
            driver.findElement(By.xpath("//*[@id=\"passwordNext\"]/content")).click();
            wait.until(redirectionCondition(prevUrl));
            logged_in = true;
        }
    }

    public void logout() {
        driver.get(manageUrl);
        wait.until(pageLoadedCondition());
        driver.findElement(By.xpath("/html/body/header/nav/a[2]")).click();
        wait.until(pageLoadedCondition());
        logged_in = false;
    }


    public void createTeststreams() {
        createNewStream("test1");
        createNewStream("test2");
        createNewStream("test3");
    }


    public void createNewStream(String name) {
        createNewStream(name, null);
    }

    public void createNewStream(String name, String tag) {
        driver.get(newStreamUrl);
        wait.until(elementToBeClickable(By.name("stream-name")));
        driver.findElement(By.name("stream-name")).sendKeys(name);
        if (tag != null) {
            driver.findElement(By.name("tags")).sendKeys(tag);
        }
        driver.findElement(By.id("create")).submit();
        wait.until(pageLoadedCondition());
    }

    public void deleteAll() {
        // delete each stream after test
        driver.get(manageUrl);
        wait.until(pageLoadedCondition());
        List<WebElement> streamToDelete = driver.findElements(By.name("steramToDelete"));
        for (WebElement e : streamToDelete) {
            e.click();
        }
        driver.findElement(By.xpath("//*[@id=\"main_content\"]/div/div/div[1]/form/button")).submit();
        wait.until(pageLoadedCondition());
    }

    public void unSubAll() {
        driver.get(manageUrl);
        wait.until(pageLoadedCondition());
        List<WebElement> streamToUnSub = driver.findElements(By.name("unsubscribe"));
        for (WebElement e : streamToUnSub) {
            e.click();
        }
        driver.findElement(By.xpath("//*[@id=\"main_content\"]/div/div/div[2]/form/button")).submit();
        wait.until(pageLoadedCondition());
    }

//    public String convertImage(File f) throws IOException {
//        int length = (int) f.length();
//        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(f));
//        byte[] bytes = new byte[length];
//        reader.read(bytes, 0, length);
//        reader.close();
//        String encoded = Base64.getEncoder().encodeToString(bytes);
//        return encoded;
//    }

    public List<String> loadFiles(String dir) {
        List<String> encodedFiles = new ArrayList<>();
        File folder = new File(dir);
        File[] fileNames = folder.listFiles();
        for (File f : fileNames) {
//            String encodedFile;
//            try {
//                encodedFile = convertImage(f);
//                encodedFiles.add(encodedFile);
//            }
//            catch(Exception e) {
//                System.err.println("image convert failed");
//            }
            String path = f.getAbsolutePath();
            path = path.replace("\\", "\\\\");
            encodedFiles.add(path);
        }
        return encodedFiles;
    }

    public void upLoadImages(String dir) {
        List<String> encodedFiles = loadFiles(dir);
        WebElement up = driver.findElement(By.xpath("/html/body/input"));
        StringBuilder imgToSend = new StringBuilder();
        for (int i = 0; i < encodedFiles.size() - 1; i++) {
            imgToSend.append(encodedFiles.get(i));
            imgToSend.append(" \n ");
        }
        imgToSend.append(encodedFiles.get(encodedFiles.size() - 1));
        String toSend = imgToSend.toString();
        up.sendKeys(toSend);
        driver.findElement(By.id("submit-image")).click();
        List<WebElement> curImage = driver.findElements(By.xpath("//*[@id=\"img-pane\"]/div"));
        String oldImg;
        if (curImage.size() == 0) {
            oldImg = null;
        } else {
            oldImg = driver.findElement(By.xpath("//*[@id=\"img-pane\"]/div[1]/a")).getAttribute("href");
        }
        wait.until(imgUploaded(oldImg));
    }
 }
