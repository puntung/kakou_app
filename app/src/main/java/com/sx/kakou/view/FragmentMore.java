package com.sx.kakou.view;

import com.example.sx_kakou.R;
import com.sx.kakou.model.UserInfo;
import com.sx.kakou.util.InitData;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class FragmentMore extends Fragment implements View.OnClickListener{
	private static LinearLayout photolayout;
	private static LinearLayout analyse;
	private static LinearLayout err_log_layout;
    private static TextView tv_username;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragmentmore, null);
		photolayout = (LinearLayout)view.findViewById(R.id.more_takephoto);
        analyse = (LinearLayout)view.findViewById(R.id.more_analyse);
        tv_username = (TextView)view.findViewById(R.id.fm_username);
		err_log_layout = (LinearLayout)view.findViewById(R.id.more_err_log);

        initData();
		photolayout.setOnClickListener(this);
		analyse.setOnClickListener(this);
        err_log_layout.setOnClickListener(this);
		return view;
	}

    public void initData(){
        UserInfo us = InitData.userInfo;
        tv_username.setText(us.getRoleName().replace("\"", ""));
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
