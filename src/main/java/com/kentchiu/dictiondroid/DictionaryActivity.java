package com.kentchiu.dictiondroid;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import roboguice.activity.RoboActivity;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
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
		Tab tab = actionBar.newTab().setText(text).setTabListener(new MyTabListener<DictFragment>(this, text, DictFragment.class));
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
			StringUtils.replace(first, " ", "").toLowerCase();
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
		// Inflate the options menu from XML
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		TextView queryTextView = (TextView) menu.findItem(R.id.menu_search).getActionView().findViewById(R.id.queryTextView);
		queryTextView.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				mQuery = v.getText().toString();
				eventManager.fire(new QueryChangeEvent(mQuery));
				return true;
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

		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		boolean supportRecongnize = !activities.isEmpty();
		if (supportRecongnize) {

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

	//	public void setQuery(String query) {
	//		mQuery = query;
	//		
	//	}

	//    @Override
	//    public boolean onCreateOptionsMenu(Menu menu) {
	//    	getMenuInflater().inflate(R.menu.menu_foobar, menu);
	//    	MenuItem findItem = menu.findItem(R.id.menu_search);
	//    	findItem.expandActionView();
	//    	
	//		View actionView = findItem.getActionView();
	//    	actionView.setOnClickListener(new OnClickListener() {
	//			
	//			public void onClick(View v) {
	//				Toast.makeText(DictionActivity2.this, "clicked", Toast.LENGTH_SHORT).show();
	//			}
	//		});
	//    	
	//    	
	//    	 ShareActionProvider shareActionProvider = (ShareActionProvider) menu.findItem(R.id.menu_share).getActionProvider();
	//
	//    	    // If you use more than one ShareActionProvider, each for a different action,
	//    	    // use the following line to specify a unique history file for each one.
	//    	    // mShareActionProvider.setShareHistoryFileName("custom_share_history.xml");
	//
	//    	    // Set the default share intent
	//    	 Intent intent = new Intent(Intent.ACTION_SEND);
	//    		intent.setType("text/plain");
	//    		intent.putExtra(Intent.EXTRA_TEXT, "Message");
	//
	//    	return true;
	//    }

}