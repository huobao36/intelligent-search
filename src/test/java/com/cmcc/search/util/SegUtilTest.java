package com.cmcc.search.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.junit.Test;

import com.cmcc.search.util.SegUtil.SortWord;

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
}
