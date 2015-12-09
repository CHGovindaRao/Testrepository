package pageobjects;


import omelet.data.IProperty;
import omelet.data.driverconf.IBrowserConf;
import omelet.driver.Driver;

import org.openqa.selenium.WebDriver;

public class PageObjectFactory {

	
	private WebDriver driver;
	private IProperty prop;
	
	private SeleniumPage seleniumPage;
	private GooglePage googlePage;
	GmailHomePage gmailHomePage;
	
	
	public PageObjectFactory(IBrowserConf browserConf, IProperty prop){
		this.prop = prop;
		driver = Driver.getDriver(browserConf);
		driver.manage().window().maximize();
		
	}
	
	public SeleniumPage seleniumPage(){
		
		if(null==seleniumPage)
			seleniumPage = new SeleniumPage(driver,prop);
		
		return seleniumPage;
	}
	
	public GooglePage googlePage(){
		
		if(null==googlePage)
			googlePage = new GooglePage(driver,prop);
		
		return googlePage;
	}
	
public GmailHomePage gmailHomePage(){
		
		if(null==googlePage)
			gmailHomePage = new GmailHomePage(driver,prop);
		
		return gmailHomePage;
	}
	
	
	
}
