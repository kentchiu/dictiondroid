package com.kentchiu.dictiondroid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;

public class DictionaryActivity extends RoboActivity {
	private static final int	VOICE_RECOGNITION_REQUEST_CODE	= 1234;
	private static final int	EDIT_ID							= 1;
	@InjectView(R.id.input)
	private EditText			mInput;
	@InjectView(R.id.mic)
	private ImageView			mSpeakButton;
	@InjectView(R.id.webview)
	private WebView				mWebView;
	@InjectView(R.id.progressbar)
	private ProgressBar			mProgressBar;
	@InjectView(R.id.scoll_menu_bar)
	private LinearLayout		mScollMenuBar;
	@Inject
	private InputMethodManager	mInputMethodManager;
	@Inject
	private IDictionaryService	mDictionaryService;
	private String				mCurrentDictName;
	private String				mQuery;

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, EDIT_ID, Menu.NONE, "Edit Prefs").setIcon(R.drawable.ic_launcher).setAlphabeticShortcut('e');
		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case EDIT_ID:
			startActivity(new Intent(this, SettingsActivity.class));
			return (true);
		}
		return (super.onOptionsItemSelected(item));
	}

	private void initScollMenuItem() {
		for (Dictionary each : mDictionaryService.allDictionaries()) {
			ImageButton icon = (ImageButton) getLayoutInflater().inflate(R.layout.scoll_menu_item, mScollMenuBar, false);
			try {
				Bitmap bm = BitmapFactory.decodeStream(getAssets().open("favicon/" + each.getName() + ".ico"));
				icon.setImageBitmap(bm);
			} catch (IOException e) {
				Ln.i("coluldn't find favicon of diction [%s]", each.getName());
				try {
					Bitmap bm = BitmapFactory.decodeStream(getAssets().open("favicon/default.ico"));
					icon.setImageBitmap(bm);
				} catch (IOException e1) {
					Ln.e(e1, "coluldn't find default favicon");
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
			icon.setTag(each);
			icon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Dictionary dict = (Dictionary) v.getTag();
					query(dict, mInput.getText().toString());
				}

			});
			mScollMenuBar.addView(icon);
		}
	}

	private void initVoiceRecognize() {
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() != 0) {
			mSpeakButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startVoiceRecognitionActivity();
				}
			});
		} else {
			mSpeakButton.setEnabled(false);
		}
		mInput.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (EditorInfo.IME_ACTION_SEND == actionId) {
					query(mDictionaryService.findByName(mCurrentDictName), mInput.getText().toString());
					mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}
				return true;
			}
		});
	}

	private void initWebView() {
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				mProgressBar.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				mProgressBar.setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
			// Fill the list view with the strings the recognizer thought it could have heard
			ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			String first = Iterables.getFirst(matches, "Not Found");
			mInput.setText(first);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dictionary);
		initVoiceRecognize();
		initScollMenuItem();
		initWebView();
		if (savedInstanceState == null) {
			Dictionary dict = Iterables.getFirst(mDictionaryService.allDictionaries(), null);
			query(dict, "welcome");
		} else {
			String dictName = savedInstanceState.getString("currentDictName");
			String query = savedInstanceState.getString("query");
			Dictionary dict = mDictionaryService.findByName(dictName);
			query(dict, query);
		}

		//Load the preference defaults 
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

		String name = settings.getString("namePref", "");
		boolean isMoreEnabled = settings.getBoolean("morePref", false);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("query", mQuery);
		outState.putString("currentDictName", mCurrentDictName);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	private void query(Dictionary dict, String query) {
		mCurrentDictName = dict.getName();
		mQuery = query;
		mInput.setText(query);
		mWebView.loadUrl(dict.toUrl(query));
	}

	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech or Spell a word");
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

}