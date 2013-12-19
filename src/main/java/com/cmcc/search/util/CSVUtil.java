package com.cmcc.search.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class CSVUtil {
	
	private Reader in = null;
	private String fileName;
	
	public CSVUtil(String fn) {
		this.fileName = fn;
	}
	
	public Iterable<CSVRecord> openCSVFile() {
		Iterable<CSVRecord> parser = null;
		try {
			in = new InputStreamReader(new FileInputStream(
					fileName), "UTF-8");
		} catch (FileNotFoundException e2) {
		} catch (UnsupportedEncodingException e) {
		}
		if (in == null)
			return parser;

		try {
			parser = CSVFormat.EXCEL.parse(in);
		} catch (IOException e2) {
		}
		return parser;
	}
	
	public void closeCSVFile() {
		if(in != null) {
			try {
				in.close();
			} catch (IOException e) {
			}
		}
	}
}
