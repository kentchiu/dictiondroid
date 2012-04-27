package com.kentchiu.dictiondroid;

import java.util.ArrayList;
import java.util.List;

import roboguice.RoboGuice;
import roboguice.activity.event.OnActivityResultEvent;
import roboguice.adapter.IterableAdapter;
import roboguice.event.EventManager;
import roboguice.event.Observes;
import roboguice.util.Ln;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

public class Queryer extends LinearLayout {
	static class Revise implements Function<String, String> {
		@Override
		public String apply(String input) {
			return CharMatcher.anyOf(" ").removeFrom(input).toLowerCase();
		}
	}

	private TextView			mQueryTextView;
	private Spinner				mSpinner;
	private ImageView			mMicImageView;
	private ImageView			mPenImageView;
	private static final int	VOICE_RECOGNITION_REQUEST_CODE	= 1234;
	private Activity			mActivity;
	@Inject
	private EventManager		mEventManager;

	public Queryer(final Context context) {
		super(context);
		mActivity = (Activity) context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.queryer, this, true);
		RoboGuice.getInjector(context).injectMembers(this);
		mQueryTextView = (TextView) findViewById(R.id.queryTextView);
		mMicImageView = (ImageView) findViewById(R.id.micImageView);
		mPenImageView = (ImageView) findViewById(R.id.penImageView);
		mSpinner = (Spinner) findViewById(R.id.spinner1);

		// enable voice recognition if supports
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		boolean supportRecognize = !activities.isEmpty();
		if (supportRecognize) {
			mMicImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mSpinner.setVisibility(VISIBLE);
					mQueryTextView.setVisibility(GONE);
					startVoiceRecognitionActivity(mActivity);
				}

			});
		} else {
			mMicImageView.setVisibility(View.GONE);
		}

		mPenImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mSpinner.setVisibility(GONE);
				mQueryTextView.setVisibility(VISIBLE);
			}

		});

		mSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String query = ((TextView) view).getText().toString();
				mEventManager.fire(new QueryEvent(query));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}

		});

		mQueryTextView.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				mEventManager.fire(new QueryEvent(v.getText().toString()));
				return true;
			}
		});

	}

	public void handleOnActivityResult(@Observes OnActivityResultEvent e) {
		onActivityResult(e.getRequestCode(), e.getResultCode(), e.getData());
	}

	protected void handleQuery(@Observes QueryEvent event) {
		String query = event.getQuery();
		Ln.d("query changed : %s", query);
		mQueryTextView.setText(query);
	}

	private void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			// Fill the list view with the strings the recognizer thought it could have heard
			ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			Iterable<String> words = Iterables.transform(matches, new Revise());
			String first = Iterables.getFirst(words, "Not Found");
			Ln.d("get [%s] from recognition", first);

			mEventManager.fire(new QueryEvent(first));
			IterableAdapter<String> adapter = new IterableAdapter<String>(mActivity, android.R.layout.simple_spinner_item, words);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mSpinner.setAdapter(adapter);
		}
	}

	private void startVoiceRecognitionActivity(Activity activity) {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech or Spell a word");
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
		activity.startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

}
