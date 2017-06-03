package com.jyin.test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;

public class BaiduQianbao {
	private static class Sucai {
		public String title = "";
		public String imageUrl = "";
		public String content = "";
		public String linkUrl = "";
	}

	private static WebDriver driver;
	private static String FILE_DIR = "c:\\weixin\\sucai\\";
	
	static {
		init();
	}

	private static void init() {
		FILE_DIR = Thread.currentThread().getContextClassLoader().getResource("").getPath().replaceFirst("/", "").replace("/", "\\");
		System.out.println(FILE_DIR);
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	private static Function<WebDriver, Boolean> isPageLoaded() {
		return new Function<WebDriver, Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript(
						"return document.readyState").equals("complete");
			}
		};
	}

	private static void waitForPageLoad() {
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(isPageLoaded());
	}

	public static final String htmlToCode(String s) {
		if (s == null) {
			return "";
		} else {
			s = s.replace("\n\r", "<br>&nbsp;&nbsp;");
			s = s.replace("\r\n", "<br>&nbsp;&nbsp;");// 这才是正确的！
			s = s.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
			s = s.replace(" ", "&nbsp;");

			s = s.replace("\"", "\\" + "\"");// 如果原文含有双引号，这一句最关键！！！！！！
			return s;
		}
	}
	
	/**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    private static List<String> readFileByLines(String fileName) {
//        File file = new File(fileName);
        BufferedReader reader = null;
        List<String> lineList = new ArrayList<String>();
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"UTF-8"));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                System.out.println("line " + line + ": " + tempString);
                lineList.add(tempString);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return lineList;
    }


	private static List<Sucai> getSucaiList() {
		List<Sucai> sucaiList = new ArrayList<Sucai>();
		List<String> urlList = readFileByLines(FILE_DIR+"url.txt");
		String targetUrl = urlList.get(0);
		List<String> sucaiUrlList = null;
		if(targetUrl.contains("http://weixin.sogou.com/gzh")) {
			sucaiUrlList = readUrlsByWXSogouURL(targetUrl);
		}
		else if(targetUrl.contains("http://mp.weixin.qq.com/mp/getmasssendmsg")) {
			sucaiUrlList = readUrlsByWXHistoryURL(targetUrl);
		} 
		else {
			sucaiUrlList = urlList;
		}
		for (int i = 0;i<= sucaiUrlList.size();i++) {
			if(i==sucaiUrlList.size()) {
				break;
			}
			String sucaiUrl = sucaiUrlList.get(i);
			driver.get(sucaiUrl+"");
			sleep(5000);
			Long scrollHeight = (Long) ((JavascriptExecutor) driver)
			.executeScript("return document.body.scrollHeight;");
			Long availHeight = (Long) ((JavascriptExecutor) driver)
					.executeScript("return window.screen.availHeight;");
			Long scrollAvailHeight = availHeight;
			System.out.println("scrollHeight:"+scrollHeight);
			while(scrollHeight>scrollAvailHeight) {
				sleep(3000);
				((JavascriptExecutor) driver).executeScript("scroll(0,"+scrollAvailHeight+");");
				scrollAvailHeight = scrollAvailHeight+availHeight;
				System.out.println(scrollAvailHeight);
			}
			sleep(3000);
			driver.findElement(By.id("js_content"));
			waitForPageLoad();
			Sucai sucai = new Sucai();
			sucai.title = (String) ((JavascriptExecutor) driver)
					.executeScript("return msg_title;");
			sucai.title = sucai.title.replace("&nbsp;", " ");
			sucai.content = (String) ((JavascriptExecutor) driver)
					.executeScript("return document.getElementById('js_content').innerHTML;");
			sucai.content = sucai.content.replaceAll("\n", "");
			sucai.content = sucai.content.replaceAll("\r", "");
			
			sucai.imageUrl = (String) ((JavascriptExecutor) driver)
					.executeScript("return msg_cdn_url;");
			System.out.println(sucaiUrl+sucai.title+sucai.imageUrl);
			try {
				saveImage(sucai.imageUrl, FILE_DIR + (i+1) + ".jpg");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sucaiList.add(sucai);
		}
		return sucaiList;
	}

	private static List<String> readUrlsByWXSogouURL(String historyUrl) {
		//historyUrl = "http://weixin.sogou.com/gzh?openid=oIWsFt3rbRGIuAEOGWEJANIMwDhQ";
		driver.get(historyUrl+"");
		driver.findElement(By.xpath("//div[@class='p-more']/a")).click();
		List<WebElement> linkElements = driver.findElements(By.xpath("//div[@class='img_box2']/a"));
		List<String> urlList = new ArrayList<String>();
		int length = 8;
		if(length>linkElements.size()) {
			length = linkElements.size();
		}
		for(int i=0;i<length;i++) {
			urlList.add(linkElements.get(i).getAttribute("href"));
			System.out.println(linkElements.get(i).getAttribute("href"));
		}
		return urlList;
	}
	
	private static List<String> readUrlsByWXHistoryURL(String historyUrl) {
		//historyUrl = "http://weixin.sogou.com/gzh?openid=oIWsFt3rbRGIuAEOGWEJANIMwDhQ";
		driver.get(historyUrl+"");
		List<WebElement> linkElements = driver.findElements(By.xpath("//a"));
		List<String> urlList = new ArrayList<String>();
		for(int i=0;i<8;i++) {
			urlList.add(linkElements.get(i).getAttribute("hrefs"));
			System.out.println(linkElements.get(i).getAttribute("hrefs"));
		}
		return urlList;
	}

	private static void sleep(long millis) {
		try {
			Thread.sleep(millis);;
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private static void login(String userName, String password) {
		System.out.println("登陆信息："+userName);
		driver.get("https://www.baifubao.com/");
		driver.findElement(By.name("userName")).sendKeys(userName);
		driver.findElement(By.name("password")).sendKeys(password);
		driver.findElement(By.id("TANGRAM__PSP_4__submit")).click();
	}

	private static void transfer() {
		waitForPageLoad();
		WebElement remainingElement = driver.findElement(By
				.xpath("//em[@id='remaining-sum']/strong"));
		String remainingText = remainingElement.getText();
		float remainingAmout = Float.parseFloat(remainingText);
		if(remainingAmout>0) {
			driver.findElement(By.className("top-up")).click();//超级转账
			waitForPageLoad();
			driver.findElement(By.id("transfer-account")).sendKeys("jonyin8401@gmail.com");
			driver.findElement(By.id("transfer-amount")).sendKeys(remainingText);
			driver.findElement(By.id("payee_reason")).sendKeys(remainingText);
			driver.findElement(By.id("transfer-next")).click();
			waitForPageLoad();
			System.out.println("wait for popup submit");
			driver.findElement(By
					.xpath("//div[@class='btn-container']/a[@class='account-transfer-submit']")).click();
			waitForPageLoad();
			driver.findElement(By.id("if-use-balance")).click();
			driver.findElement(By.id("Balance_Password_Edit")).sendKeys("YJY840101");
			driver.findElement(By
					.xpath("//div[@id='balance-pay']/form[@id='pay-balance-form']/div[@class='layerout-div']/div[@class='dib form-scecond-container']/a[@class='next-step-btn dib']")).click();
			
			
		}		
	}
	
	public static void saveImage(String imageUrl, String fileName) throws Exception {
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
  		File imageFile = new File(fileName);
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

	public static void main(String[] args) {
		//List<Sucai> sucaiList = getSucaiList();
		List<String> accountList = readFileByLines(FILE_DIR+"baidu_account.txt");
		for(String account:accountList) {
			init();
			login(account.split(":")[1], "yjy840101");
			transfer();
		}
			
	}

	private static void logout() {
		waitForPageLoad();
		driver.findElement(By.id("logout")).click();
		waitForPageLoad();
	}

}
