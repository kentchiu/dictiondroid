package com.ericharlow.domain;

import java.util.List;

public interface IDictionaryService {

	List<Dictionary> allDictionaries();

	Dictionary findByName(String name);

}
