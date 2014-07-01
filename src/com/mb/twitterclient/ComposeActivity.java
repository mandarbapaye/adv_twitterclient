package com.mb.twitterclient;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.util.Log;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mb.twitterclient.models.Tweet;
import com.mb.twitterclient.models.User;
import com.mb.twitterclient.util.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ComposeActivity extends Activity {
	
	TwitterRestClient restClient;
	
	ImageView ivNewTweetImage;
	TextView tvNewTweetName;
	TextView tvNewTweetScreenName;
	EditText etComposeTweet;
	TextView tvTweetCounter;
	Button btnNewTweet;
	
	String replyTo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose);
		
		ivNewTweetImage = (ImageView) findViewById(R.id.ivNewTweetImage);
		tvNewTweetName = (TextView) findViewById(R.id.tvNewTweetName);
		tvNewTweetScreenName = (TextView) findViewById(R.id.tvNewTweetScreenName);
		
		etComposeTweet = (EditText) findViewById(R.id.etComposeTweet);
		tvTweetCounter = (TextView) findViewById(R.id.tvTweetCounter);
		btnNewTweet = (Button) findViewById(R.id.btnNewTweet);
		
		Bundle intentParams = getIntent().getExtras();
		replyTo = intentParams != null ? intentParams.getString("replyTo") : null;
		
		User user = TwitterApplication.getAccountHolder();
		
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(user.getProfileImageUrl(), ivNewTweetImage);
		tvNewTweetName.setText(user.getName());
		tvNewTweetScreenName.setText("@" + user.getScreenName());
		
		restClient = TwitterApplication.getRestClient();
		
		setupHandlers();
		
		if (replyTo != null) {
			etComposeTweet.setText("@" + replyTo);
		}
	}
	
	private void setupHandlers() {
		etComposeTweet.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				tvTweetCounter.setText(String.valueOf(140 - etComposeTweet.getText().toString().length()));
			}
		});
	}

	public void sendTweet(View v) {
		String replyText = etComposeTweet.getText().toString();
		setProgressBarIndeterminateVisibility(true);
		restClient.postNewTweet(replyText, replyTo, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(JSONObject tweetJson) {
				Tweet newTweet = Tweet.fromJSON(tweetJson);
				Intent data = new Intent();
				data.putExtra(Constants.NEW_TWEET, newTweet);
				setResult(RESULT_OK, data);
			}
			
			@Override
			public void onFinish() {
				setProgressBarIndeterminateVisibility(false);
				super.onFinish();
				finish();
			}
		});
	}
}
