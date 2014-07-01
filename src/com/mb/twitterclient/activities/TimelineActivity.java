package com.mb.twitterclient.activities;

import java.util.HashMap;
import java.util.Map;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.mb.twitterclient.ProfileActivity;
import com.mb.twitterclient.R;
import com.mb.twitterclient.TwitterApplication;
import com.mb.twitterclient.TwitterRestClient;
import com.mb.twitterclient.fragments.HomeTimelineTweetsFragment;
import com.mb.twitterclient.fragments.MentionsTweetsFragment;
import com.mb.twitterclient.fragments.TweetsListFragment;
import com.mb.twitterclient.listeners.FragmentTabListener;
import com.mb.twitterclient.util.Util;

public class TimelineActivity extends FragmentActivity {
	
	TwitterRestClient restClient;
	Tab homeTab, mentionsTab;
	Map<String, FragmentTabListener<? extends TweetsListFragment>> tabListeners; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 
		restClient = TwitterApplication.getRestClient();
		setContentView(R.layout.activity_timeline);
		
		tabListeners = new HashMap<String, FragmentTabListener<? extends TweetsListFragment>>();
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
        
        FragmentTabListener<HomeTimelineTweetsFragment> homeTabListener =
        		new FragmentTabListener<HomeTimelineTweetsFragment>(R.id.flTimelineFragmentContainer, this, "homeFrag", HomeTimelineTweetsFragment.class);
        
        FragmentTabListener<MentionsTweetsFragment> mentionsTabListener =
        		new FragmentTabListener<MentionsTweetsFragment>(R.id.flTimelineFragmentContainer, this, "mentionsFrag", MentionsTweetsFragment.class);
        

        Tab homeTab = actionBar
            .newTab()
            .setText("Home")
            .setIcon(R.drawable.ic_ab_home)
            .setTag("HomeTimelineTweetsFragment")
            .setTabListener(homeTabListener);

        actionBar.addTab(homeTab);
        actionBar.selectTab(homeTab);

        Tab mentionsTab = actionBar
            .newTab()
            .setText("Mentions")
            .setIcon(R.drawable.ic_ab_mentions)
            .setTag("MentionsTweetsFragment")
            .setTabListener(mentionsTabListener);
        
        actionBar.addTab(mentionsTab);
        tabListeners.put("home", homeTabListener);
        tabListeners.put("mentions", mentionsTabListener);
    }

	
	public void onComposeClicked() {
		HomeTimelineTweetsFragment homeFragment = (HomeTimelineTweetsFragment) tabListeners.get("home").getFragment();
		homeFragment.replyToTweet(null);
	}
	
	public void onProfileClicked() {
		Intent profileIntent = new Intent(this, ProfileActivity.class);
		profileIntent.putExtra("userId", -1);
		startActivityForResult(profileIntent, 20);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.miCompose:
				onComposeClicked();
				return true;
			case R.id.miProfile:
				onProfileClicked();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
}
