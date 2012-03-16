package com.kentchiu.dictiondroid;

import com.google.inject.AbstractModule;

public class MyModule extends AbstractModule {

	@Override
	protected void configure() {
		binder().bind(IDictionaryService.class).to(DictionaryService.class);
	}

}
