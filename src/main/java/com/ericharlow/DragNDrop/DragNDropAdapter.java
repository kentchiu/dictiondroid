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

package com.ericharlow.DragNDrop;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.kentchiu.dictiondroid.Dictionary;
import com.kentchiu.dictiondroid.DictionaryService;
import com.kentchiu.dictiondroid.R;

public final class DragNDropAdapter extends BaseAdapter implements RemoveListener, DropListener {

	static class ViewHolder {
		TextView	text;
		CheckBox	checkBox;
	}

	private int[]				mLayouts;
	private LayoutInflater		mInflater;

	private List<Dictionary>	mContent;

	public DragNDropAdapter(Context context, int[] itemLayouts, DictionaryService service) {
		LinkedList<Dictionary> content = Lists.newLinkedList(service.allDictionaries());
		init(context, itemLayouts, content);
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
	public String getItem(int position) {
		return mContent.get(position).getName();
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
		// A ViewHolder keeps references to children views to avoid unneccessary calls
		// to findViewById() on each row.
		ViewHolder holder;

		// When convertView is not null, we can reuse it directly, there is no need
		// to reinflate it. We only inflate a new View when the convertView supplied
		// by ListView is null.
		if (convertView == null) {
			convertView = mInflater.inflate(mLayouts[0], null);

			// Creates a ViewHolder and store references to the two children views
			// we want to bind data to.
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.TextView01);
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.CheckBox01);
			convertView.setTag(holder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
		}

		// Bind the data efficiently with the holder.
		String name = mContent.get(position).getName();
		holder.text.setText(name);
		holder.checkBox.setChecked(name.startsWith("c") || name.startsWith("d"));
		return convertView;
	}

	private void init(Context context, int[] layouts, List<Dictionary> content) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		mInflater = LayoutInflater.from(context);
		mLayouts = layouts;
		mContent = content;
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
}