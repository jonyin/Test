package com.jyin.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.internal.Base64Encoder;

public class Duandizhi extends Base {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmdd");
	public static List<String> readFileByLines(String fileName) {
		File file = new File(fileName);
		List<String> lineList = new ArrayList<String>();
		for(File subFile:file.listFiles()) {
			if(subFile.isDirectory()){
				System.out.println(subFile.getName());
				lineList.add(fileName);
			}
		}
		
		return lineList;
	}
	
	private static void login(String url) {
		driver.get("http://dwz.aidmin.cn/");
		driver.findElement(By.id("longurl")).sendKeys(url);
		driver.findElement(By.name("gen_url")).click();
	}

	public static void main(String[] args) {
		List<String> testList = readFileByLines("C:/Users/Administrator/Documents/ceshiyouxi/qianduan/game/test");
		for(String test:testList) {
			String url = "http://h5.lazysa.com/game/test/?tid="+test+"--"+dateFormat.format(new Date());
			login(url);
		}
	}

}
