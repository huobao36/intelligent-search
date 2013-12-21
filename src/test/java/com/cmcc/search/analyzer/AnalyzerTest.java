package com.cmcc.search.analyzer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Test;

public class AnalyzerTest {

	private Iterable<CSVRecord> readFile() {
		Iterable<CSVRecord> parser = null;

		Reader in = null;
		try {
			in = new InputStreamReader(new FileInputStream(
					"data/ask_answer.csv"), "UTF-8");
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (in == null)
			return parser;

		try {
			parser = CSVFormat.EXCEL.parse(in);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		return parser;
	}

	public void printTokenAnalyzed(int type) {
		Iterable<CSVRecord> parser = readFile();
		if (parser == null)
			return;
		switch (type) {
		case AnalyzerFactory.ANALYZER_STANDARD:
			System.out.println("STAND ANALYZER START....");
			break;
		case AnalyzerFactory.ANALYZER_CJK:
			System.out.println("CJK ANALYZER START....");
			break;
		case AnalyzerFactory.ANALYZER_IK:
			System.out.println("IK ANALYZER START....");
			break;
		case AnalyzerFactory.ANALYZER_PAODING:
			System.out.println("PAODING ANALYZER START....");
			break;
		default:
			break;
		}
		Analyzer analyzer = AnalyzerFactory.getInstance().getAnalyzer(type);
	   

		for (CSVRecord record : parser) {

			String query = record.get(0);
			System.out.print("query: " + query + ", segments: ");
			try {
				TokenStream ts = analyzer.tokenStream("query",
						new StringReader(query));
				CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
				try {
					ts.reset(); // Resets this stream to the beginning.
								// (Required)
					while (ts.incrementToken()) {
						// Use AttributeSource.reflectAsString(boolean)
						// for token stream debugging.
						System.out
								.print(termAtt.toString() + " ");
					}
					ts.end(); // Perform end-of-stream operations, e.g. set the
								// final offset.
				} finally {
					ts.close(); // Release resources associated with this
								// stream.
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("\n");
		}
		System.out.println();
	}

	@Test
	public void testAnalyzer() {
		printTokenAnalyzed(AnalyzerFactory.ANALYZER_STANDARD);
		printTokenAnalyzed(AnalyzerFactory.ANALYZER_CJK);
		printTokenAnalyzed(AnalyzerFactory.ANALYZER_IK);
		printTokenAnalyzed(AnalyzerFactory.ANALYZER_PAODING);
	}
	
	
}
