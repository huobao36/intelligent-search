package com.cmcc.search.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.csv.CSVRecord;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.springframework.stereotype.Service;

import com.cmcc.search.analyzer.AnalyzerFactory;
import com.cmcc.search.domain.QAPair;
import com.cmcc.search.service.IntelligentAnswerService;
import com.cmcc.search.util.CSVUtil;
import com.cmcc.search.util.SegUtil;
import com.cmcc.search.util.SegUtil.SortWord;

@Service
public class IntelligentAnswerServiceImpl implements IntelligentAnswerService {
	private final static String INDEX_QUESTION = "QUESTION";
	private final static String INDEX_ANSWER = "ANSWER";
	private final static String ASK_ANSWER_LIB_FN = "/data/deploy/cmcc-search/webapp/ask_answer.csv";
	private final static int TOP_N_COUNT = 50;
	private IndexSearcher indexSearcher = null;
//	private Directory indexDir = new RAMDirectory();
	private Analyzer analyzer = null;
	private SegUtil oneGramUtil = new SegUtil(TOP_N_COUNT);
	private SegUtil twoGramUtil = new SegUtil(TOP_N_COUNT);
	private SegUtil nGramUtil = new SegUtil(TOP_N_COUNT);

	private void OneOrTwoGram(String query, int n) {
		Analyzer analyzer = null;
		if (n == 1) {
			analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
		} else if (n == 2) {
			analyzer = new CJKAnalyzer(Version.LUCENE_CURRENT);
		}

		try {
			TokenStream ts = analyzer.tokenStream("query", new StringReader(
					query));
			CharTermAttribute termAtt = ts
					.addAttribute(CharTermAttribute.class);
			try {
				ts.reset(); // Resets this stream to the beginning.
							// (Required)
				while (ts.incrementToken()) {
					if (n == 1)
						oneGramUtil.put(termAtt.toString());
					else if (n == 2)
						twoGramUtil.put(termAtt.toString());
				}
				ts.end(); // Perform end-of-stream operations, e.g. set the
			} finally {
				ts.close(); // Release resources associated with this
							// stream.
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void extractByNGRAM(String query) {
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

	private void extractKeyWord() {
		CSVUtil util = new CSVUtil(ASK_ANSWER_LIB_FN);
		Iterable<CSVRecord> parser = util.openCSVFile();
		for (CSVRecord record : parser) {
			String query = record.get(0);
			extractByNGRAM(query);
		}
		util.closeCSVFile();
	}

	private void buildIndex(int type) {
		setAnalyzer(type);
		CSVUtil util = new CSVUtil(ASK_ANSWER_LIB_FN);
		Iterable<CSVRecord> parser = util.openCSVFile();
		IndexWriterConfig iwConfig = new IndexWriterConfig(Version.LUCENE_CURRENT,
				analyzer);
		iwConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
		
		IndexWriter iwriter = null;
		Directory dir = new RAMDirectory();
		try {
			iwriter = new IndexWriter(dir, iwConfig);
		} catch (CorruptIndexException e1) {
			e1.printStackTrace();
		} catch (LockObtainFailedException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (iwriter == null)
			return;

		long id = 1L;
		for (CSVRecord record : parser) {
			try {
				String query = record.get(0);
				String answer = record.get(1);
				Document doc = new Document();
//				doc.add(new LongField("ID", ++id, Store.YES));
//				doc.add(new StringField(INDEX_QUESTION, query, Store.YES));
//				doc.add(new StringField("ANSWER", answer, Store.YES));
				doc.add(new Field("ID", String.valueOf(++id), Field.Store.YES,
						Field.Index.NOT_ANALYZED));
				doc.add(new Field("QUESTION", query, Field.Store.YES,
						Field.Index.ANALYZED));
				doc.add(new Field("ANSWER", answer, Field.Store.YES,
						Field.Index.ANALYZED));
				iwriter.addDocument(doc);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		util.closeCSVFile();
		
		try {
			iwriter.close();
		} catch (CorruptIndexException e1) {
		} catch (IOException e1) {
		}
		
		try {
			IndexReader ireader = IndexReader.open(dir);
			indexSearcher = new IndexSearcher(ireader);
		} catch (CorruptIndexException e1) {
		} catch (IOException e1) {
		}
		System.out.println("intelligent answer initialize end.");
	}

	private void addTopNWordsToDic(int type) throws IOException {
		// only compatible with IK
		String path = IntelligentAnswerServiceImpl.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		System.out.println(path);
		File file = new File(path + "/ext.dic");
		if (!file.exists()) {
			file.createNewFile();
		}
		OutputStream out=new FileOutputStream(file);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));
		List<SortWord> nGrams = nGramUtil.getTopFreqSortWord();
		for(SortWord nGram : nGrams) {
			bw.write(nGram.getRawWord() + "\n");
		}
		bw.close();
	}
	
	private void eraseTopNWordsFromDic(int type) throws IOException {
		String path = IntelligentAnswerServiceImpl.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		File file = new File(path + "/ext.dic");
		if (!file.exists()) {
			file.createNewFile();
		}
		PrintWriter pw = new PrintWriter(file);
		pw.write("");
		pw.close();
	}
	
	@Override
	public void initialize() {
		extractKeyWord();						// extract key word from text
		try {
			addTopNWordsToDic(AnalyzerFactory.ANALYZER_IK);
		} catch (IOException e) {
			e.printStackTrace();
		}					// add top n to dictionary
		buildIndex(AnalyzerFactory.ANALYZER_IK);
	}

	@Override
	public void finalize() {

	}
	
	@Override
	public List<QAPair> searchAnswers(String keyWords, int page, int size) {
		List<QAPair> res = new LinkedList<QAPair>();
		if (indexSearcher == null)
			return res;
		QueryParser queryParser = new QueryParser(Version.LUCENE_CURRENT,
				INDEX_QUESTION, analyzer);
		try {
			Query query = queryParser.parse(keyWords);
			TopDocs topDocs = indexSearcher.search(query, page * size);
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			int start = (page - 1) * size;
			if (start < 0)
				start = 0;
			if (start >= topDocs.totalHits)
				return res;
			int end = (start + size) > topDocs.totalHits ? topDocs.totalHits
					: (start + size);
			for (int i = start; i < end; i++) {
				Document targetDoc = indexSearcher.doc(scoreDocs[i].doc);
				QAPair QAEntity = new QAPair();
				QAEntity.setAnswer(targetDoc.get(INDEX_ANSWER));
				QAEntity.setQuery(targetDoc.get(INDEX_QUESTION));
				res.add(QAEntity);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return res;
	}

	private void setAnalyzer(int type) {
		analyzer = AnalyzerFactory.getInstance().getAnalyzer(type);
	}

	public SegUtil getOneGramUtil() {
		return oneGramUtil;
	}

	public SegUtil getTwoGramUtil() {
		return twoGramUtil;
		
	}

	public SegUtil getNGramUtil() {
		return nGramUtil;
	}

	@Override
	public void buildIndexByAnalyzer(int analyzerType, boolean loaddic) {
		nGramUtil.clear();
		try {
			eraseTopNWordsFromDic(analyzerType);
			if(loaddic) {
				extractKeyWord();
				addTopNWordsToDic(analyzerType);
			}
		} catch (IOException e1) {
		}
		AnalyzerFactory.getInstance().removeAnalyzer(analyzerType);
		buildIndex(analyzerType);
	}

}
