package com.cmcc.search.analyzer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.paoding.analysis.analyzer.PaodingAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class AnalyzerFactory {
	private Map<Integer, Analyzer> analyzers = new ConcurrentHashMap<Integer, Analyzer>();
	public final static int ANALYZER_STANDARD = 0; // one tuple
	public final static int ANALYZER_CJK = 1; // two tuples
	public final static int ANALYZER_IK = 2; //
	public final static int ANALYZER_PAODING = 3;
	private static AnalyzerFactory instance = new AnalyzerFactory();
	
	public static AnalyzerFactory  getInstance() {
		return instance; 
	}
	
	public Analyzer getAnalyzer(int type) {
		Analyzer res = analyzers.get(type);
		if (res == null) {
			switch (type) {
			case ANALYZER_STANDARD: {
				res = new StandardAnalyzer(Version.LUCENE_45);
				analyzers.put(type, res);
			}
				break;
			case ANALYZER_CJK: {
				res = new CJKAnalyzer(Version.LUCENE_45);
				analyzers.put(type, res);
			}
				break;
			case ANALYZER_IK: {
				res = new IKAnalyzer(true);
				analyzers.put(type, res);
			}
				break;
			case ANALYZER_PAODING: {
				res = new PaodingAnalyzer();
				analyzers.put(type, res);
			}
				break;
			default:
				break;
			}
		}
		return res;
	}
	
	public void removeAnalyzer(int type) {
		if(analyzers.containsKey(type)) {
			analyzers.remove(type);
		}
	}
}
