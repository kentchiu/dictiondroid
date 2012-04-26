package com.kentchiu.dictiondroid;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import roboguice.activity.RoboActivity;
import roboguice.util.Ln;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.kentchiu.dictiondroid.domain.Dictionary;
import com.kentchiu.dictiondroid.domain.DictionaryService;

public class DictionaryActivity extends RoboActivity {

	private static final int	VOICE_RECOGNITION_REQUEST_CODE	= 1234;
	@Inject
	private DictionaryService	mDictionaryService;
	private String				mQuery;

	private void addTab(ActionBar actionBar, String text) {
		Tab tab = actionBar.newTab().setText(text).setTabListener(new DcitTabListener<DictFragment>(this, text, DictFragment.class));
		actionBar.addTab(tab);
	};

	public String getQuery() {
		return mQuery;
	}

	/**
	 * Handle the results from the recognition activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
			// Fill the list view with the strings the recognizer thought it could have heard
			ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			String first = Iterables.getFirst(matches, "Not Found");
			String lowerCase = StringUtils.replace(first, " ", "").toLowerCase();
			mQuery = lowerCase;
			Ln.d("get [%s] from recognition", lowerCase);
			eventManager.fire(new QueryChangeEvent(mQuery));
		}
		super.onActivityResult(requestCode, resultCode, data);
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
		View queryer = menu.findItem(R.id.menu_search).getActionView();
		// make text response for query input
		TextView queryTextView = (TextView) queryer.findViewById(R.id.queryTextView);
		queryTextView.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				mQuery = v.getText().toString();
				eventManager.fire(new QueryChangeEvent(mQuery));
				return true;
			}
		});

		// enable voice recognition if supports
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		boolean supportRecognize = !activities.isEmpty();
		ImageView micImageView = (ImageView) queryer.findViewById(R.id.micImageView);
		if (supportRecognize) {
			micImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startVoiceRecognitionActivity();
				}
			});
		} else {
			micImageView.setVisibility(View.GONE);
		}

		//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		SpinnerAdapter mSpinnerAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, new String[] { "A", "B", "C" });

		getActionBar().setListNavigationCallbacks(mSpinnerAdapter, new OnNavigationListener() {

			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				return false;
			}
		});
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

	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech or Spell a word");
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

}