/*
 * Copyright (C) 2010 Eric Harlow
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ericharlow.dnd;

import java.util.List;

import roboguice.util.Ln;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.kentchiu.dictiondroid.R;
import com.kentchiu.dictiondroid.domain.Dictionary;
import com.kentchiu.dictiondroid.domain.IDictionaryService;

public final class DragNDropAdapter extends BaseAdapter implements RemoveListener, DropListener {

	static class ViewHolder {
		TextView	text;
		CheckBox	checkBox;
	}

	private int[]				mLayouts;
	private LayoutInflater		mInflater;
	private List<Dictionary>	mContent;

	public DragNDropAdapter(Context context, int[] itemLayouts, IDictionaryService service) {
		init(context, itemLayouts, Lists.newLinkedList(service.allDictionaries()));
	}

	public List<Dictionary> getContent() {
		return mContent;
	}

	/**
	 * The number of items in the list
	 * @see android.widget.ListAdapter#getCount()
	 */
	@Override
	public int getCount() {
		return mContent.size();
	}

	/**
	 * Since the data comes from an array, just returning the index is
	 * sufficient to get at the data. If we were using a more complex data
	 * structure, we would return whatever object represents one row in the
	 * list.
	 *
	 * @see android.widget.ListAdapter#getItem(int)
	 */
	@Override
	public Dictionary getItem(int position) {
		return mContent.get(position);
	}

	/**
	 * Use the array index as a unique id.
	 * @see android.widget.ListAdapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Make a view to hold each row.
	 *
	 * @see android.widget.ListAdapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		final int idx = position;
		if (convertView == null) {
			convertView = mInflater.inflate(mLayouts[0], null);

			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.TextView01);
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.CheckBox01);
			holder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					Dictionary dict = mContent.get(idx);
					dict.setEnabled(isChecked);
					if (dict.isEnabled()) {
						Ln.v("dict %s is enabled", dict.getName());
					} else {
						Ln.v("dict %s is disabled", dict.getName());
					}
				}
			});
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// Bind the data efficiently with the holder.
		Dictionary dict = mContent.get(position);
		String name = dict.getName();
		holder.text.setText(name);
		holder.checkBox.setChecked(dict.isEnabled());
		return convertView;
	}

	@Override
	public void onDrop(int from, int to) {
		Dictionary temp = mContent.get(from);
		mContent.remove(from);
		mContent.add(to, temp);
	}

	@Override
	public void onRemove(int which) {
		if (which < 0 || which > mContent.size()) {
			return;
		}
		mContent.remove(which);
	}

	private void init(Context context, int[] layouts, List<Dictionary> content) {
		mInflater = LayoutInflater.from(context);
		mLayouts = layouts;
		mContent = content;
	}
}