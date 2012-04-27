package com.kentchiu.dictiondroid;

import roboguice.activity.RoboActivity;
import roboguice.event.Observes;
import roboguice.util.Ln;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.google.inject.Inject;
import com.kentchiu.dictiondroid.domain.Dictionary;
import com.kentchiu.dictiondroid.domain.DictionaryService;

public class DictionaryActivity extends RoboActivity {

	@Inject
	private DictionaryService	mDictionaryService;
	private String				mQuery;

	private void addTab(ActionBar actionBar, String text) {
		Tab tab = actionBar.newTab().setText(text).setTabListener(new DcitTabListener<DictFragment>(this, text, DictFragment.class));
		actionBar.addTab(tab);
	}

	public String getQuery() {
		return mQuery;
	};

	protected void handleQuery(@Observes QueryEvent event) {
		Ln.d("query changed : %s", event.getQuery());
		mQuery = event.getQuery();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupActionBar();
		if (mQuery == null) {
			mQuery = "welcome";
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	private void setupActionBar() {
		getActionBar().setHomeButtonEnabled(true);

		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);

		for (Dictionary each : mDictionaryService.allDictionaries()) {
			addTab(actionBar, each.getName());
		}
	}

}