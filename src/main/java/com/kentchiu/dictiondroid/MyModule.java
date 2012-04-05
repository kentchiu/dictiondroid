package com.kentchiu.dictiondroid;

import com.ericharlow.domain.DictionaryService;
import com.ericharlow.domain.IDictionaryService;
import com.google.inject.AbstractModule;

public class MyModule extends AbstractModule {

	@Override
	protected void configure() {
		binder().bind(IDictionaryService.class).to(DictionaryService.class);
	}

}
