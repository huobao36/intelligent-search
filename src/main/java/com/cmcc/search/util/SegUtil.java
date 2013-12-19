package com.cmcc.search.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// can be used for scanning large file
public class SegUtil {
	private Map<String, Long> wordsCount = new HashMap<String, Long>();
	private int topCount = 20;
	private List<SortWord> topEles = new LinkedList<SortWord>();

	public SegUtil(int topCount) {
		this.topCount = topCount;
	}

	public void setTopCount(int n) {
		topCount = n;
	}

	private SortWord findSortWord(String str) {
		for (SortWord word : topEles) {
			if (word.getRawWord().compareTo(str) == 0)
				return word;
		}
		return null;
	}

	public void put(String str) {
		long count = 0L;
		if (wordsCount.containsKey(str)) {
			count = wordsCount.get(str) + 1;
			wordsCount.put(str, count);
		} else {
			count = 1L;
			wordsCount.put(str, 1L);
		}
		SortWord curWord = findSortWord(str);
		if (curWord != null) {
			curWord.setCount(curWord.getCount() + 1);
		} else {
			curWord = new SortWord(str, count);
			if (topEles.size() < topCount) {
				topEles.add(curWord);
				Collections.sort(topEles);
			} else {
				SortWord minEle = topEles.get(0);
				if (count > minEle.getCount()) {
					topEles.add(curWord);
					topEles.remove(0);
					Collections.sort(topEles);
				}
			}

		}
	}

	public List<String> getTopFreqWords() {
		List<String> res = new ArrayList<String>();
		for (SortWord sortWord : topEles) {
			res.add(sortWord.rawWord);
		}
		Collections.reverse(res);
		return res;
	}

	public List<SortWord> getTopFreqSortWord() {
		List<SortWord> res = new ArrayList<SortWord>();
		for (SortWord sortWord : topEles) {
			res.add(sortWord);
		}
		Collections.reverse(res);
		return res;
	}
	
	public void clear() {
		wordsCount.clear();
		topEles.clear();
	}

	public class SortWord implements Comparable<SortWord> {
		private String rawWord;
		private long count;

		public SortWord(String rawWord, long count) {
			super();
			this.rawWord = rawWord;
			this.count = count;
		}

		@Override
		public int compareTo(SortWord o) {
			if (this.count > o.count)
				return 1;
			else if (this.count == o.count)
				return 0;
			else
				return -1;

		}

		public String getRawWord() {
			return rawWord;
		}

		public void setRawWord(String rawWord) {
			this.rawWord = rawWord;
		}

		public long getCount() {
			return count;
		}

		public void setCount(long count) {
			this.count = count;
		}
	}

}
