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

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (mFragment == null) {
			mFragment = Fragment.instantiate(mActivity, mClass.getName());
			ft.add(android.R.id.content, mFragment, mTag);
		} else {
			ft.attach(mFragment);
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		if (mFragment != null) {
			ft.detach(mFragment);
		}
	}
}