package com.ericharlow.domain;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.res.AssetManager;
import android.os.Bundle;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import com.google.common.io.LineProcessor;
import com.google.inject.Inject;

public class DictionaryService implements IDictionaryService {

	@Inject
	private AssetManager	manager;

	@Override
	public List<Dictionary> allDictionaries() {
//		ArrayList<Dictionary> results = Lists.newArrayList();
//		results.add(new Dictionary("urban", "http://m.urbandictionary.com/#define?term=$$"));
//		results.add(new Dictionary("tfd", "http://www.thefreedictionary.com/$$"));
//		results.add(new Dictionary("wikitionary", "http://en.wiktionary.org/wiki/$$"));
//		results.add(new Dictionary("longman", "http://www.ldoceonline.com/search/?q=$$"));
//		results.add(new Dictionary("dictionary", "http://m.dictionary.com/?submit-result-SEARCHD=Search&q=$$"));
//		results.add(new Dictionary("dreye", "http://www.dreye.com/mws/dict.php?project=nd&ua=dc_cont&w=$$&x=0&y=0"));
//		results.add(new Dictionary("cambridge", "http://dictionary.cambridge.org/spellcheck/british/?q=$$"));
//		results.add(new Dictionary("collins", "http://www.collinsdictionary.com/dictionary/english/$$"));
//		results.add(new Dictionary("etymonline", "http://www.etymonline.com/index.php?allowed_in_frame=0&search=$$&searchmode=none"));
//		results.add(new Dictionary("macmilland", "http://www.macmillandictionary.com/dictionary/british/$$"));
//		results.add(new Dictionary("oxford", "http://oald8.oxfordlearnersdictionaries.com/dictionary/$$"));
//		results.add(new Dictionary("webster", "http://www.learnersdictionary.com/search/$$"));
//		new Bundle();
//
//		return ImmutableList.copyOf(results);
		return load();
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

	public List<Dictionary> load() {
		try {

			LineProcessor<List<Dictionary>> p = new LineProcessor<List<Dictionary>>() {

				private List<Dictionary>	results	= Lists.newArrayList();

				@Override
				public List<Dictionary> getResult() {
					return results;
				}

				@Override
				public boolean processLine(String line) throws IOException {
					if (StringUtils.startsWith(line, "#")) {
						
					} else {
						String name = StringUtils.substringBefore(line, ",").trim();
						String template = StringUtils.substringAfter(line, ",").trim();
						results.add(new Dictionary(name, template));
					}
					return true;
				}

			};
			InputSupplier<InputStreamReader> s = new InputSupplier<InputStreamReader>() {

				@Override
				public InputStreamReader getInput() throws IOException {
					InputStream open = manager.open("config");
					return new InputStreamReader(open);
				}
			};
			List<Dictionary> readLines = CharStreams.readLines(s, p);
			return readLines;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ImmutableList.of();
	}
}
