package com.kentchiu.dictiondroid;

import roboguice.RoboGuice;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class Queryer extends LinearLayout {
	public Queryer(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.queryer, this, true);
		RoboGuice.getInjector(context).injectMembers(this);
	}

}
