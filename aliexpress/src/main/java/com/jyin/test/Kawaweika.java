package com.jyin.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By.ByTagName;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;

public class Kawaweika {
	WebDriver driver;
	static final String dir = "C:/Users/Administrator/Documents/weitui/website/heka/";
	static final String jsContent = "<script type='text/javascript'>"
	  +" function getQueryString(name) {                                 "
	  +"     var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');"
	  +"     var r = window.location.search.substr(1).match(reg);        "
	  +"     if (r != null) return decodeURI(r[2]);                       "
	  +"     return '';                                                 "
	  +" }                                                               "
	  +" function getId() {                                              "
	  +"     var id = getQueryString('id');                              "
	  +"     if(null==id||''==id) {                                      "
	  +"         id = '@1@';                                           "
	  +"     }                                                           "
	  +"     return id;                                                  "
	  +" }                                                               "
	  +" function getNote() {                                            "
	  +"     var note = getQueryString('note');                          "
	  +"     if(null==note||''==note) {                                  "
	  +"         note='@2@';"
	  +"     }                                                           "
	  +"     return note;                                                "
	  +" }                                                               "
      +"     var cardid = getId();                                           "
      +"     var note=getNote();                                         "
      +"     var vtype=getQueryString('vtype');                          "
      +"     var vid=getQueryString('vid'); var musicUrl='@3@'; if(null!=vtype&&''!=vtype&&null!=vid&&''!=vid) {var musicUrl='voice/'+vtype+'/'+vid+'.mp3';}                            "
      +" </script>                                                       "
      +"\n<script src=\"./js/heka.js";
	static final String jsKawaContent = "<script type='text/javascript'> " 
			  +" function getQueryString(name) {                                 "
			  +"     var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');"
			  +"     var r = window.location.search.substr(1).match(reg);        "
			  +"     if (r != null) return decodeURI(r[2]);                       "
			  +"     return '';                                                 "
			  +" }                                                               "
			  +" function getId() {                                              "
			  +"     var id = getQueryString('id');                              "
			  +"     if(null==id||''==id) {                                      "
			  +"         id = '@1@';                                           "
			  +"     }                                                           "
			  +"     return id;                                                  "
			  +" }                                                               "
			  +" function getNote() {                                            "
			  +"     var note = getQueryString('note');                          "
			  +"     if(null==note||''==note) {                                  "
			  +"         note='@2@';"
			  +"     }                                                           "
			  +"     return note;                                                "
			  +" }                                                               "
		      +"     var cardid = getId();                                           "
		      +"     var note=getNote();                                         "
		      +"     var vtype=getQueryString('vtype');                          "
		      +"     var vid=getQueryString('vid'); var musicUrl='@3@'; if(null!=vtype&&''!=vtype&&null!=vid&&''!=vid) {var musicUrl='voice/'+vtype+'/'+vid+'.mp3';}                            "
		      +" </script>                                                       "
		      +"\n<script src=\"./js/heka.js";
	static final String kawaHost = "http://kwvk.oicp.net/1heka/kawaweika";

