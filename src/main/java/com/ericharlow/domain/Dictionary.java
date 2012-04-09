package com.ericharlow.domain;

import org.apache.commons.lang3.StringUtils;

public class Dictionary {
	private boolean				enabled;
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
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setName(String name) {
		mName = name;
	}

	public void setUrlTemplate(String urlTemplate) {
		mUrlTemplate = urlTemplate;
	}

	@Override
	public String toString() {
		return "Dictionary [mName=" + mName + ", mUrlTemplate=" + mUrlTemplate + "]";
	}

	public String toUrl(String query) {
		return StringUtils.replace(getUrlTemplate(), "$$", query);
	}

}
