package com.mb.twitterclient.activities;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.mb.twitterclient.R;
import com.mb.twitterclient.TwitterApplication;
import com.mb.twitterclient.TwitterRestClient;
import com.mb.twitterclient.fragments.ComposeTweetFragment;
import com.mb.twitterclient.fragments.ComposeTweetFragment.OnTweetComposedListener;
import com.mb.twitterclient.fragments.HomeTimelineTweetsFragment;
import com.mb.twitterclient.fragments.MentionsTweetsFragment;
import com.mb.twitterclient.listeners.FragmentTabListener;
import com.mb.twitterclient.util.Util;

public class TimelineActivity extends FragmentActivity implements OnTweetComposedListener {
	
	TwitterRestClient restClient;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		restClient = TwitterApplication.getRestClient();
		setContentView(R.layout.activity_timeline);
		setupTabs();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.twitter_options_menu, menu);
		menu.findItem(R.id.miCompose).setVisible(Util.isNetworkConnected(this));
		return super.onCreateOptionsMenu(menu);
	}
	
	private void setupTabs() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);

        Tab tab1 = actionBar
            .newTab()
            .setText("Home")
            .setIcon(R.drawable.ic_home)
            .setTag("HomeTimelineTweetsFragment")
            .setTabListener(
                new FragmentTabListener<HomeTimelineTweetsFragment>(R.id.flTimelineFragmentContainer, this, "homeFrag", HomeTimelineTweetsFragment.class));

        actionBar.addTab(tab1);
        actionBar.selectTab(tab1);

        Tab tab2 = actionBar
            .newTab()
            .setText("Mentions")
            .setIcon(R.drawable.ic_mentions)
            .setTag("MentionsTweetsFragment")
            .setTabListener(
            		new FragmentTabListener<MentionsTweetsFragment>(R.id.flTimelineFragmentContainer, this, "homeFrag", MentionsTweetsFragment.class));
        
        actionBar.addTab(tab2);
    }

	
	public void onComposeClicked(MenuItem item) {
		ComposeTweetFragment composeTweetFragment = ComposeTweetFragment.newInstance();
		composeTweetFragment.show(getFragmentManager(), "ComposeTweet");
	}
	
	public void onTweetComposed(String tweetText) {
//		restClient.postNewTweet(tweetText, new JsonHttpResponseHandler() {
//			@Override
//			public void onSuccess(JSONObject tweetJson) {
//				// below approach may not be correct as some other
//				// twitter client might have added another tweet at
//				// the same time which is not reflected here.
//
//				tweetsAdapter.insert(Tweet.fromJSON(tweetJson), 0);
//				tweetsAdapter.notifyDataSetChanged();
//				lvTweets.smoothScrollToPosition(0);
//
//				// is this a better approach then.
////				tweetsAdapter.clear();
////				loadTweets();
//			}
//			
//			@Override
//			public void onFailure(Throwable e, String str) {
//				Log.d("error", e.getMessage());
//			}
//		});
	}
	
}
