package com.mb.twitterclient.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mb.twitterclient.models.Tweet;
import com.mb.twitterclient.util.Util;

public class UserTweetsFragment extends TweetsListFragment {
	
	private long userId;
	
	public static UserTweetsFragment newInstance(long userId) {
		UserTweetsFragment fragment = new UserTweetsFragment();
		Bundle args = new Bundle();
		args.putLong("userId", userId);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.userId = getArguments().getLong("userId");
	}
	
	protected void loadTweets() {
		
		long maxId = tweetsAdapter.isEmpty() ? 0 : tweetsAdapter.getItem(tweetsAdapter.getCount() - 1).getTweetId();
		// Reduce the value so that only tweets older than the one loaded are received (otherwise we get dup for last tweet)
		maxId--;
		
//		if (maxId > 0)
//			Log.d("debug", "Load tweets older than: " + tweetsAdapter.getItem(tweetsAdapter.getCount() - 1).getBody() + ", id: " + maxId);
		
		if (!Util.isNetworkConnected(getActivity())) {
			Toast.makeText(getActivity(), "Loading from DB", Toast.LENGTH_SHORT).show();
			List<Tweet> tweetsList = loadTweetsFromDatabase(maxId);
			tweetsAdapter.addAll(tweetsList);
			tweetsAdapter.notifyDataSetChanged();
			return;
		}
		
		restClient.getUserTweets(this.userId, maxId, new JsonHttpResponseHandler() {
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
		});
	}
	
	protected void loadNewerTweets() {
		if (tweetsAdapter.isEmpty() || !Util.isNetworkConnected(getActivity())) {
			lvTweets.onRefreshComplete();
		} else {
			long sinceId = tweetsAdapter.getItem(0).getTweetId();
			restClient.getNewerUserTweets(this.userId, sinceId, new JsonHttpResponseHandler() {
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
				}
				
				@Override
				public void onFailure(Throwable e, String str) {
					Log.d("error", e.getMessage());
				}
				
				@Override
				public void onFinish() {
					lvTweets.onRefreshComplete();
					super.onFinish();
				}
			});

		}
	}

	
	
}
