package com.kentchiu.dictiondroid;

import java.io.File;

import android.os.Environment;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.kentchiu.dictiondroid.domain.DictionaryService;
import com.kentchiu.dictiondroid.domain.IDictionaryService;

public class MyModule extends AbstractModule {

	public File sdcard() {
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but all we need
			//  to know is we can neither read nor write
			mExternalStorageWriteable = false;
		}

		if (mExternalStorageWriteable) {
			File sdcardRoot = Environment.getExternalStorageDirectory();
			File dir = new File(sdcardRoot, "dictiondroid");
			if (!dir.exists()) {
				dir.mkdir();
			}
			return dir;
		} else {
			return null;
		}
	}

	@Override
	protected void configure() {
		bind(IDictionaryService.class).to(DictionaryService.class);
		if (sdcard() != null) {
			bind(File.class).annotatedWith(Names.named("sdcard")).toInstance(sdcard());
		}
	}
}
