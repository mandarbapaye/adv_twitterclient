package com.mb.twitterclient.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mb.twitterclient.ComposeActivity;
import com.mb.twitterclient.ProfileActivity;
import com.mb.twitterclient.R;
import com.mb.twitterclient.TwitterApplication;
import com.mb.twitterclient.fragments.ComposeTweetFragment;
import com.mb.twitterclient.models.Tweet;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetAdapter extends ArrayAdapter<Tweet> {

	public TweetAdapter(Context context, List<Tweet> tweets) {
		super(context, 0, tweets);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Tweet tweet = getItem(position);

		View v;
		if (convertView != null) {
			v = convertView;
		} else {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			v = inflator.inflate(R.layout.tweet_list_item, parent, false);
		}
		
		ImageView ivProfileImg = (ImageView) v.findViewById(R.id.ivProfileImg);
		TextView tvName = (TextView) v.findViewById(R.id.tvName);
		TextView tvScreenName = (TextView) v.findViewById(R.id.tvScreenName);
		TextView tvTweet = (TextView) v.findViewById(R.id.tvTweet);
		TextView tvTimeAgo = (TextView) v.findViewById(R.id.tvTimeAgo);
		final TextView tvTweetRetweet = (TextView) v.findViewById(R.id.tvTweetRetweet);
		final TextView tvTweetFavorite = (TextView) v.findViewById(R.id.tvTweetFavorite);
		final ImageView ivTweetFavorite = (ImageView) v.findViewById(R.id.ivTweetFavorite);
		final ImageView ivTweetRetweet = (ImageView) v.findViewById(R.id.ivTweetRetweet);
		ImageView ivTweetReply = (ImageView) v.findViewById(R.id.ivTweetReply);
				
		ivProfileImg.setImageResource(android.R.color.transparent);
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), ivProfileImg);
		tvTweet.setText(tweet.getBody());
		tvName.setText(tweet.getUser().getName());
		tvScreenName.setText("@" + tweet.getUser().getScreenName());
		tvTweetRetweet.setText(String.valueOf(tweet.getRetweetCount()));
		tvTweetFavorite.setText(String.valueOf(tweet.getFavCount()));
		tvTimeAgo.setText(getRelativeTimeAgo(tweet.getCreatedAt()));
		markFav(tweet.isFavorited(), ivTweetFavorite, tvTweetFavorite);
		markRetweet(tweet.isRetweeted(), ivTweetRetweet, tvTweetRetweet);
		
		ivProfileImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
				profileIntent.putExtra("userId", tweet.getUser().getUserId());
				getContext().startActivity(profileIntent);
			}
		});
		
		ivTweetFavorite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean favState = tweet.isFavorited();
				tweet.setFavorited(!favState);
				markFav(!favState, ivTweetFavorite, tvTweetFavorite);
				TwitterApplication.getRestClient().toggleFavorite(!favState, tweet.getTweetId(), new JsonHttpResponseHandler() {
				});
			}
		});
		
		ivTweetRetweet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean retweetState = tweet.isRetweeted();
				tweet.setRetweeted(!retweetState);
				markRetweet(!retweetState, ivTweetRetweet, tvTweetRetweet);
				if (tweet.isRetweeted()) {
					TwitterApplication.getRestClient().retweet(tweet.getTweetId(), new JsonHttpResponseHandler() {
					});
				}
			}
		});
		
		ivTweetReply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getContext(), ComposeActivity.class);
				i.putExtra("replyTo", tweet.getUser().getScreenName());
				getContext().startActivity(i);
			}
		});

		return v;
	}

	private void markFav(boolean isFavorited, ImageView ivTweetFavorite, TextView tvTweetFavorite) {
		if (isFavorited) {
			ivTweetFavorite.setImageResource(R.drawable.ic_tweet_favorited);
			tvTweetFavorite.setTextColor(getContext().getResources().getColor(R.color.favorite_orange));
		} else {
			ivTweetFavorite.setImageResource(R.drawable.ic_tweet_fav);
			tvTweetFavorite.setTextColor(getContext().getResources().getColor(android.R.color.darker_gray));
		}
	}
	
	private void markRetweet(boolean isRetweeted, ImageView ivTweetRetweet, TextView tvTweetRetweet) {
		if (isRetweeted) {
			ivTweetRetweet.setImageResource(R.drawable.ic_retweeted);
			tvTweetRetweet.setTextColor(getContext().getResources().getColor(R.color.retweet_green));
		} else {
			ivTweetRetweet.setImageResource(R.drawable.ic_tweet_retweet);
			tvTweetRetweet.setTextColor(getContext().getResources().getColor(android.R.color.darker_gray));
		}
	}	
	
	public String getRelativeTimeAgo(String rawJsonDate) {
		String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
		sf.setLenient(true);
	 
		long msFor7Days = 7 * 24 * 60 * 60 * 1000; 
		try {
			long dateMillis = sf.parse(rawJsonDate).getTime();
			long agoMs = System.currentTimeMillis() - dateMillis;
			if (agoMs > (msFor7Days - 1)) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
				return dateFormat.format(new Date(dateMillis));
			} else {
				String relativeDateFull = DateUtils.getRelativeTimeSpanString(dateMillis,
										 System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
				
				Scanner scanner = new Scanner(relativeDateFull);
				scanner.useDelimiter(" ");
				
				StringBuilder relativeDateShort = new StringBuilder();
				int count = 0;
				while (scanner.hasNext() && count < 2) {
					if (count == 0) {
						String firstPart = scanner.next();
						if (firstPart.equalsIgnoreCase("yesterday")) {
							relativeDateShort.append("1d");
						} else {
							relativeDateShort.append(firstPart);
						}
					} else {
						relativeDateShort.append(scanner.next().charAt(0));
					}
					count++;
				}
				
				return relativeDateShort.toString();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return "";
	}

	
}
