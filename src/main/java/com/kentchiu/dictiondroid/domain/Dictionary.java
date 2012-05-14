package com.kentchiu.dictiondroid.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

public class Dictionary {
	public static Dictionary createDictionary(String line) {
		Dictionary dict = null;
		Pattern pattern = Pattern.compile("^(\\#)?\\s*?(\\w*),\\s*(.*)");
		Matcher m = pattern.matcher(line);
		if (m.find()) {
			Preconditions.checkState(m.groupCount() == 3, line + " is not well format");
			String name = m.group(2);
			String template = m.group(3);
			dict = new Dictionary(name, template);
			dict.setEnabled(m.group(1) == null);
		}
		return dict;
	}

	private boolean				mEnabled		= true;
	private String				mName;
	private String				mUrlTemplate;

	public final static String	templateToken	= "$$";

	public Dictionary() {
	}

	public Dictionary(String name, String urlTemplate) {
		super();
		mName = name;
		mUrlTemplate = urlTemplate;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Dictionary other = (Dictionary) obj;
		if (mName == null) {
			if (other.mName != null) {
				return false;
			}
		} else if (!mName.equals(other.mName)) {
			return false;
		}
		return true;
	}

	public String getName() {
		return mName;
	}

	public String getUrlTemplate() {
		return mUrlTemplate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (mName == null ? 0 : mName.hashCode());
		return result;
	}

	public boolean isEnabled() {
		return mEnabled;
	}

	public void setEnabled(boolean enabled) {
		mEnabled = enabled;
	}

	public void setName(String name) {
		mName = name;
	}

	public void setUrlTemplate(String urlTemplate) {
		mUrlTemplate = urlTemplate;
	}

	@Override
	public String toString() {
		if (mEnabled) {
			return String.format("%s, %s", mName, mUrlTemplate);
		} else {
			return String.format("# %s, %s", mName, mUrlTemplate);
		}
	}

	public String toUrl(String query) {
		return StringUtils.replace(getUrlTemplate(), "$$", query);
	}

}
