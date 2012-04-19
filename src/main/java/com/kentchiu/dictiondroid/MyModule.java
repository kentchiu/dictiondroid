package com.kentchiu.dictiondroid;

import com.google.inject.AbstractModule;
import com.kentchiu.dictiondroid.domain.DictionaryService;
import com.kentchiu.dictiondroid.domain.IDictionaryService;

public class MyModule extends AbstractModule {

	@Override
	protected void configure() {
		binder().bind(IDictionaryService.class).to(DictionaryService.class);
	}

}
