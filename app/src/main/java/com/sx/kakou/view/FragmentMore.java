package com.sx.kakou.view;

import com.example.sx_kakou.R;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentMore extends Fragment{
	private static SwipeRefreshLayout msgRefreshLayout;
	@SuppressLint("InlinedApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragmentmore, null);
		return view;
	}

	
}
