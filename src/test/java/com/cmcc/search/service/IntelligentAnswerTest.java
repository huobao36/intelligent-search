package com.cmcc.search.service;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.cmcc.search.analyzer.AnalyzerFactory;
import com.cmcc.search.domain.QAPair;
import com.cmcc.search.service.IntelligentAnswerService;
import com.cmcc.search.service.impl.IntelligentAnswerServiceImpl;
import com.cmcc.search.util.SegUtil;
import com.cmcc.search.util.SegUtil.SortWord;

public class IntelligentAnswerTest {

	private IntelligentAnswerServiceImpl intelligentAnswerService = new IntelligentAnswerServiceImpl();
	
	@Test
	public void testIndexQAndA() {
		intelligentAnswerService.initialize();
		List<QAPair> answers = intelligentAnswerService.searchAnswers("4G", 1, 10);
		for(QAPair answer : answers) {
			System.out.println(answer.getQuery());
			System.out.println(answer.getAnswer());
		}
	}
	
	@Test
	public void testDiffrentAnalyzers() {
		intelligentAnswerService.initialize();
		SegUtil nGramUtil = intelligentAnswerService.getNGramUtil();
		List<SortWord> nGrams = nGramUtil.getTopFreqSortWord();
		for(SortWord nGram : nGrams) {
			System.out.print( nGram.getRawWord() + ", " + nGram.getCount() + "; ");
		}
		System.out.println();
		List<QAPair> answers = intelligentAnswerService.searchAnswers("4G", 1, 10);
		for(QAPair answer : answers) {
			System.out.println(answer.getQuery());
			System.out.println(answer.getAnswer());
		}
		
		
//		intelligentAnswerService.buildIndexByAnalyzer(AnalyzerFactory.ANALYZER_PAODING);
//		nGrams = nGramUtil.getTopFreqSortWord();
//		for(SortWord nGram : nGrams) {
//			System.out.print( nGram.getRawWord() + ", " + nGram.getCount() + "; ");
//		}
//		System.out.println();
//		answers = intelligentAnswerService.searchAnswers("4G", 1, 10);
//		for(QAPair answer : answers) {
//			System.out.println(answer.getQuery());
//			System.out.println(answer.getAnswer());
//		}
	}
}
