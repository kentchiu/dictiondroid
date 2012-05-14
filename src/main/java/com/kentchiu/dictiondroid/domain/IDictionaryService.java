package com.kentchiu.dictiondroid.domain;

import java.util.List;

public interface IDictionaryService {

	List<Dictionary> allDictionaries();

	Dictionary findByName(String name);

	void save(List<Dictionary> dicts);

}
