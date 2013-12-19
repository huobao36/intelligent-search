package com.cmcc.search.web.controller;

import java.util.List;

import javax.annotation.Resource;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cmcc.search.analyzer.AnalyzerFactory;
import com.cmcc.search.domain.QAPair;
import com.cmcc.search.service.IntelligentAnswerService;
import com.cmcc.search.util.SegUtil.SortWord;


@Controller("cmcc.qa")
public class QAController {
	@Resource
	private IntelligentAnswerService intelligentAnswerService;
	
	private boolean dicIndexBuilt = false;
	private boolean normalIndexBuilt = false;
	
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String searchRelativeQA(Model model,
			@RequestParam(required = true, defaultValue = "") String key,
			@RequestParam(required = false, defaultValue = "1") int page,
			@RequestParam(required = false, defaultValue = "50") int size) {
		
		List<QAPair> qAnda = intelligentAnswerService.searchAnswers(key, page, size);
		model.addAttribute("key", key);
		model.addAttribute("page", page);
		model.addAttribute("size", size);
		model.addAttribute("query_answers", qAnda);
		return "search";
	}
	
	@RequestMapping(value = "/debug.html", method = RequestMethod.GET)
	public String debugQA(Model model,
			@RequestParam(required = true, defaultValue = "") String key,
			@RequestParam(required = false, defaultValue = "1") int page,
			@RequestParam(required = false, defaultValue = "50") int size) {
		if(!dicIndexBuilt) {
			intelligentAnswerService.buildIndexByAnalyzer(AnalyzerFactory.ANALYZER_IK, true);
			dicIndexBuilt = true;
		}
		List<SortWord> sortWords = intelligentAnswerService.getNGramUtil().getTopFreqSortWord();
		List<QAPair> qAnda = intelligentAnswerService.searchAnswers(key, page, size);
		
		model.addAttribute("key", key);
		model.addAttribute("page", page);
		model.addAttribute("size", size);
		model.addAttribute("query_answers", qAnda);
		model.addAttribute("sort_words", sortWords);
		return "debug";
	}
}
