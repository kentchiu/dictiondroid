package com.kentchiu.dictiondroid;

import roboguice.activity.RoboActivity;
import roboguice.event.Observes;
import roboguice.util.Ln;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ericharlow.dnd.DragNDropListActivity;
import com.google.inject.Inject;
import com.kentchiu.dictiondroid.domain.Dictionary;
import com.kentchiu.dictiondroid.domain.IDictionaryService;

public class DictionaryActivity extends RoboActivity {

	@Inject
	private IDictionaryService	mDictionaryService;
	private String				mQuery;

	public String getQuery() {
		return mQuery;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupActionBar();
		if (mQuery == null) {
			mQuery = "welcome";
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Ln.i("featureId: %d", featureId);
		Ln.i("Menuitem : %s", item.getTitle());
		switch (item.getItemId()) {
		case R.id.menu_setting:
			Intent intent = new Intent(DictionaryActivity.this, DragNDropListActivity.class);
			startActivity(intent);
			return true;
		default:
			return false;
		}
	}

	private void addTab(ActionBar actionBar, String text) {
		Tab tab = actionBar.newTab().setText(text).setTabListener(new DcitTabListener<DictFragment>(this, text, DictFragment.class));
		actionBar.addTab(tab);
	}

	private void setupActionBar() {
		getActionBar().setHomeButtonEnabled(true);

		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);

		for (Dictionary each : mDictionaryService.allDictionaries()) {
			if (each.isEnabled()) {
				addTab(actionBar, each.getName());
			}
		}
	}

	protected void handleQuery(@Observes QueryEvent event) {
		Ln.d("query changed : %s", event.getQuery());
		mQuery = event.getQuery();
	}

}