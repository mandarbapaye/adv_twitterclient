package com.mb.twitterclient;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.net.Uri;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterRestClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
    public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
    public static final String REST_CONSUMER_KEY = "SXl7LFbD8vO4LiI0f3F4NFgkF";       // Change this
    public static final String REST_CONSUMER_SECRET = "YJM6mbv2TY0xMWNxgrbHc2MNcDGMD4OJ0ZSvtfGRNB9bPx3lHD"; // Change this
    public static final String REST_CALLBACK_URL = "oauth://mbtwitterrest"; // Change this (here and in manifest)
    
    public TwitterRestClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }
    
    public void getTimeline(long maxId, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl("statuses/home_timeline.json");
    	RequestParams params = new RequestParams();
    	params.put("count", "8");
    	
    	if (maxId > 0) {
    		params.put("max_id", String.valueOf(maxId));
    	}
    	
    	client.get(apiUrl, params, handler);
    }
    
    public void getNewerTweets(long sinceId, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl("statuses/home_timeline.json");
    	RequestParams params = new RequestParams();
    	params.put("since_id", String.valueOf(sinceId));
    	client.get(apiUrl, params, handler);
    }

    
    public void getTweetDetails(long tweetId, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl("statuses/show.json");
    	RequestParams params = new RequestParams();
    	params.put("id", String.valueOf(tweetId));
    	client.get(apiUrl, params, handler);
    }
    
    public void postNewTweet(String tweet, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl("statuses/update.json");
    	RequestParams params = new RequestParams();
    	params.put("status", tweet);
    	client.post(apiUrl, params, handler);
    }

    public void getMentionsTimeline(long maxId, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl("statuses/mentions_timeline.json");
    	RequestParams params = new RequestParams();
    	params.put("count", "8");
    	
    	if (maxId > 0) {
    		params.put("max_id", String.valueOf(maxId));
    	}
    	
    	client.get(apiUrl, params, handler);
    }
    
    public void getNewerMentionsTweets(long sinceId, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl("statuses/mentions_timeline.json");
    	RequestParams params = new RequestParams();
    	params.put("since_id", String.valueOf(sinceId));
    	client.get(apiUrl, params, handler);
    }
    
    public void getProfileInfo(long userId, AsyncHttpResponseHandler handler) {
    	if (userId <= 0) {
    		client.get(getApiUrl("account/verify_credentials.json"), handler);
    	} else {
    		String apiUrl = getApiUrl("users/show.json");
    		RequestParams params = new RequestParams();
    		params.put("user_id", String.valueOf(userId));
    		client.get(apiUrl, params, handler);
    		
    	}
    }
    
    public void getUserTweets(long userId, long maxId, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl("statuses/user_timeline.json");
    	RequestParams params = new RequestParams();
    	params.put("user_id", String.valueOf(userId));
    	
    	if (maxId > 0) {
    		params.put("max_id", String.valueOf(maxId));
    	}
    	
    	client.get(apiUrl, params, handler);
    }
    
    public void getNewerUserTweets(long userId, long sinceId, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl("statuses/user_timeline.json");
    	RequestParams params = new RequestParams();
    	params.put("user_id", String.valueOf(userId));
    	params.put("since_id", String.valueOf(sinceId));
    	client.get(apiUrl, params, handler);
    }
    
    public void toggleFavorite(boolean isFavorite, long tweetId, AsyncHttpResponseHandler handler) {
    	String apiUrl = isFavorite ? getApiUrl("favorites/create.json") : getApiUrl("favorites/destroy.json");
    	RequestParams params = new RequestParams();
    	params.put("id", String.valueOf(tweetId));
    	client.post(apiUrl, params, handler);
    }
    
    public void retweet(long tweetId, AsyncHttpResponseHandler handler) {
    	String apiUrl = getApiUrl("statuses/retweet/" + tweetId + ".json");
    	client.post(apiUrl, handler);
    }
    
}