package com.mb.twitterclient.fragments;

import java.util.ArrayList;

import org.json.JSONArray;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mb.twitterclient.models.Tweet;
import com.mb.twitterclient.util.Util;

public class MentionsTweetsFragment extends TweetsListFragment {

	protected void loadTweets() {
		
		long maxId = tweetsAdapter.isEmpty() ? 0 : tweetsAdapter.getItem(tweetsAdapter.getCount() - 1).getTweetId();
		// Reduce the value so that only tweets older than the one loaded are received (otherwise we get dup for last tweet)
		maxId--;
		
//		if (maxId > 0)
//			Log.d("debug", "Load tweets older than: " + tweetsAdapter.getItem(tweetsAdapter.getCount() - 1).getBody() + ", id: " + maxId);
		
		if (!Util.isNetworkConnected(getActivity())) {
//			Toast.makeText(getActivity(), "Loading from DB", Toast.LENGTH_SHORT).show();
//			List<Tweet> tweetsList = loadTweetsFromDatabase(maxId);
//			tweetsAdapter.addAll(tweetsList);
//			tweetsAdapter.notifyDataSetChanged();
			return;
		}
		
		showProgressBar();
		restClient.getMentionsTimeline(maxId, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray tweets) {
//				Log.d("debug", "*********** NEW RESULTS.");
//				for (int i = 0; i < tweets.length(); i++) {
//					try {
//						Log.d("debug", "tweet body: " + tweets.getJSONObject(i).getString("text"));
//					} catch (Exception e) {}
//				}
				
				ArrayList<Tweet> tweetsList = Tweet.fromJSONArray(tweets);
				tweetsAdapter.addAll(tweetsList);
				tweetsAdapter.notifyDataSetChanged();
				saveToDatabase(tweetsList);
			}
			
			@Override
			public void onFailure(Throwable e, String str) {
				Log.d("error", e.getMessage());
			}
			
			@Override
			public void onFinish() {
				hideProgressBar();
				super.onFinish();
			}
			
		});
	}
	
	protected void loadNewerTweets() {
		if (tweetsAdapter.isEmpty() || !Util.isNetworkConnected(getActivity())) {
			lvTweets.onRefreshComplete();
		} else {
			long sinceId = tweetsAdapter.getItem(0).getTweetId();
			showProgressBar();
			restClient.getNewerMentionsTweets(sinceId, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONArray tweets) {
					ArrayList<Tweet> tweetsList = Tweet.fromJSONArray(tweets);
					if (tweetsList.isEmpty()) {
						// this is so as to refresh the timestamps in case there
						// are not tweets.
						Tweet tempTweet = new Tweet();
						tweetsAdapter.add(tempTweet);
						tweetsAdapter.remove(tempTweet);
						tweetsAdapter.notifyDataSetChanged();
					} else {
						for (int i = tweetsList.size() - 1; i >= 0; i--) {
							tweetsAdapter.insert(tweetsList.get(i), 0);
						}
						tweetsAdapter.notifyDataSetChanged();
						saveToDatabase(tweetsList);
					}
					
//					lvTweets.onRefreshComplete();
				}
				
				@Override
				public void onFailure(Throwable e, String str) {
					Log.d("error", e.getMessage());
//					lvTweets.onRefreshComplete();
				}
				
				@Override
				public void onFinish() {
					hideProgressBar();
					lvTweets.onRefreshComplete();
					super.onFinish();
				}
			});

		}
	}

	
}
