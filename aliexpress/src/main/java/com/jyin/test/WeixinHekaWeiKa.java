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

public class WeixinHekaWeiKa {
	private static class Sucai {
		public String title = "";
		public String imageUrl = "";
		public String content = "";
		public String linkUrl = "";
	}

	private static WebDriver driver;
	private static String FILE_DIR = "c:\\weixin\\sucai\\hekaweika\\";
	
	static {
		init();
	}

	private static void init() {
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
		driver.get("https://mp.weixin.qq.com/cgi-bin/loginpage");
		driver.findElement(By.id("account")).sendKeys(userName);
		driver.findElement(By.id("pwd")).sendKeys(password);
		driver.findElement(By.id("loginBt")).click();
	}

	private static void duoTuWen(List<Sucai> sucaiList) {
		waitForPageLoad();
		List<WebElement> sucaiGuanliElement = driver.findElements(By
				.xpath("//dd[@class='menu_item ']/a"));
		for(WebElement element:sucaiGuanliElement) {
			if("素材管理".equalsIgnoreCase(element.getText())) {
				element.click();
				break;
			}
		}
		waitForPageLoad();
		WebElement aElement = driver.findElement(By.xpath("//a[@class='btn btn_input btn_primary r btn_new']"));
		driver.get(aElement.getAttribute("href"));
//		List<WebElement> duoTuwenElement = driver.findElements(By
//				.xpath("//span[@class='create_access jsCreate']/a"));
//		duoTuwenElement.get(1).click();
		waitForPageLoad();
		WebElement addElement = driver.findElement(By
				.xpath("//a[@id='js_add_appmsg']"));
		((JavascriptExecutor) driver).executeScript(
				"arguments[0].className='';", addElement);
		for (int i = 1; i < sucaiList.size(); i++) {
			addElement.click();
		}
		List<String> guanggaoList = readFileByLines(FILE_DIR+"guanggao.txt");
		List<String> guanzhuList = readFileByLines(FILE_DIR+"guanggao微信推广.txt");
		List<String> replaceList = readFileByLines(FILE_DIR+"tihuan.txt");
		for (int i = 1; i <= sucaiList.size(); i++) {
			waitForPageLoad();
			WebElement appmsgItemElement = driver.findElement(By
					.xpath("//div[@id='appmsgItem" + i + "']"));
			appmsgItemElement.click();
			WebElement editMaskElement;
			if (1 == i) {
				editMaskElement = driver
						.findElement(By
								.xpath("//div[@id='appmsgItem"
										+ i
										+ "']/div[@class='cover_appmsg_item']/div[@class='appmsg_thumb_wrp']"));
			} else {
				editMaskElement = driver.findElement(By
						.xpath("//div[@id='appmsgItem" + i
								+ "']/div[@class='appmsg_edit_mask']"));
			}
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].className='';", editMaskElement);
			editMaskElement.click();
			WebElement editElement = driver
					.findElement(By
							.xpath("//a[@class='icon18_common edit_gray js_edit' and @data-id='"
									+ i + "']"));
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].style.display='';", editElement);
			editElement.click();
			driver.findElement(
					By.xpath("//input[@class='frm_input js_title js_counter']"))
					.sendKeys(sucaiList.get(i - 1).title);
			WebElement uploadImageElement = driver.findElement(By
					.xpath("//input[@type='file']"));
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].style.display='block';", uploadImageElement);
			uploadImageElement.sendKeys(FILE_DIR + i + ".jpg");
			driver.findElement(By.className("icon_checkbox")).click();
			String gzhName = driver.findElement(By.className("nickname")).getText();
			
			String startContent="<p><img data-src=\"http://mmbiz.qpic.cn/mmbiz/iahnLqLKxk2PEFsE7Qtwsp5AT8icdhia7aHpUQHW2l55mNKrl6Zc5eKRV5SaUCqBL3PF8jvUWLecTMVq8AfGXe5rA/0\" style=\"width: auto !important; height: auto !important; visibility: visible !important;\" class=\"\" id=\"c1422359622363\" data-type=\"gif\" data-ratio=\"0.1\" data-w=\"500\" _width=\"auto\" src=\"http://mmbiz.qpic.cn/mmbiz/iahnLqLKxk2PEFsE7Qtwsp5AT8icdhia7aHpUQHW2l55mNKrl6Zc5eKRV5SaUCqBL3PF8jvUWLecTMVq8AfGXe5rA/0?tp=webp\"></p>";
			if("免费平台".equalsIgnoreCase(gzhName)){
				//startContent = "<p style=\"margin: 0px auto; padding: 0px; max-width: 600px; word-wrap: normal; min-height: 1em; white-space: normal; line-height: 24px; border: 1px solid rgb(0, 187, 236); color: rgb(68, 68, 68); font-family: 微软雅黑; font-size: 12px; box-sizing: border-box !important; border-radius: 2em !important; background-color: rgb(255, 255, 255);\"><span class=\"main\" style=\"margin: 0px; padding: 2px 2px 2px 6px; max-width: 100%; border: 0px; color: rgb(255, 255, 255); box-sizing: border-box !important; word-wrap: break-word !important; border-top-left-radius: 2em !important; border-bottom-left-radius: 2em !important; background-color: rgb(0, 187, 236);\"><span style=\"margin: 0px; padding: 0px; max-width: 100%; border: 0px; line-height: 0px; box-sizing: border-box !important; word-wrap: break-word !important;\">﻿</span><img data-ratio=\"0.5\" data-w=\"22\" data-src=\"http://mmbiz.qpic.cn/mmbiz/mj9u1OBZRqP8EvePIzqrRIHCHOzYM4ngF6tp3gjiaPQxwzT0ZR0XYZR7fTf4Pw5Xc6HV4Nw7WtbzOb8KuNnezJQ/0/mmbizgif\" width=\"auto\" style=\"margin: 0px; padding: 0px; border: 0px; box-sizing: border-box !important; word-wrap: break-word !important; visibility: visible !important; width: auto !important; height: auto !important;\" _width=\"auto\" src=\"http://mmbiz.qpic.cn/mmbiz/mj9u1OBZRqP8EvePIzqrRIHCHOzYM4ngF6tp3gjiaPQxwzT0ZR0XYZR7fTf4Pw5Xc6HV4Nw7WtbzOb8KuNnezJQ/0/mmbizgif?tp=webp\"> <strong style=\"margin: 0px; padding: 0px; max-width: 100%; border: 0px; box-sizing: border-box !important; word-wrap: break-word !important;\">提示</strong>：</span><span style=\"margin: 0px; padding: 0px 0px 0px 2px; max-width: 100%; border: 0px; box-sizing: border-box !important; word-wrap: break-word !important;\"><span style=\"margin: 0px; padding: 0px; max-width: 100%; border: 0px; color: rgb(127, 127, 127); box-sizing: border-box !important; word-wrap: break-word !important;\">点击<span style=\"margin: 0px; padding: 0px; max-width: 100%; color: rgb(0, 187, 236); box-sizing: border-box !important; word-wrap: break-word !important;\">↑</span>上方</span><span style=\"margin: 0px; padding: 0px 0px 0px 2px; max-width: 100%; border: 0px; box-sizing: border-box !important; word-wrap: break-word !important;\">\"</span><strong style=\"margin: 0px; padding: 0px; max-width: 100%; border: 0px; box-sizing: border-box !important; word-wrap: break-word !important;\"><span style=\"margin: 0px; padding: 0px 0px 0px 2px; max-width: 100%; border: 0px; color: rgb(0, 112, 192); box-sizing: border-box !important; word-wrap: break-word !important;\">"+gzhName+"</span></strong><span style=\"margin: 0px; padding: 0px 0px 0px 2px; max-width: 100%; border: 0px; box-sizing: border-box !important; word-wrap: break-word !important;\">\"</span><span style=\"margin: 0px; padding: 0px; max-width: 100%; border: 0px; color: rgb(127, 127, 127); box-sizing: border-box !important; word-wrap: break-word !important;\">免费关注我们</span></span></p>";
			}
			String url=guanggaoList.get(0);
			if("免费平台".equalsIgnoreCase(gzhName)){
				//url = guanggaoList.get(0);
				url = guanzhuList.get(0);
				//url = "http://mp.weixin.qq.com/s?__biz=MjM5NDk4MzYzNw==&mid=204075030&idx=1&sn=cfe84847abdb1d4348b351d9de78d52a#rd";
			}
			driver.findElement(
					By.xpath("//input[@class='js_url frm_input']"))
					.sendKeys(url);
			String cardName="蝴蝶飞舞";
			String content = sucaiList.get(i - 1).content.replace("笑得肚子疼", gzhName);
			content = content.replace("xddzt0088", gzhName);
			content = content.replace("s9GQle8DWLFRnpPu64iaWiaHkCcByyThNHXjyc7IlmygAibBDHwgnUHztHgOB7fAseEdEjYew1f1mqu4DsiaC6YpHg","s9GQle8DWLE3r386XiaJHm1V4FlkHn5GwsWWC8Fz4JXB59jbZdxibDxfl4Yco1YL9QIEbC5OPcFmMKP0A0wQyA4A");
			content = content.replace("s9GQle8DWLFRnpPu64iaWiaHkCcByyThNH4giaZk2iaGcicKqYoTa5y3kPhD6UbbiabbzicPrur0MMEreVfXDVVn6ww5w","s9GQle8DWLE3r386XiaJHm1V4FlkHn5GwsWWC8Fz4JXB59jbZdxibDxfl4Yco1YL9QIEbC5OPcFmMKP0A0wQyA4A");
			content = content.replace("s9GQle8DWLHGyspD171HOmILCtQxQnayLzcRbCZntpticTyqBzxqF1B2OQv5oDtjCMHOOrWkaudWOUMoHxzBcPg","s9GQle8DWLE3r386XiaJHm1V4FlkHn5GwsWWC8Fz4JXB59jbZdxibDxfl4Yco1YL9QIEbC5OPcFmMKP0A0wQyA4A");
			//sucaiList.get(i - 1).content = sucaiList.get(i - 1).content.replace("关注", cardName);
			for(String replaceStr:replaceList) {
				CharSequence oldChar = replaceStr.split("@@@@")[0];
				CharSequence newChar = replaceStr.split("@@@@").length==2?replaceStr.split("@@@@")[1]:"";
				newChar = newChar.toString().replace("#GZHNAME#", gzhName);
				content = content.replace(oldChar, newChar);
			}
			String endContent = guanggaoList.get(1);
			if("免费平台".equalsIgnoreCase(gzhName)){
				//endContent = guanggaoList.get(1);
				endContent = guanzhuList.get(1);				
				//url = "http://mp.weixin.qq.com/s?__biz=MjM5NDk4MzYzNw==&mid=204075030&idx=1&sn=cfe84847abdb1d4348b351d9de78d52a#rd";
			}
			try {
				((JavascriptExecutor) driver)
						.executeScript("document.getElementById('ueditor_0').contentDocument.getElementsByClassName('view')[0].innerHTML   = '"
//								+ startContent
								+ content
								+ endContent
								+ "';");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		driver.findElement(By.xpath("//span[@id='js_send']/button")).click();
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
		List<Sucai> sucaiList = getSucaiList();
		List<String> accountList = readFileByLines(FILE_DIR+"account.txt");
		for(String account:accountList) {
			init();
			login(account.split("##")[0], account.split("##")[1]);
			duoTuWen(sucaiList);
		}
			
	}

	private static void logout() {
		waitForPageLoad();
		driver.findElement(By.id("logout")).click();
		waitForPageLoad();
	}

}
