package com.mb.twitterclient;

import android.content.Context;

import com.mb.twitterclient.models.User;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest client.
 * 
 *     TwitterRestClient client = TwitterApplication.getRestClient();
 *     // use client to send requests to API
 *     
 */
public class TwitterApplication extends com.activeandroid.app.Application {
	private static Context context;
	private static User accountHolder;
	
    @Override
    public void onCreate() {
        super.onCreate();
        TwitterApplication.context = this;
        
        // Create global configuration and initialize ImageLoader with this configuration
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
        		cacheInMemory().cacheOnDisc().build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
            .defaultDisplayImageOptions(defaultOptions)
            .build();
        ImageLoader.getInstance().init(config);
    }
    
    public static TwitterRestClient getRestClient() {
    	return (TwitterRestClient) TwitterRestClient.getInstance(TwitterRestClient.class, TwitterApplication.context);
    }
    
    public static User getAccountHolder() {
    	return accountHolder;
    }
    
    public static void setAccountHolder(User user) {
    	accountHolder = user;
    }
}