package bandpage;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Bandpage {
    static WebDriver driver;
    static Wait<WebDriver> wait;

    public static void main(String[] args) throws Exception {

    	try {
        driver = new FirefoxDriver();
        wait = new WebDriverWait(driver, 30);
        driver.get("http://www.flickr.com/");
        boolean result;
        
            result = pageCotains("Sign In");
      
        System.out.println("Flickr Home Page load " + (result? "passed." : "failed."));
        if (!result) {
        	driver.close();
        	throw new Exception("Home page: cannot find Sign in Link");
        }
        //get auth token before calling upload photo api
    	FlickrAuth.retrieveToken();
    	String photoID=FlickrAuth.upload();
    	System.out.println("Newly uploaded photoid is: " + photoID);
    	if(photoID!=null){
    		result=true;
    	}
    	System.out.println("Flickr API photo upload " + (result? "passed." : "failed."));
    	
    	driver.get("http://www.flickr.com/");
    	Thread.sleep(3000);
        //login to Flickr  
      
        result = pageCotains("Your Photostream")&&pageCotains(photoID);
      
        System.out.println("Uploaded photo showed on home page " + (result? "passed." : "failed."));
        if (!result) {
        	driver.close();
        	throw new Exception("failed to retreive uploaded photo");
        	}
        
        result = goToPhoto(photoID);
        System.out.println("Navigate to newly uploaded photo " + (result? "passed." : "failed."));
        
    	} catch(Exception e) {
              e.printStackTrace();
         }
        finally{
        	driver.quit();
        }
            

    
    }
    public static boolean goToPhoto(String photoid)  throws Exception {
    	
    	String photoURL="http://www.flickr.com/photos/bandpage-automation/"+photoid+"/in/photostream";
    	        driver.get(photoURL);   
    	        Thread.sleep(3000);  
	        	String title = driver.findElement(By.name("title")).getAttribute("content");
	        	//System.out.println("photoTitle  " + title);
    	        if(!title.equals(FlickrAuth.photoFile)){
    	        	System.out.println("Uploaded photo has wrong Title");
    	        	return false;
    	        }
    	        return true;
    }
    public static boolean authenticate (String request) throws Exception {
    	//System.out.println("current request " + request);
        driver.get(request);
        Thread.sleep(3000);
        if(!pageCotains("Sign In")){
        	throw new Exception("failed the login request");
        }       
        signIn("autotest11@rocketmail.com", "Harmony1");
        if(!pageCotains("Automation101 wants to link to your Flickr account."))
        {	throw new Exception("failed to login"); }
         
        driver.findElement(By.className("Butt")).click();
        Thread.sleep(3000);
        if(!pageCotains("You have successfully authorized the application")){
        	throw new Exception("failed to authenticate");
        }
        //System.out.println("Successfully Authenticate user");
        return true;
    }


    private static boolean pageCotains(String str) {
    	//System.out.println("PageCotains is called: current window is "+driver.getTitle());
        return driver.getPageSource().contains(str);
    }
    public static void signInPage()  throws Exception {
    	
 //       String parentWindowHandle = driver.getWindowHandle(); // save the current window handle.
//        WebDriver popup = null;
        driver.findElement(By.id("head-signin-link")).click();    
        Thread.sleep(3000);  
        Set<String> windows = driver.getWindowHandles();

        for (String window : windows) {
            driver.switchTo().window(window);
            //System.out.println("window title: "+driver.getTitle());
            if (driver.getTitle().contains("Sign in to Yahoo!")) {
                return;
            }
        }
    }
    public static void signIn(String username, String password)  throws Exception {
            	driver.findElement(By.id("username")).sendKeys(username);
            	driver.findElement(By.id("passwd")).sendKeys(password);
            	driver.findElement(By.id(".save")).click(); 
            	Thread.sleep(3000); 
 //               String parentWindowHandle = driver.getWindowHandle(); // save the current window handle.
                Set<String> windows = driver.getWindowHandles();

                for (String window : windows) {
                    driver.switchTo().window(window);
                    //("window title: "+driver.getTitle());
                    if (!driver.getPageSource().contains("Sign Out")) {
                    	throw new Exception("failed to sign in with provided accounts");
                    }
                }
                return;
                
    }
 

}
