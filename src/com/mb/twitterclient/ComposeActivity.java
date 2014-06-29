package com.mb.twitterclient;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mb.twitterclient.models.User;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ComposeActivity extends Activity {
	
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
		finish();
	}
}
