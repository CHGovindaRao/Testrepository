package test;
//import static com.qtpselenium.test.DriverScript.CONFIG;

//import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;


public class TestCase_Springer {

	public static void main(String[] args) {
        WebDriver driver = new FirefoxDriver();
        driver.get("http://www.springerprofessional.de/");
        driver.findElement(By.xpath("//*[@id='loginHomeButton']")).click();
        driver.switchTo().frame("_cas_login");
                 
       driver.findElement(By.xpath("//*[@id='sidebox_username']")).sendKeys("testuser1@springerprofessional.com");
       driver.findElement(By.xpath("//*[@id='sidebox_password']")).sendKeys("testuser1");
       driver.findElement(By.xpath(".//*[@id='fmaSubmit']")).click();
      // long implicitWaitTime=Long.parseLong(CONFIG.getProperty("implicitwait"));
		//driver.manage().timeouts().implicitlyWait(implicitWaitTime, TimeUnit.SECONDS);
        driver.switchTo().defaultContent();
        driver.findElement(By.xpath("//*[@id='loginContext']/a")).click();

	

	



	}
	}

