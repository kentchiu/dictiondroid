package com.kentchiu.dictiondroid;

public class QueryChangeEvent {
	private String	query;

	public QueryChangeEvent() {
		super();
	}

	public QueryChangeEvent(String query) {
		super();
		setQuery(query);
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

}
