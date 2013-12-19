package com.cmcc.search.domain;

import java.io.Serializable;

public class QAPair implements Serializable{
	private String query;
	private String answer;
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
}
