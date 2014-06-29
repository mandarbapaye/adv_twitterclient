package com.mb.twitterclient.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.activeandroid.ActiveAndroid;
import com.mb.twitterclient.DetailViewActivity;
import com.mb.twitterclient.R;
import com.mb.twitterclient.TwitterApplication;
import com.mb.twitterclient.TwitterRestClient;
import com.mb.twitterclient.adapters.TweetAdapter;
import com.mb.twitterclient.models.Tweet;
import com.mb.twitterclient.util.Constants;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class TweetsListFragment extends Fragment {
	
	TwitterRestClient restClient;
	
	ArrayList<Tweet> tweetsList;
	TweetAdapter tweetsAdapter;
	PullToRefreshListView  lvTweets;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		restClient = TwitterApplication.getRestClient();
		tweetsList = new ArrayList<Tweet>();
		tweetsAdapter = new TweetAdapter(getActivity(), tweetsList);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		View fragmentTweetsView = inflater.inflate(R.layout.fragment_tweets_list, container, false);
		lvTweets = (PullToRefreshListView) fragmentTweetsView.findViewById(R.id.lvTweets);
		lvTweets.setAdapter(tweetsAdapter);
		setupHandlers();
		//loadTweets();
		return fragmentTweetsView;
	}
	
	private void setupHandlers() {
		lvTweets.setOnScrollListener(new com.mb.twitterclient.EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				loadTweets();
			}
		});
		
		lvTweets.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				loadNewerTweets();
			}
		});
		
		lvTweets.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Tweet tweet = tweetsAdapter.getItem(position);
				Intent detailIntent = new Intent(getActivity(), DetailViewActivity.class);
				detailIntent.putExtra(Constants.TWEET_PARAM, tweet);
				startActivity(detailIntent);
			}
		});
	}
	
	protected void loadNewerTweets() {
	}
	
	protected void loadTweets() {
	}
	
	protected void showProgressBar() {
		getActivity().setProgressBarIndeterminateVisibility(true);
	}
	
	protected void hideProgressBar() {
		getActivity().setProgressBarIndeterminateVisibility(false);
	}
	
	void saveToDatabase(ArrayList<Tweet> tweetsList) {
		new DBCommitTask().execute(tweetsList.toArray(new Tweet[0]));
	}
	
	List<Tweet> loadTweetsFromDatabase(long maxId) {
		return Tweet.getTweets(maxId);
	}
	
	private class DBCommitTask extends AsyncTask<Tweet, Void, Boolean> {
        @Override
	    protected Boolean doInBackground(Tweet... tweets) {
	    	boolean commitSuccessful = false;
	    	try {
	    		ActiveAndroid.beginTransaction();
	    		for (Tweet tweet : tweets) {
					tweet.getUser().save();
					tweet.save();
	    		}
	    		ActiveAndroid.setTransactionSuccessful();
	    		commitSuccessful = true;
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	} finally {
	    		ActiveAndroid.endTransaction();
	    	}
	 		
	    	return commitSuccessful;
	    }
	     
	    protected void onPostExecute(Boolean result) {
//	    	String text;
//	    	if (result) {
//	    		text = "Tweets added to databse from async task.";
//	    	} else {
//	    		text = "Could not add tweets to database";
//	    	}
//	    	Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
	    }
	}
	
}

