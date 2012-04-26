package com.kentchiu.dictiondroid;

import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;

public class DcitTabListener<T extends Fragment> implements TabListener {
	private Fragment		mFragment;
	private final Activity	mActivity;
	private final String	mTag;
	private final Class<T>	mClass;

	/** Constructor used each time a new tab is created.
	  * @param activity  The host Activity, used to instantiate the fragment
	  * @param tag  The identifier tag for the fragment
	  * @param clz  The fragment's Class, used to instantiate the fragment
	  */
	public DcitTabListener(Activity activity, String tag, Class<T> clz) {
		mActivity = activity;
		mTag = tag;
		mClass = clz;
	}

	/* The following are each of the ActionBar.TabListener callbacks */

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// User selected the already selected tab. Usually do nothing.
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// Check if the fragment is already initialized
		if (mFragment == null) {
			// If not, instantiate and add it to the activity
			mFragment = Fragment.instantiate(mActivity, mClass.getName());
			ft.add(android.R.id.content, mFragment, mTag);
		} else {
			// If it exists, simply attach it in order to show it
			ft.attach(mFragment);
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		if (mFragment != null) {
			// Detach the fragment, because another one is being attached
			ft.detach(mFragment);
		}
	}
}