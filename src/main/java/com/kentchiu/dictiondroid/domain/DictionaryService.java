package com.kentchiu.dictiondroid.domain;

import static android.content.Context.MODE_PRIVATE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import android.content.Context;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import com.google.common.io.LineProcessor;
import com.google.inject.Inject;

public class DictionaryService implements IDictionaryService {

	static final String	CONFIG_FILE_NAME	= "config";
	@Inject
	private Context		mContext;

	@Override
	public List<Dictionary> allDictionaries() {

		try {
			LineProcessor<List<Dictionary>> p = new LineProcessor<List<Dictionary>>() {
				private List<Dictionary>	results	= Lists.newArrayList();

				@Override
				public List<Dictionary> getResult() {
					return results;
				}

				@Override
				public boolean processLine(String line) throws IOException {
					Dictionary dict = Dictionary.createDictionary(line);
					if (dict != null) {
						results.add(dict);
					}
					return true;
				}

			};
			InputSupplier<InputStreamReader> s = new InputSupplier<InputStreamReader>() {
				@Override
				public InputStreamReader getInput() throws IOException {
					File filesDir = mContext.getFilesDir();
					File file = new File(filesDir, CONFIG_FILE_NAME);
					if (!file.exists()) {
						List<String> lines = Lists.newArrayList();
						lines.add(new Dictionary("urban", "http://m.urbandictionary.com/#define?term=$$").toString());
						lines.add(new Dictionary("tfd", "http://www.thefreedictionary.com/$$").toString());
						lines.add(new Dictionary("wikitionary", "http://en.wiktionary.org/wiki/$$").toString());
						lines.add(new Dictionary("longman", "http://www.ldoceonline.com/search/?q=$$").toString());
						lines.add(new Dictionary("dictionary", "http://m.dictionary.com/?submit-result-SEARCHD=Search&q=$$").toString());
						lines.add(new Dictionary("dreye", "http://www.dreye.com/mws/dict.php?project=nd&ua=dc_cont&w=$$&x=0&y=0").toString());
						lines.add(new Dictionary("cambridge", "http://dictionary.cambridge.org/spellcheck/british/?q=$$").toString());
						lines.add(new Dictionary("collins", "http://www.collinsdictionary.com/dictionary/english/$$").toString());
						lines.add(new Dictionary("etymonline", "http://www.etymonline.com/index.php?allowed_in_frame=0&search=$$&searchmode=none").toString());
						lines.add(new Dictionary("macmilland", "http://www.macmillandictionary.com/dictionary/british/$$").toString());
						lines.add(new Dictionary("oxford", "http://oald8.oxfordlearnersdictionaries.com/dictionary/$$").toString());
						lines.add(new Dictionary("webster", "http://www.learnersdictionary.com/search/$$").toString());
						IOUtils.writeLines(lines, null, new FileOutputStream(file));
					}
					FileInputStream fis = new FileInputStream(file);
					return new InputStreamReader(fis);
				}
			};
			return CharStreams.readLines(s, p);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ImmutableList.of();
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

	@Override
	public void save(List<Dictionary> dicts) {
		try {
			FileOutputStream fos = mContext.openFileOutput(CONFIG_FILE_NAME, MODE_PRIVATE);
			IOUtils.writeLines(dicts, CONFIG_FILE_NAME, fos);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