	public Kawaweika() throws Exception {
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	protected Function<WebDriver, Boolean> isPageLoaded() {
		return new Function<WebDriver, Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript(
						"return document.readyState").equals("complete");
			}
		};
	}

	public void waitForPageLoad() {
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(isPageLoaded());
	}

	public void processCard(String pageUrl) throws Exception {
		driver.get(pageUrl);
		String fileName = pageUrl.replace(kawaHost, "")
				.replace("php", "html");
		String pageSource = driver.getPageSource();
		List<WebElement> imgElements = driver.findElements(ByTagName.tagName("img"));
		for(WebElement imgElement:imgElements) {
			String attributeImg = imgElement.getAttribute("src");
			if(attributeImg!=null&&attributeImg.contains(kawaHost)) 
			saveImage(attributeImg, attributeImg.replace(kawaHost, ""));
		}
		List<WebElement> scriptElements = driver.findElements(ByTagName.tagName("script"));
		String scriptSrc = "";
		for(WebElement scriptElement:scriptElements) {
			if(scriptElement.getAttribute("src")!=null&&scriptElement.getAttribute("src").contains("kawa.php?")) {
				scriptSrc = scriptElement.getAttribute("src");
				break;
			}
		}
		String id = "";
		String musicUrl = "";
		try {
			if(!scriptSrc.contains("id=")){
				scriptSrc = (String) ((JavascriptExecutor) driver)
				.executeScript("return gShareUrl;");
				id = scriptSrc.split("/")[scriptSrc.split("/").length-1].split(".php")[0];
				musicUrl = (String) ((JavascriptExecutor) driver)
						.executeScript("return gSoundUrl;");
			}
			else {
				id = scriptSrc.split("(\\?)")[1].split("id=")[1].split("&")[0];
				musicUrl = (String) ((JavascriptExecutor) driver)
						.executeScript("return kawa_data.music;");
			}
		} catch (Exception e) {
			musicUrl = (String) ((JavascriptExecutor) driver)
					.executeScript("return gSound;");
			//e.printStackTrace();
		}
		saveImage(kawaHost+"/img/home/"+id+".jpg","/img/home/"+id+".jpg");
		String note = scriptSrc.split("(\\?)")[1].split("note=")[1].split("&")[0];
		note = URLDecoder.decode(note,"gb2312");
		//note = URLEncoder.encode(note,"utf-8");
		String jsReplaceContent = jsContent.replace("@1@", id);
		jsReplaceContent = jsReplaceContent.replace("@2@", note);
		saveImage(kawaHost+"/"+musicUrl,"/"+musicUrl);
		jsReplaceContent = jsReplaceContent.replace("@3@", musicUrl);
		pageSource = pageSource.replace("<script src=\"kawa.php", jsReplaceContent);
		pageSource = pageSource.replace(kawaHost, ".");
		pageSource = pageSource.replace("words 			   :", "words 			   : note, //");
		pageSource = pageSource.replace("music              :", "music              : musicUrl, //");
		pageSource = pageSource.replace("gb2312", "utf-8");
		pageSource = pageSource.replace("wx.config", "testXXX");
		pageSource = pageSource.replace("wx.onMenuShareAppMessage", "testXXX");
		pageSource = pageSource.replace("wx.onMenuShareAppMessage", "testXXX");
		pageSource = pageSource.replace("wx.onMenuShareTimeline", "testXXX");
		pageSource = pageSource.replace("wx.ready", "testXXX");
		
		pageSource = pageSource.replace("showcard_weixin.php", "showcard_weixin.js");
		pageSource = pageSource.replace("function Customize()", "function testXXX1()");
		pageSource = pageSource.replace("function Share()", "function testXXX2()");
		pageSource = pageSource.replace("function Follow()", "function testXXX3()");
		pageSource = pageSource.replace("function GetMore()", "function testXXX4()"); 
		
		pageSource = pageSource.replace("gTextDesc =", "gTextDesc = cardnote;//");
		pageSource = pageSource.replace("gTextDesc=", "gTextDesc = cardnote;//");
		pageSource = pageSource.replace("gCardText =", "gCardText = cardnote;//");
		pageSource = pageSource.replace("gCardText=", "gCardText = cardnote;//");
		pageSource = pageSource.replace("gSound =", "gSound = musicUrl;//");
		pageSource = pageSource.replace("gSound=", "gSound = musicUrl;//");
		pageSource = pageSource.replace("gSoundUrl =", "gSoundUrl = musicUrl;//");
		pageSource = pageSource.replace("gSoundUrl=", "gSoundUrl = musicUrl;//");
		pageSource = pageSource.replace("&lt;", "<");
		pageSource = pageSource.replace("&gt;", ">");
		pageSource = pageSource.replace("&amp;", "&");
		this.writeToFile(fileName, pageSource);
	}
	
	public void processOldCard(String pageUrl) throws Exception {
		driver.get(pageUrl);
		String fileName = pageUrl.replace(kawaHost, "")
				.replace("php", "html");
		String pageSource = driver.getPageSource();
		List<WebElement> imgElements = driver.findElements(ByTagName.tagName("img"));
		for(WebElement imgElement:imgElements) {
			String attributeImg = imgElement.getAttribute("src");
			if(attributeImg!=null&&attributeImg.contains(kawaHost)) 
			saveImage(attributeImg, attributeImg.replace(kawaHost, ""));
		}
		List<WebElement> scriptElements = driver.findElements(ByTagName.tagName("script"));
		String scriptSrc = "";
		for(WebElement scriptElement:scriptElements) {
			if(scriptElement.getAttribute("src")!=null&&scriptElement.getAttribute("src").contains("kawa.php?")) {
				scriptSrc = scriptElement.getAttribute("src");
				break;
			}
		}
		String id = "";
		if(!scriptSrc.contains("id=")){
			scriptSrc = (String) ((JavascriptExecutor) driver)
			.executeScript("return gShareUrl;");
			id = scriptSrc.split("/")[scriptSrc.split("/").length-1].split(".php")[0];
		}
		else {
			id = scriptSrc.split("(\\?)")[1].split("id=")[1].split("&")[0];
		}
		saveImage(kawaHost+"/img/home/"+id+".jpg","/img/home/"+id+".jpg");
		String note = scriptSrc.split("(\\?)")[1].split("note=")[1].split("&")[0];
		note = URLDecoder.decode(note,"gb2312");
		//note = URLEncoder.encode(note,"utf-8");
		String jsReplaceContent = jsContent.replace("@1@", id);
		jsReplaceContent = jsReplaceContent.replace("@2@", note);
		String musicUrl = (String) ((JavascriptExecutor) driver)
				.executeScript("return gSoundUrl;");
		if(musicUrl ==null||musicUrl == ""){
			scriptSrc = (String) ((JavascriptExecutor) driver)
			.executeScript("return gSoundUrl;");
		}
		saveImage(kawaHost+"/"+musicUrl,"/"+musicUrl);
		jsReplaceContent = jsReplaceContent.replace("@3@", musicUrl);
		pageSource = pageSource.replace("<script src=\"kawa.php", jsReplaceContent);
		pageSource = pageSource.replace("<script src=\"kawa.php", "<script src=\"./js/heka.js");
		pageSource = pageSource.replace(kawaHost, ".");
		pageSource = pageSource.replace("words 			   :", "words 			   : note, //");
		pageSource = pageSource.replace("music              :", "music              : musicUrl, //");
		pageSource = pageSource.replace("gb2312", "utf-8");
		pageSource = pageSource.replace("wx.config", "testXXX");
		pageSource = pageSource.replace("wx.onMenuShareAppMessage", "testXXX");
		pageSource = pageSource.replace("wx.onMenuShareAppMessage", "testXXX");
		pageSource = pageSource.replace("wx.onMenuShareTimeline", "testXXX");
		pageSource = pageSource.replace("wx.ready", "testXXX");
		this.writeToFile(fileName, pageSource);
	}
	
	public void processKawaCard(String pageUrl) throws Exception {
		driver.get(pageUrl);
		String cardId = pageUrl.split("&")[0].split("cardid=")[1];
		String fileName = cardId+".html";
		String pageSource = driver.getPageSource();
		List<WebElement> imgElements = driver.findElements(ByTagName.tagName("img"));
		int imgIndex = 0;
		for(WebElement imgElement:imgElements) {
			String attributeImg = imgElement.getAttribute("src");
			if(attributeImg.contains("kawa1.gif")||attributeImg.contains("music_note_big.png")) {
				continue;
			}
			String imgFileName = "images/"+cardId+"/"+imgIndex+".png";
			System.out.println(attributeImg);
			saveImage(attributeImg, imgFileName);
			pageSource = pageSource.replace(attributeImg, imgFileName);
			imgIndex++;
		}
		List<WebElement> scriptElements = driver.findElements(ByTagName.tagName("script"));
		String scriptSrc = "";
		for(WebElement scriptElement:scriptElements) {
			if(scriptElement.getAttribute("src")!=null&&scriptElement.getAttribute("src").contains("kawa.php?")) {
				scriptSrc = scriptElement.getAttribute("src");
				break;
			}
		}
		String id = cardId;
		String musicUrl = "";
		String cardIcon = "";
		String note = "";
		try {
			musicUrl = (String) ((JavascriptExecutor) driver)
						.executeScript("return kawa_data.music;");
			cardIcon = (String) ((JavascriptExecutor) driver)
					.executeScript("return kawa_data.icon;");
			note = (String) ((JavascriptExecutor) driver)
					.executeScript("return kawa_data.words;");
		} catch (Exception e) {
			musicUrl = (String) ((JavascriptExecutor) driver)
					.executeScript("return gSound;");
			//e.printStackTrace();
		}
		saveImage(cardIcon,"/img/home/"+id+".jpg");
		//note = URLEncoder.encode(note,"utf-8");
		String jsReplaceContent = jsKawaContent.replace("@1@", id);
		jsReplaceContent = jsReplaceContent.replace("@2@", note);
		String musicFileName = "sound/"+musicUrl.split("/")[musicUrl.split("/").length-1];
		saveImage(musicUrl,musicFileName);
		jsReplaceContent = jsReplaceContent.replace("@3@", musicFileName);
		pageSource = pageSource.replace("<script src=\"kawa.js", jsReplaceContent);
		pageSource = pageSource.replace("<script src=\"kawamovie.js", "<script src=\"./js/kawamovie.js");
		pageSource = pageSource.replace(kawaHost, ".");
		pageSource = pageSource.replace("words = ", "words = note, //");
		pageSource = pageSource.replace("music              :", "music              : musicUrl, //");
		pageSource = pageSource.replace("gb2312", "utf-8");
		pageSource = pageSource.replace("wx.config", "function testXXX(){};testXXX");
		pageSource = pageSource.replace("wx.onMenuShareAppMessage", "testXXX");
		pageSource = pageSource.replace("wx.onMenuShareAppMessage", "testXXX");
		pageSource = pageSource.replace("wx.onMenuShareTimeline", "testXXX");
		pageSource = pageSource.replace("wx.ready", "testXXX");
		
		pageSource = pageSource.replace("showcard_weixin.php", "showcard_weixin.js");
		pageSource = pageSource.replace("function Customize()", "function testXXX1()");
		pageSource = pageSource.replace("function Share()", "function testXXX2()");
		pageSource = pageSource.replace("function Follow()", "function testXXX3()");
		pageSource = pageSource.replace("function GetMore()", "function testXXX4()"); 
		
		pageSource = pageSource.replace("gTextDesc =", "gTextDesc = cardnote;//");
		pageSource = pageSource.replace("gTextDesc=", "gTextDesc = cardnote;//");
		pageSource = pageSource.replace("gCardText =", "gCardText = cardnote;//");
		pageSource = pageSource.replace("gCardText=", "gCardText = cardnote;//");
		pageSource = pageSource.replace("gSound =", "gSound = musicUrl;//");
		pageSource = pageSource.replace("gSound=", "gSound = musicUrl;//");
		pageSource = pageSource.replace("gSoundUrl =", "gSoundUrl = musicUrl;//");
		pageSource = pageSource.replace("gSoundUrl=", "gSoundUrl = musicUrl;//");
		pageSource = pageSource.replace("&lt;", "<");
		pageSource = pageSource.replace("&gt;", ">");
		pageSource = pageSource.replace("&amp;", "&");
		this.writeToFile(fileName, pageSource);
	}

	public static void saveImage(String urlString, String fileName) {
		fileName = fileName.split("\\?")[0];
		//System.out.println(urlString);
		// 构造URL
		try {
			URL url = new URL(urlString);
			// 打开连接
			URLConnection con = url.openConnection();
			// 设置请求超时为5s
			con.setConnectTimeout(5 * 1000);
			// 输入流
			InputStream is = con.getInputStream();
	
			// 1K的数据缓冲
			byte[] bs = new byte[1024];
			// 读取到的数据长度
			int len;
			// 输出的文件流
			File file = new File(dir + fileName);
			File dir = new File(file.getParent());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			OutputStream os = new FileOutputStream(file);
			// 开始读取
			while ((len = is.read(bs)) != -1) {
				os.write(bs, 0, len);
			}
			// 关闭
			os.close();
			is.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void writeToFile(String fileName, String content) throws Exception {
		File file = new File(dir + fileName);
		File dir = new File(file.getParent());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (!file.exists())
			file.createNewFile();
		FileOutputStream out = new FileOutputStream(file, false);
		out.write(content.getBytes("utf-8"));
		out.close();
	}

	public static void main(String[] args) throws Exception {
		Kawaweika ka = new Kawaweika();
		//ka.ddd("http://kwvk.oicp.net/1heka/kawaweika/kawa.php?id=y2112&note=%C8%CB%D3%D0%CA%B1%BA%F2%B8%D0%B5%BD%BA%DC%C0%DB%A3%AC%C0%DB%B5%C4%C9%ED%D0%C4%C6%A3%B1%B9%A3%AC%C0%DB%B5%C4%D0%C4%C1%A6%BD%BB%B4%E1%A1%A3%B2%BB%CF%EB%BA%CD%C8%CE%BA%CE%C8%CB%CB%B5%BB%B0%A3%AC%C1%AC%C9%FA%C6%F8%BA%CD%BC%C6%BD%CF%B5%C4%C1%A6%C6%F8%B6%BC%C3%BB%D3%D0%C1%CB%A3%AC%D6%BB%CF%EB%D2%BB%B8%F6%C8%CB%BE%B2%BE%B2%B5%C4%B7%A2%B4%F4%A3%AC%D2%F2%CE%AA%A3%AC%D3%D0%CC%AB%B6%E0%B5%C4%CE%DE%C4%CE%A3%AC%CE%DE%B7%A8%CA%CD%BB%B3%7E&vtype=&vid=","/js/kawa.js");
//		ka.ddd("http://kwvk.oicp.net/1heka/kawaweika/editcard.php?id=y2112&note=%CE%D2%C3%BB%D3%D0%BE%AA%CC%EC%B6%AF%B5%D8%B5%C4%B0%AE%C7%E9%D0%FB%D1%D4%A3%AC%D2%B2%C3%BB%D3%D0%BA%A3%BF%DD%CA%AF%C0%C3%B5%C4%B0%AE%C7%E9%B3%D0%C5%B5%A1%A3%B5%AB%CA%C7%CE%D2%CF%EB%B8%E6%CB%DF%C4%E3%A3%BA%CE%D2%B1%C8%C9%CF%D2%BB%C3%EB%B8%FC%B0%AE%C4%E3%A1%A3&vtype=&vid=","/editcard.html");
//		ka.ddd("http://kwvk.oicp.net/1heka/kawaweika/voice/voice.php?id=y2112&note=%C8%CB%D3%D0%CA%B1%BA%F2%B8%D0%B5%BD%BA%DC%C0%DB%A3%AC%C0%DB%B5%C4%C9%ED%D0%C4%C6%A3%B1%B9%A3%AC%C0%DB%B5%C4%D0%C4%C1%A6%BD%BB%B4%E1%A1%A3%B2%BB%CF%EB%BA%CD%C8%CE%BA%CE%C8%CB%CB%B5%BB%B0%A3%AC%C1%AC%C9%FA%C6%F8%BA%CD%BC%C6%BD%CF%B5%C4%C1%A6%C6%F8%B6%BC%C3%BB%D3%D0%C1%CB%A3%AC%D6%BB%CF%EB%D2%BB%B8%F6%C8%CB%BE%B2%BE%B2%B5%C4%B7%A2%B4%F4%A3%AC%D2%F2%CE%AA%A3%AC%D3%D0%CC%AB%B6%E0%B5%C4%CE%DE%C4%CE%A3%AC%CE%DE%B7%A8%CA%CD%BB%B3%7E&vtype=&vid=","/voice/voice.html");
//		ka.processMusic("http://kwvk.oicp.net/1heka/kawaweika/voice/voice.php?name=&id=y2112&note=%CD%BB%B3~&vid=2&vid2=0");
		ka.processCard("http://kwvk.oicp.net/1heka/kawaweika/y20022.php");
		//ka.processCatCard("http://kwvk.oicp.net/1heka/kawaweika/jieri.php");
		//ka.processKawaCard("http://weika5.kagirl.net/kawa2/show.php?cardid=30007&modify=yes&timecookie=201502172322&sharec=1");

	}
	
	public void processHeka(String baseUrl) throws Exception {
	    driver.get(baseUrl + "");
	    String indexPageContent = driver.getPageSource();
	    List<WebElement> catElements = driver.findElements(By.xpath("//div[@id='menu_list0']/a"));
	    System.out.println("分类：");
	    List<String> catLinks = new ArrayList<String>();
	    for(WebElement catElement:catElements) {
	    	//Object executeScript = ((JavascriptExecutor) driver).executeScript("return arguments[0].innerHTML;", catElement);
	    	String productLink= catElement.getAttribute("href");
	    	catLinks.add(productLink);
	    	//break;
	    }
	    for(String catLink:catLinks) {
	    	processCatCard(catLink);
	    }
	    String fileName = baseUrl.replace(kawaHost, "")
				.replace("php", "html");
	    indexPageContent = indexPageContent.replace(kawaHost, ".");
	    indexPageContent = indexPageContent.replace(".php", ".html");
	    indexPageContent = indexPageContent.replace("gb2312", "utf-8");
    	this.writeToFile(fileName,indexPageContent);
	}

	public void processCatCard(String catLink) throws Exception {
		System.out.println("分类："+catLink);
		driver.get(catLink + "");
		String pageContent = driver.getPageSource();
		List<WebElement> pageElements = driver.findElements(By.xpath("//div[@id='box']/a"));
		List<String> pageLinks = new ArrayList<String>();
		for(WebElement pageElement:pageElements) {
			//Object executeScript = ((JavascriptExecutor) driver).executeScript("return arguments[0].innerHTML;", catElement);
			String productLink= pageElement.getAttribute("href");
			pageLinks.add(productLink);
			//break;
		}
		for(String pageLink:pageLinks) {
			if(pageLink.contains("kawaweika/y")) {
				System.out.println("页面break："+pageLink);
				continue;
			}
			else {
				System.out.println("页面："+pageLink);
			}
			try {
				processCard(pageLink);
			} catch (Exception e) {
				System.out.println("页面："+pageLink+"		fail....");
				e.printStackTrace();
			}
			//break;
		}
		String fileName = catLink.replace(kawaHost, "")
				.replace("php", "html");
		pageContent = pageContent.replace(kawaHost, ".");
		pageContent = pageContent.replace(".php", ".html");
		pageContent = pageContent.replace("gb2312", "utf-8");
		this.writeToFile(fileName,pageContent);
	}

	public void ddd(String pageUrl, String fileName) throws Exception {
		driver.get(pageUrl);
		String pageSource = driver.getPageSource();
		pageSource = pageSource.replace("gb2312", "utf-8");
		System.out.println(pageSource);
		this.writeToFile(fileName, pageSource);
	}
	
	public void processMusic(String pageUrl) throws Exception {
		driver.get(pageUrl);
		List<WebElement> catElements = driver.findElements(By.xpath("//ul[@id='festival']/a"));
	    System.out.println("分类：");
	    List<String> catLinks = new ArrayList<String>();
	    for(WebElement catElement:catElements) {
	    	//Object executeScript = ((JavascriptExecutor) driver).executeScript("return arguments[0].innerHTML;", catElement);
	    	String productLink= catElement.getAttribute("option-id");
	    	catLinks.add(productLink);
	    	//break;
	    }
	    for(String catLink:catLinks) {
	    	String catUrl ="http://kwvk.oicp.net/1heka/kawaweika/voice/voice.php?name=&id=y2112&note=%CD%BB%B3~&vid=2&vid2="+catLink;
	    	driver.get(catUrl + "");
	    	this.writeToFile("/voice/"+catLink+".html", driver.findElement(By.xpath("//tbody[@id='tbodyid']")).getText());
	    	List<WebElement> pageElements = driver.findElements(By.xpath("//tbody[@id='tbodyid']/tr/td/a"));
	    	List<String> pageLinks = new ArrayList<String>();
	    	for(WebElement pageElement:pageElements) {
	    		String id = pageElement.getAttribute("id");
	        	if(id.contains("bf_")) {
	        		System.out.println("http://kwvk.oicp.net/1heka/media/voice/"+catLink+"/"+id.substring(3) +".mp3");
	        		this.saveImage("http://kwvk.oicp.net/1heka/media/voice/"+catLink+"/"+id.substring(3) +".mp3", "/voice/"+catLink+"/"+id.substring(3)+".mp3");
	        	}
	        }
	    }
	}

}
