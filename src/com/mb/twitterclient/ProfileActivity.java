package com.mb.twitterclient;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mb.twitterclient.fragments.ProfileFragment;
import com.mb.twitterclient.fragments.UserTweetsFragment;
import com.mb.twitterclient.models.User;

public class ProfileActivity extends FragmentActivity {
	
	TwitterRestClient twitterClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_profile);
		 
		twitterClient = TwitterApplication.getRestClient();
		
		Bundle bundle = getIntent().getExtras();
		long userId = bundle.getLong("userId");
		getUserProfileInfo(userId);
	}
	
	private void getUserProfileInfo(long userId) {
		if (userId <= 0) {
			 loadFragments(TwitterApplication.getAccountHolder());
		} else {
			twitterClient.getProfileInfo(userId, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONObject profileJson) {
					loadFragments(User.fromJSON(profileJson));
				}
			});
		}
		
//		twitterClient.getProfileInfo(userId, new JsonHttpResponseHandler() {
//			@Override
//			public void onSuccess(JSONObject profileJson) {
//				User user = User.fromJSON(profileJson);
//				getActionBar().setTitle("@" + user.getScreenName());
//				
//				ProfileFragment profileFragment = ProfileFragment.newInstance(user);
//				FragmentTransaction frX = getSupportFragmentManager().beginTransaction();
//				frX.replace(R.id.flProfileFragmentContainer, profileFragment);
//				frX.commit();
//				
//				UserTweetsFragment userTweetsFragment = UserTweetsFragment.newInstance(user.getUserId());
//				frX = getSupportFragmentManager().beginTransaction();
//				frX.replace(R.id.flUserTweetsFragmentContainer, userTweetsFragment);
//				frX.commit();
//
//			}
//		});
	}
	
	private void loadFragments(User user) {
		getActionBar().setTitle("@" + user.getScreenName());
		
		ProfileFragment profileFragment = ProfileFragment.newInstance(user);
		FragmentTransaction frX = getSupportFragmentManager().beginTransaction();
		frX.replace(R.id.flProfileFragmentContainer, profileFragment);
		frX.commit();
		
		UserTweetsFragment userTweetsFragment = UserTweetsFragment.newInstance(user.getUserId());
		frX = getSupportFragmentManager().beginTransaction();
		frX.replace(R.id.flUserTweetsFragmentContainer, userTweetsFragment);
		frX.commit();
	}
	
}
