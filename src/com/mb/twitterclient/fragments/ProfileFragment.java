package com.mb.twitterclient.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mb.twitterclient.R;
import com.mb.twitterclient.models.User;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProfileFragment extends Fragment {
	
	private User user;

	public static ProfileFragment newInstance(User user) {
		ProfileFragment profileFragment = new ProfileFragment();
		Bundle args = new Bundle();
		args.putSerializable("user", user);
		profileFragment.setArguments(args);
		return profileFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.user = (User) getArguments().get("user");
//		Log.d("debug", user.getName());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.fragment_profile_view, container, false);
		if (user != null) {
			ImageView ivProfileImage = (ImageView) v.findViewById(R.id.ivProfileImage);
			TextView tvProfileName = (TextView) v.findViewById(R.id.tvProfileName);
			TextView tvProfileTagline = (TextView) v.findViewById(R.id.tvProfileTagline);
			TextView tvProfileFollowerCount = (TextView) v.findViewById(R.id.tvProfileFollowerCount);
			TextView tvProfileFollowingCount = (TextView) v.findViewById(R.id.tvProfileFollowingCount);
			
			ivProfileImage.setImageResource(android.R.color.transparent);
			ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.displayImage(user.getProfileImageUrl(), ivProfileImage);
			
			tvProfileName.setText(user.getName());
			tvProfileTagline.setText(user.getTagLine());
			tvProfileFollowerCount.setText(String.valueOf(user.getFollowers()));
			tvProfileFollowingCount.setText(String.valueOf(user.getFollowing()));
		}

		return v;
	}
	
}
