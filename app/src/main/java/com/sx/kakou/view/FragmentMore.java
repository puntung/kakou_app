package com.sx.kakou.view;

import com.example.sx_kakou.R;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FragmentMore extends Fragment implements View.OnClickListener{
	private static LinearLayout photolayout;
	private static LinearLayout analyse;
	private static LinearLayout err_log_layout;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragmentmore, null);
		photolayout = (LinearLayout)view.findViewById(R.id.more_takephoto);
        analyse = (LinearLayout)view.findViewById(R.id.more_analyse);
		err_log_layout = (LinearLayout)view.findViewById(R.id.more_err_log);
		photolayout.setOnClickListener(this);
		analyse.setOnClickListener(this);
        err_log_layout.setOnClickListener(this);
		return view;
	}


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.more_takephoto:
                Intent intent = new Intent(getActivity(),CarControlActivity.class);
                intent.putExtra("user_id",getActivity().getIntent().getStringExtra("user_id"));
                startActivity(intent);
                break;
            case R.id.more_analyse:
                startActivity(new Intent(getActivity(),AnalyseActivity.class));
                break;
            case R.id.more_err_log:
                startActivity(new Intent(getActivity(),ErrorlistActivity.class));
                break;
        }
    }
}
