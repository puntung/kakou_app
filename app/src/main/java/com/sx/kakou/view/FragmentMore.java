package com.sx.kakou.view;

import com.example.sx_kakou.R;

import android.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FragmentMore extends Fragment implements View.OnClickListener{
	private static LinearLayout photolayout;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragmentmore, null);
		photolayout = (LinearLayout)view.findViewById(R.id.more_takephoto);
		photolayout.setOnClickListener(this);
		return view;
	}


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.more_takephoto:
                Toast.makeText(getActivity(),"该功能暂未上线",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
