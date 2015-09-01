package com.sx.kakou.tricks;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.sx_kakou.R;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ResultListAdapter extends BaseAdapter{
	private  Context mContext = null;
	private JSONObject json = null;
	private TextView tv_hpzl;
	private TextView tv_hphm;
	private TextView tv_clpp1;
	private TextView tv_clpp2;
	private TextView tv_clxh;
	private TextView tv_zzcmc;
	private TextView tv_clsbdh;
	private TextView tv_fdjh;
	private TextView tv_cllx;
	private TextView tv_csys;
	private TextView tv_syr;
	private TextView tv_fzrq;
	
	public ResultListAdapter(Context mContext, JSONObject json) {
		super();
		this.mContext = mContext;
		this.json = json;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int mcount=0;
		try {
			mcount = json.getInt("total_count");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mcount;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = LayoutInflater.from(mContext).inflate(R.layout.list_result, null);
		tv_hpzl = (TextView)convertView.findViewById(R.id.tv_hpzl);
		tv_hphm = (TextView)convertView.findViewById(R.id.tv_hphm);
		tv_clpp1 = (TextView)convertView.findViewById(R.id.tv_clpp1);
		tv_clpp2 = (TextView)convertView.findViewById(R.id.tv_clpp2);
		tv_clxh = (TextView)convertView.findViewById(R.id.tv_clxh);
		tv_zzcmc = (TextView)convertView.findViewById(R.id.tv_zzcmc);
		tv_clsbdh = (TextView)convertView.findViewById(R.id.tv_clsbdh);
		tv_fdjh = (TextView)convertView.findViewById(R.id.tv_fdjh);
		tv_cllx = (TextView)convertView.findViewById(R.id.tv_cllx);
		tv_csys = (TextView)convertView.findViewById(R.id.tv_csys);
		tv_syr = (TextView)convertView.findViewById(R.id.tv_syr);
		tv_fzrq = (TextView)convertView.findViewById(R.id.tv_fzrq);
		
		try {
			
			JSONArray array = json.getJSONArray("items");
			JSONObject object = array.getJSONObject(0);
			tv_hpzl.setText(object.getString("hpzl"));
			tv_hphm.setText(object.getString("hphm"));
			tv_clpp1.setText(object.getString("clpp1"));
			tv_clpp2.setText(object.getString("clpp2"));
			tv_clxh.setText(object.getString("clxh"));
			tv_zzcmc.setText(object.getString("zzcmc"));
			tv_clsbdh.setText(object.getString("clsbdh"));
			tv_fdjh.setText(object.getString("fdjh"));
			tv_cllx.setText(object.getString("cllx"));
			tv_csys.setText(object.getString("csys"));
			tv_syr.setText(object.getString("syr"));
			tv_fzrq.setText(object.getString("fzrq"));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return convertView;
	}

}
