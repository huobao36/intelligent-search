package com.cmcc.search.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jsoup.Jsoup;
import org.junit.Test;

public class FileUtilTest {

	@Test
	public void encodeFile() throws IOException {
		String fileName = "/Users/cmcc/Documents/mobile/2013年9月CRM客服知识库访问日志.csv";
		String output = "/Users/cmcc/Documents/mobile/2013年9月CRM客服知识库访问日志.utf8.csv";
		String encoding = FileUtil.getFileEncoding(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(fileName), encoding));
		BufferedWriter bw = new BufferedWriter(new FileWriter(output));
		String line = null;
		while((line = br.readLine())  != null ) {
			bw.append(line);
			bw.newLine();
		}
		bw.close();
	}
}
