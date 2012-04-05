package com.ericharlow.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.os.Bundle;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class DictionaryService implements IDictionaryService {
	@Override
	public List<Dictionary> allDictionaries() {
		ArrayList<Dictionary> results = Lists.newArrayList();
		results.add(new Dictionary("urban", "http://m.urbandictionary.com/#define?term=$$"));
		results.add(new Dictionary("tfd", "http://www.thefreedictionary.com/$$"));
		results.add(new Dictionary("wikitionary", "http://en.wiktionary.org/wiki/$$"));
		results.add(new Dictionary("longman", "http://www.ldoceonline.com/search/?q=$$"));
		results.add(new Dictionary("dictionary", "http://m.dictionary.com/?submit-result-SEARCHD=Search&q=$$"));
		results.add(new Dictionary("dreye", "http://www.dreye.com/mws/dict.php?project=nd&ua=dc_cont&w=$$&x=0&y=0"));
		results.add(new Dictionary("cambridge", "http://dictionary.cambridge.org/spellcheck/british/?q=$$"));
		results.add(new Dictionary("collins", "http://www.collinsdictionary.com/dictionary/english/$$"));
		results.add(new Dictionary("etymonline", "http://www.etymonline.com/index.php?allowed_in_frame=0&search=$$&searchmode=none"));
		results.add(new Dictionary("macmilland", "http://www.macmillandictionary.com/dictionary/british/$$"));
		results.add(new Dictionary("oxford", "http://oald8.oxfordlearnersdictionaries.com/dictionary/$$"));
		results.add(new Dictionary("webster", "http://www.learnersdictionary.com/search/$$"));
		Bundle bundle = new Bundle();
		
		return ImmutableList.copyOf(results);
	}

	@Override
	public Dictionary findByName(String name) {
		final String name2 = name;
		return Iterables.find(allDictionaries(), new Predicate<Dictionary>() {

			@Override
			public boolean apply(Dictionary input) {
				return StringUtils.equalsIgnoreCase(name2, input.getName());
			}

		});
	}
}
