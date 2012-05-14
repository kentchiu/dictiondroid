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

import roboguice.activity.RoboListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.kentchiu.dictiondroid.R;
import com.kentchiu.dictiondroid.domain.Dictionary;
import com.kentchiu.dictiondroid.domain.DictionaryService;

public class DragNDropListActivity extends RoboListActivity {

	@Inject
	private DictionaryService	mService;

	private DropListener		mDropListener	= new DropListener() {
													@Override
													public void onDrop(int from, int to) {
														ListAdapter adapter = getListAdapter();
														if (adapter instanceof DragNDropAdapter) {
															((DragNDropAdapter) adapter).onDrop(from, to);
															getListView().invalidateViews();
														}
													}
												};

	private RemoveListener		mRemoveListener	= new RemoveListener() {
													@Override
													public void onRemove(int which) {
														ListAdapter adapter = getListAdapter();
														if (adapter instanceof DragNDropAdapter) {
															((DragNDropAdapter) adapter).onRemove(which);
															getListView().invalidateViews();
														}
													}
												};

	private DragListener		mDragListener	= new DragListener() {

													int	backgroundColor	= 0xe0103010;
													int	defaultBackgroundColor;

													@Override
													public void onDrag(int x, int y, ListView listView) {
														// TODO Auto-generated method stub
													}

													@Override
													public void onStartDrag(View itemView) {
														itemView.setVisibility(View.INVISIBLE);
														defaultBackgroundColor = itemView.getDrawingCacheBackgroundColor();
														itemView.setBackgroundColor(backgroundColor);
														ImageView iv = (ImageView) itemView.findViewById(R.id.ImageView01);
														if (iv != null) {
															iv.setVisibility(View.INVISIBLE);
														}
													}

													@Override
													public void onStopDrag(View itemView) {
														itemView.setVisibility(View.VISIBLE);
														itemView.setBackgroundColor(defaultBackgroundColor);
														ImageView iv = (ImageView) itemView.findViewById(R.id.ImageView01);
														if (iv != null) {
															iv.setVisibility(View.VISIBLE);
														}
													}

												};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dragndroplistview);
		setListAdapter(new DragNDropAdapter(DragNDropListActivity.this, new int[] { R.layout.dragitem }, mService));
		ListView listView = getListView();

		if (listView instanceof DragNDropListView) {
			((DragNDropListView) listView).setDropListener(mDropListener);
			((DragNDropListView) listView).setRemoveListener(mRemoveListener);
			((DragNDropListView) listView).setDragListener(mDragListener);
		}
	}

	@Override
	protected void onStop() {
		int count = getListAdapter().getCount();
		List<Dictionary> dicts = Lists.newArrayList();
		for (int i = 0; i < count; i++) {
			Dictionary item = (Dictionary) getListAdapter().getItem(i);
			dicts.add(item);
		}
		mService.save(dicts);
		super.onStop();
	}
}