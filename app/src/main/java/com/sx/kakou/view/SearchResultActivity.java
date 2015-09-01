package com.sx.kakou.view;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.sx_kakou.R;
import com.sx.kakou.tricks.ResultListAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SearchResultActivity extends Activity implements OnClickListener{
	private static ImageView resultbackView;
	private static ListView	 resultView;
	private static TextView tv_count;
	private ResultListAdapter adapter = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchresult);
		resultbackView = (ImageView)findViewById(R.id.result_back);
		resultView = (ListView)findViewById(R.id.result_list);
		tv_count = (TextView)findViewById(R.id.tv_msgcount);
		String data = getIntent().getStringExtra("data");
		JSONObject json= null;
		try {
			json = new JSONObject(data);
			tv_count.setText("总共查询到"+json.getInt("total_count")+"条数据");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		adapter = new ResultListAdapter(this, json);
		resultbackView.setOnClickListener(this);
		resultView.setAdapter(adapter);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.result_back:
			finish();
			break;
		}
		
	}
}
