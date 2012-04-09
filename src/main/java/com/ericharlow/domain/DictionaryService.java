package com.ericharlow.domain;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import android.content.res.AssetManager;

import com.google.common.base.Preconditions;
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
		try {
			LineProcessor<List<Dictionary>> p = new LineProcessor<List<Dictionary>>() {
				private List<Dictionary>	results	= Lists.newArrayList();

				@Override
				public List<Dictionary> getResult() {
					return results;
				}

				@Override
				public boolean processLine(String line) throws IOException {
					Pattern pattern = Pattern.compile("^(\\#)?\\s*?(\\w*),\\s*(.*)");
					Matcher m = pattern.matcher(line);
					if (m.find()) {
						Preconditions.checkState(m.groupCount() == 3, line + " is not well format");
						String name = m.group(2);
						String template = m.group(3);
						Dictionary dict = new Dictionary(name, template);
						dict.setEnabled(m.group(1) == null);
						results.add(dict);
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
