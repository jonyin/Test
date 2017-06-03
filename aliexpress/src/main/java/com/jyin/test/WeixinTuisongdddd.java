package com.jyin.test;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.browserlaunchers.Sleeper;

public class WeixinTuisongdddd extends Base {

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
			if ("用户管理".equalsIgnoreCase(element.getText())) {
				element.click();
				break;
			}
		}
		
		waitForPageLoad();
		
		driver.findElement(By.xpath("//a[@title='未分组']")).click();
		for(int i=0;i<3000;i++) {
			driver.findElement(By.className("icon_checkbox")).click();
			driver.findElement(By.className("jsBtLabel")).click();
			driver.findElement(By.xpath("//a[@data-name='1111']"))
			.click();
			sleep(1000);
			waitForPageLoad();
		}
	}

	public static void main(String[] args) {
			login("4784026@qq.com", "damnwho123");
			tuisong();
	}

}
