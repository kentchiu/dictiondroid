package com.kentchiu.dictiondroid;

import roboguice.RoboGuice;
import roboguice.util.Ln;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.google.inject.Inject;
import com.kentchiu.dictiondroid.domain.Dictionary;
import com.kentchiu.dictiondroid.domain.DictionaryService;

public class DictFragment extends Fragment {

	private WebView				mWebView;
	@Inject
	private DictionaryService	mDictionaryService;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		RoboGuice.getInjector(getActivity()).injectMembersWithoutViews(this);
		mWebView = new WebView(getActivity());
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setAppCacheEnabled(true);
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				Ln.v("page finished of %s", url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				Ln.v("page started of %s", url);
			}
		});

		String query = "hello";
		Dictionary dictionary = mDictionaryService.findByName(getTag());
		TextView textView = new TextView(getActivity());
		if (dictionary != null) {
			mWebView.loadUrl(dictionary.toUrl(query));
			textView.setText(dictionary.toUrl(query));
		}
		//return mWebView;
		return mWebView;
	}

}
