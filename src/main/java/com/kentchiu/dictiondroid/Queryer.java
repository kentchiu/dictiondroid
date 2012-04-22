package com.kentchiu.dictiondroid;

import roboguice.RoboGuice;
import roboguice.event.EventManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.google.inject.Inject;

public class Queryer extends LinearLayout {

	@Inject
	private EventManager	mEventManager;

	public Queryer(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.queryer, this, true);
		RoboGuice.getInjector(context).injectMembers(this);
		//		TextView queryTextView = (TextView) findViewById(R.id.queryTextView); 
		//		queryTextView.setOnEditorActionListener(new OnEditorActionListener() {
		//			@Override
		//			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		//				mEventManager.fire(new QueryChangeEvent(v.getText().toString()));
		//				return false;
		//			}
		//		});
	}

}
