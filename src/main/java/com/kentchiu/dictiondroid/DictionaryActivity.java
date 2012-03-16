package com.kentchiu.dictiondroid;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectView;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.common.collect.Iterables;

public class DictionaryActivity extends Activity {
	private static final int	VOICE_RECOGNITION_REQUEST_CODE	= 1234;
	@InjectView(R.id.input)
	private EditText			mInput;
	@InjectView(R.id.mic)
	private ImageView			mSpeakButton;

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

				}
				return false;
			}
		});
		if (savedInstanceState != null) {
			//mDictView.query(savedInstanceState.getString("query"));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech or Spell a word");
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}
}