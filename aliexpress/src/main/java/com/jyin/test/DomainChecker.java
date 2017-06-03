package com.jyin.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;

public class DomainChecker extends Base {
	public static Logger logger = Logger.getLogger(DomainChecker.class.getName());

	private static String currentDomain;

	private static void changeValue(String prefix, String key, String value) {
		driver.get("http://rd.lualugame.com/?edit&s=0&type=hash&key=admin%3A"+prefix+"&hkey="+key);
		driver.switchTo().frame("iframe");
		WebElement textValue = driver.findElement(By.id("value"));
		textValue.clear();
		textValue.sendKeys(value);		
		driver.findElement(By
				.xpath("//input[@value='Edit']")).click();
	}
	
	private static String getValue(String prefix, String key) {
		driver.get("http://rd.lualugame.com/?edit&s=0&type=hash&key=admin%3A"+prefix+"&hkey="+key);
		driver.switchTo().frame("iframe");
		return driver.findElement(By.id("value")).getText();
	}
	
	private static void updateJs(String prefix) {
		driver.get("http://backend.lualugame.com/genjs.php?hashkey="+prefix);
	}
	
	private static boolean isDomainStop(String domain) {
		driver.get("https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxcheckurl?requrl=http%3A%2F%2F"+domain+"&skey=%40crypt_71af21fb_41f7ebe88df6643d02b7cf1513185a48&deviceid=e845717709656693&pass_ticket=8DfcFwBELAMQ4ED%252FB92lqCE%252BKdNNVKqJ3PSLHCLl%252FbmLyNtD4qIbQy77lYXscZHL&opcode=2&scene=1&username=@dc2fad8956e6fb30d2b9f27c3e3404ae119dbddfa3e3b5f0d3c64c482000ec2db");
		boolean isWXLogin = Boolean.FALSE;
		for(Cookie cookie:driver.manage().getCookies()) {
			//logger.info(cookie.getDomain()+":"+cookie.getName()+":"+cookie.getPath()+":"+cookie.getValue()+":"+cookie.getExpiry());
			if("wxsid".equals(cookie.getName())){
				isWXLogin = Boolean.TRUE;
			}
		}
		return isWXLogin && StringUtils.isEmpty(driver.getTitle());
	}

	public static void main(String[] args) throws InterruptedException {
		currentDomain = getValue("wxhb_config","share_domain");
		long loopCount = 0L;
		while (true) {
			if(isDomainStop(currentDomain)) {
				String shareDomains=getValue("wxhb_config","shareDomains");
				String[] shareDomainArray = shareDomains.split("#");
				List<String> shareDomainList = new ArrayList<String>(Arrays.asList(shareDomainArray));
				currentDomain=shareDomainList.get(0)+".applinzi.com";
				changeValue("wxhb_config","share_domain",currentDomain);
				logger.info("更新share_domain："+currentDomain);
				Thread.sleep(5*1000L);
				shareDomainList.remove(0);
				String newShareDomains = StringUtils.join(shareDomainList, "#");
				changeValue("wxhb_config","shareDomains",newShareDomains);
				logger.info("更新shareDomains："+newShareDomains);
				Thread.sleep(5*1000L);
				updateJs("wxhb_config");
				logger.info("更新js：");
				Thread.sleep(5*1000L);
			}
			loopCount++;
			if(0==loopCount%60) {//跑60次重新去一次服务器数据
				currentDomain = getValue("wxhb_config","share_domain");
			}
			Thread.sleep(1*60*1000L);
		}
	}

}
