package com.cmcc.search.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;

public class HtmlUtil {
	private final static Logger logger = Logger.getLogger(HtmlUtil.class);
	
	public static String filterHtmlTags(String fileName)
			throws FileNotFoundException, IOException {
		String encoding = FileUtil.getFileEncoding(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(fileName), encoding));
		String line = null;
		StringBuffer sb = new StringBuffer();
		while((line = br.readLine())  != null ) {
			sb.append(line).append("\n");
		}
		return Jsoup.parse(sb.toString()).text();
	}

}
