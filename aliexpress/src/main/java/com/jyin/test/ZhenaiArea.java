package com.jyin.test;

/*
 
*Selenium 2 WebDriver with JUnit 4
*/
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;

 
//import org.openqa.selenium.server.SeleniumServer;

public class ZhenaiArea {

	WebDriver driver  ;
	String baseUrl = new String();

//	@Before
//	public void setUp() throws Exception {
//		driver.get("http://www.google.com");
//	}
//
//	@Test
//	public void testAdvancedSearch() throws Exception {
//		driver.findElement(By.name("q")).sendKeys("Selenium 2.0 WebDriver");
//		driver.findElement(By.name("q")).submit();
//		System.out.println("Page title is: " + driver.getTitle());
//	}
//
//	@After
//	public void tearDown() throws Exception {
//		driver.quit();
//	}
//}
	
  @Before
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    baseUrl = "http://wd.koudai.com/item_classes.html?userid=254626746&c=11251752&des=%E5%A5%97%E8%A3%85";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  protected Function<WebDriver, Boolean> isPageLoaded() {
      return new Function<WebDriver, Boolean>() {
          public Boolean apply(WebDriver driver) {
              return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
          }
      };
  }

  public void waitForPageLoad() {
      WebDriverWait wait = new WebDriverWait(driver, 30);
      wait.until(isPageLoaded());
  }
  
  
  @Test
  public void testFinal() throws Exception {
    driver.get(baseUrl + "");
    ((JavascriptExecutor) driver).executeScript("scroll(0,document.body.scrollHeight*5);");
    List<WebElement> catElements = driver.findElements(By.xpath("//ul/li/a"));
    System.out.println("商品：");
    List<String> productLinks = new ArrayList<String>();
    for(WebElement catElement:catElements) {
    	//Object executeScript = ((JavascriptExecutor) driver).executeScript("return arguments[0].innerHTML;", catElement);
    	String productLink= catElement.getAttribute("href");
    	productLinks.add(productLink);
    	//break;
    }
    for(String productLink:productLinks) {
    	driver.get(productLink + "");
    	String productName = driver.findElement(By.xpath("//h2[@id='item_name']")).getText();
    	System.out.println(productName);
    	String productPrice = driver.findElement(By.xpath("//span[@id='item_price']")).getText();
    	System.out.println(productPrice);

        List<WebElement> imageElements = driver.findElements(By.xpath("//div[@id='detail_wrap']/img"));
        for(WebElement imageElement:imageElements) {
        	String imageUrl = imageElement.getAttribute("src");
        	System.out.println(imageUrl);
			saveImage(imageUrl,productName);
        }
    	
    }
//    for(WebElement provinceElement:catElements) {
//    	Object province = ((JavascriptExecutor) driver).executeScript("return arguments[0].innerHTML;", provinceElement);
//    	System.out.println(province+":");
//    	((JavascriptExecutor) driver).executeScript("return arguments[0].click();", provinceElement);
//    	List<WebElement> cityElements = driver.findElements(By.xpath("//div[@class='area_box_tr2']/a"));
//        for(WebElement cityElement:cityElements) {
//        	Object executeScript = ((JavascriptExecutor) driver).executeScript("return arguments[0].innerHTML;", cityElement);
//        	System.out.println("\t"+executeScript);
//        }
//    }
    
//    driver.findElement(By.id("xloginPasswordId")).clear();
//    driver.findElement(By.id("xloginPasswordId")).sendKeys("120325lucy");
//    driver.findElement(By.id("xloginPassportId")).clear();
//    driver.findElement(By.id("xloginPassportId")).sendKeys("lucyprettygift@126.com");
//    driver.findElement(By.id("signInButton")).click();
//    
//    waitForPageLoad();
//    
//    driver.findElement(By.linkText("Display Product")).click();
//    waitForPageLoad();
//    
//    
//    
//    System.out.println(driver.getTitle());  
//    // 获取当前页面句柄  
//    String handle = driver.getWindowHandle();  
//    // 获取所有页面的句柄，并循环判断不是当前的句柄，就做选取switchTo()  
//    for (String handles : driver.getWindowHandles()) {  
//        if (handles.equals(handle))  
//            continue;  
//        driver.switchTo().window(handles);  
//    }  
//    // 打印出当前页面title,发现我已经把控制权交给了注册页面  
//    System.out.println(driver.getTitle());  
//    
//    
//    
//    driver.findElement(By.id("pc-input-search")).sendKeys("testkeyset");
//
//    
//     		
//
//    // ERROR: Caught exception [ERROR: Unsupported command [selectWindow | name=nameStorage:{} | ]]
//    driver.findElement(By.cssSelector("a[title=\"Phones & Telecommunications\"] > span")).click();
//    driver.findElement(By.cssSelector("a[title=\"Mobile Phone Accessories & Parts\"] > span")).click();
//    driver.findElement(By.cssSelector("a[title=\"Mobile Phone Bags & Cases\"] > span")).click();
//    driver.findElement(By.id("pc-btn-next")).click();
//    
//    waitForPageLoad();
//
//    
//    
//    
//    //driver.findElement(By.id("help-index")).click();
//    driver.findElement(By.cssSelector("input.search-sel")).click();
//    driver.findElement(By.xpath("//div[@id='search-list-2242']/div[4]")).click();
//    try {
//    	new Select(driver.findElement(By.id("auto-id-351"))).selectByVisibleText("Case(手机壳)");
//    }catch (Error e){
//    	//e.printStackTrace();
//    	
//    }
//    
//    driver.findElement(By.xpath("//div[@id='post-property']/table[2]/tbody/tr/td[2]/input")).click();
//    driver.findElement(By.xpath("(//div[@id='search-list-2242']/div[2])[2]")).click();   
//    try {
//    	new Select(driver.findElement(By.id("auto-id-200001145"))).selectByVisibleText("Yes(有)");
//    }catch (Error e){
//    	//e.printStackTrace();
//    }
//    
//    
//    driver.findElement(By.xpath("//div[@id='post-property']/table[3]/tbody/tr/td[2]/input")).click();
//    driver.findElement(By.xpath("(//div[@id='search-list-2242']/div[2])[3]")).click();
//    try {
//        new Select(driver.findElement(By.id("auto-id-43"))).selectByVisibleText("Power Case(带电池的手机壳)");
//    }catch (Error e){
//    	//e.printStackTrace();
//    }
//    
//    
////    Product Name:Please enter Product name.
////    Product Keyword:Please enter one Product Keyword.
////    Product Photo:Please add a Product Photo.
////    Features:Please check items with different features are correctly priced.
////    Description:Please enter Detail description.
////    Packaging Details:Please enter Packaging Details.
////    Shipping template:Select Template
//    
//    
//    driver.findElement(By.xpath("//div[@id='post-property']/table[4]/tbody/tr/td[2]")).click();
//    driver.findElement(By.id("auto-id-3")).click();
//    driver.findElement(By.id("auto-id-3")).click();
//    driver.findElement(By.id("auto-id-3")).clear();
//    driver.findElement(By.id("auto-id-3")).sendKeys("S10");
//    driver.findElement(By.xpath("//div[@id='post-property']/table[6]/tbody/tr/td[2]")).click();
//    driver.findElement(By.id("pt-title")).clear();
//    driver.findElement(By.id("pt-title")).sendKeys("wooden bag");
//    driver.findElement(By.id("pk-primary-keyword")).clear();
//    driver.findElement(By.id("pk-primary-keyword")).sendKeys("wooden");
//// 
//    ((JavascriptExecutor) driver).executeScript("document.getElementById('pi-upload-images').value = 'fileDestOrder:1|fileFlag:add|fileHeight:160|fileId:0|fileMd5…XXagOFbXr/220788032/HT1DBpYFHtXXXagOFbXr.jpg|isError:false ';");   
//// 
//    driver.findElement(By.id("spu-price")).click();
//    driver.findElement(By.id("spu-price")).clear();
//    driver.findElement(By.id("spu-price")).sendKeys("19");
//    
//    driver.findElement(By.id("delivery-days")).sendKeys("3");
//    
//    driver.findElement(By.id("package-weight")).sendKeys("3");
//    driver.findElement(By.id("package-length")).sendKeys("3");
//    driver.findElement(By.id("package-width")).sendKeys("3");
//    driver.findElement(By.id("package-height")).sendKeys("3");
//// 
//     ((JavascriptExecutor) driver).executeScript("document.getElementsByClassName('kse-bwrap')[0].innerHTML   = 'this is a beautiful';");
//     //<p>	lenovo case p18 </p><p>	&nbsp; </p><p>	&nbsp; </p> 
//     //innerHTML
//// 
//    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(By.cssSelector("li[title=\"Wholesale\"]")));
//
//    
//     
//     
//     
// 
//   // driver.findElement(By.xpath("//div[@id='post-property']/table[4]/tbody/tr/td[2]")).click();
//     driver.findElement(By.id("btn-submit")).click();
//     ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(By.id("btn-submit")));
//          
//       waitForPageLoad();
//					
//       System.out.println(driver.getTitle());  
//       
// 
    
    
 
    		
    
 
  }
  
  
  	public void saveImage(String imageUrl, String dirname) throws Exception {
  		//new一个URL对象
  		URL url = new URL(imageUrl);
  		//打开链接
  		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
  		//设置请求方式为"GET"
  		conn.setRequestMethod("GET");
  		//超时响应时间为5秒
  		conn.setConnectTimeout(5 * 1000);
  		//通过输入流获取图片数据
  		InputStream inStream = conn.getInputStream();
  		//得到图片的二进制数据，以二进制封装得到数据，具有通用性
  		byte[] data = readInputStream(inStream);
  		//new一个文件对象用来保存图片，默认保存当前工程根目录
  		String[] split = imageUrl.split("/");
  		String fileName = split[split.length-1].split("[?]")[0];
  		File imageFile = new File("D:/weidian/"+dirname);
  		imageFile.mkdir();
  		imageFile = new File("D:/weidian/"+dirname+"/"+fileName);
  		//创建输出流
  		FileOutputStream outStream = new FileOutputStream(imageFile);
  		//写入数据
  		outStream.write(data);
  		//关闭输出流
  		outStream.close();
  	}
  	public static byte[] readInputStream(InputStream inStream) throws Exception{
  		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
  		//创建一个Buffer字符串
  		byte[] buffer = new byte[1024];
  		//每次读取的字符串长度，如果为-1，代表全部读取完毕
  		int len = 0;
  		//使用一个输入流从buffer里把数据读取出来
  		while( (len=inStream.read(buffer)) != -1 ){
  			//用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
  			outStream.write(buffer, 0, len);
  		}
  		//关闭输入流
  		inStream.close();
  		//把outStream里的数据写入内存
  		return outStream.toByteArray();
  	}
  


//  @After
//  public void tearDown() throws Exception {
//    driver.quit();
//    String verificationErrorString = verificationErrors.toString();
//    if (!"".equals(verificationErrorString)) {
//      fail(verificationErrorString);
//    }
//  }
// 
  
  
}
