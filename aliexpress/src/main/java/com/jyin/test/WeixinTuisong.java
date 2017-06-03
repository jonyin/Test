package com.jyin.test;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.browserlaunchers.Sleeper;

public class WeixinTuisong extends Base {

	private static void login(String userName, String password) {
		System.out.println("登陆信息：" + userName);
		driver.get("https://mp.weixin.qq.com/cgi-bin/loginpage");
		driver.findElement(By.id("account")).sendKeys(userName);
		driver.findElement(By.id("pwd")).sendKeys(password);
		driver.findElement(By.id("loginBt")).click();
	}

	private static void tuisong() {
		((JavascriptExecutor) driver)
		.executeScript("window.resizeTo(window.screen.avalWidth, window.screen.avalHeight);");
		waitForPageLoad();
		List<WebElement> sucaiGuanliElement = driver.findElements(By
				.xpath("//dd[@class='menu_item ']/a"));
		for (WebElement element : sucaiGuanliElement) {
			if ("群发功能".equalsIgnoreCase(element.getText())) {
				element.click();
				break;
			}
		}
		waitForPageLoad();
		driver.findElement(By.xpath("//li[@class='tab_nav tab_appmsg width5 no_extra']"))
		.click();
		sleep(3000);
		driver.findElements(By.xpath("//div[@class='appmsg multi']")).get(0)
		.click();
		driver.findElement(By.xpath("//button[@data-index='0']"))
		.click();
		waitForPageLoad();
		driver.findElement(By.xpath("//span[@id='js_submit']/button"))
		.click();
	}

	public static void main(String[] args) {
		List<String> accountList = readFileByLines(FILE_DIR + "account.txt");
		for (String account : accountList) {
			init();
			login(account.split("##")[0], account.split("##")[1]);
			tuisong();
		}
	}

}
