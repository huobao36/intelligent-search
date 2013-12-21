package com.cmcc.search.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.fnlp.app.keyword.AbstractExtractor;
import org.fnlp.app.keyword.WordExtract;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKTokenizer;

import com.cmcc.search.util.SegUtil.SortWord;

import edu.fudan.nlp.cn.tag.CWSTagger;
import edu.fudan.nlp.corpus.StopWords;
import edu.fudan.util.exception.LoadModelException;

public class SegUtilTest {
	private void extractByNGRAM(String query, SegUtil nGramUtil) {
		Reader reader = new StringReader(query);
		NGramTokenizer gramTokenizer = new NGramTokenizer(Version.LUCENE_CURRENT, reader, 2, 4);
		CharTermAttribute termAtt = gramTokenizer.addAttribute(CharTermAttribute.class);
		try {
			gramTokenizer.reset(); 
			while (gramTokenizer.incrementToken()) {
				nGramUtil.put(termAtt.toString());
			}
			gramTokenizer.end(); 
			gramTokenizer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	private void extractByIKTokenizer(String query, SegUtil ikTopNUtil) {
		Reader reader = new StringReader(query);
		IKTokenizer ikTokenizer = new IKTokenizer(reader, true);
		CharTermAttribute termAtt = ikTokenizer.addAttribute(CharTermAttribute.class);
		try {
			ikTokenizer.reset(); 
			while (ikTokenizer.incrementToken()) {
				ikTopNUtil.put(termAtt.toString());
			}
			ikTokenizer.end(); 
			ikTokenizer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	@Test
	public void testNGramUtil() throws IOException {
		SegUtil util = new SegUtil(150);
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream("knowledge.txt")));
		String line = null;
		while((line = br.readLine()) != null) {
			extractByNGRAM(line, util);
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter("NGRAM.txt"));
		List<SortWord> words = util.getTopFreqSortWord();
		for(SortWord word : words) {
			bw.append(word.getRawWord()).append('\t').append(String.valueOf(word.getCount()));
			bw.newLine();
		}
		bw.close();
	}
	
	@Test
	public void testIKAnalyzer() throws IOException {
		SegUtil util = new SegUtil(150);
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream("knowledge.txt")));
		String line = null;
		while((line = br.readLine()) != null) {
			extractByIKTokenizer(line, util);
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter("IKExtractor.txt"));
		List<SortWord> words = util.getTopFreqSortWord();
		for(SortWord word : words) {
			bw.append(word.getRawWord()).append('\t').append(String.valueOf(word.getCount()));
			bw.newLine();
		}
		bw.close();
	}
	
	@Test
	public void testFudanNlpExtractor() throws IOException, LoadModelException {
		List<String> keywords = new ArrayList<String>();
		StopWords sw = new StopWords("models/stopwords");
		CWSTagger seg = new CWSTagger("models/seg.m");
		AbstractExtractor key = new WordExtract(seg, sw);
		// you need to set the keywords number, here you will get 10 keywords
		final int KEY_WORDS_NUMBER = 150;
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream("knowledge.txt")));
		StringBuffer sb = new StringBuffer();
		String line = null;
		while((line = br.readLine()) != null) {
			sb.append(line);
		}
		Map<String, Integer> ans = key.extract(sb.toString(), KEY_WORDS_NUMBER);
		BufferedWriter bw = new BufferedWriter(new FileWriter("FudanNLPExtractor.txt"));
		for (Map.Entry<String, Integer> entry : ans.entrySet()) {
			String keymap = entry.getKey().toString();
			String value = entry.getValue().toString();
			keywords.add(keymap);
			bw.append(keymap).append('\t').append(value);
			bw.newLine();
		}
		bw.close();
	}
	
	@Test
	public void testFudanTokenizer() {
		 	
	}
}
