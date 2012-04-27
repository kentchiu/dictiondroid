package com.kentchiu.dictiondroid;

public class QueryEvent {
	private String	query;

	public QueryEvent() {
		super();
	}

	public QueryEvent(String query) {
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
