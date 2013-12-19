package com.cmcc.search.service;

import java.util.List;

import com.cmcc.search.domain.QAPair;
import com.cmcc.search.util.SegUtil;

public interface IntelligentAnswerService {
	public void initialize();
	public void buildIndexByAnalyzer(int analyzerType, boolean loaddic);
	public List<QAPair> searchAnswers(String keyWords, int page, int size);
	public void finalize();
	public SegUtil getNGramUtil();
}
