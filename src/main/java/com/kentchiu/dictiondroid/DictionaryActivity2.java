package com.kentchiu.dictiondroid;

import roboguice.activity.RoboActivity;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.os.Bundle;

import com.google.inject.Inject;
import com.kentchiu.dictiondroid.domain.Dictionary;
import com.kentchiu.dictiondroid.domain.DictionaryService;

public class DictionaryActivity2 extends RoboActivity {

	@Inject
	private DictionaryService	mDictionaryService;

	private void addTab(ActionBar actionBar, String text) {
		Tab tab = actionBar.newTab().setText(text).setTabListener(new MyTabListener<DictFragment>(this, text, DictFragment.class));
		actionBar.addTab(tab);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setHomeButtonEnabled(true);

		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);

		for (Dictionary each : mDictionaryService.allDictionaries()) {
			addTab(actionBar, each.getName());
		}
	}

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