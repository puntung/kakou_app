package com.sx.kakou.view;

import com.example.sx_kakou.R;
import com.google.gson.JsonObject;
import com.square.github.restrofit.Constants;
import com.square.github.restrofit.KakouClient;
import com.square.github.restrofit.ServiceGenerator;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends Activity implements OnClickListener{
	private static ImageView searchbackView;
	private ProgressDialog progressDialog = null;
	private static Button  search_btn;
	private static EditText et_q_hphm;
	private static TextView tv_q_csys;
	private static LinearLayout ly_csys;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		searchbackView = (ImageView)findViewById(R.id.search_back);
		search_btn = (Button)findViewById(R.id.search_btn);
		et_q_hphm = (EditText)findViewById(R.id.et_q_hphm);
		tv_q_csys = (TextView)findViewById(R.id.tv_q_hpys);
		ly_csys = (LinearLayout)findViewById(R.id.ly_hpys);
		searchbackView.setOnClickListener(this);
		search_btn.setOnClickListener(this);
		ly_csys.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.search_back:
			finish();
			break;
		case R.id.search_btn:
			progressDialog = ProgressDialog.show(this, null,"请稍等",true);
			progressDialog.setCancelable(true);
			String queryStr = et_q_hphm.getText()+"+hpys:"+tv_q_csys.getText();
			getSearchInfo(queryStr, null);
			break;
		case R.id.ly_hpys:
			AlertDialog.Builder dialog = new AlertDialog.Builder(SearchActivity.this);
			final String hpysdict[] = getResources().getStringArray(R.array.hpysdict);
			dialog.setItems(hpysdict,new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					tv_q_csys.setText(hpysdict[which]);
				}
			});
			dialog.show();
			break;
		}
	}
	
	public void getSearchInfo(String hp,String csys){
		KakouClient client = ServiceGenerator.createService(KakouClient.class,Constants.BASE_URL,"kakou","pingworker");
		client.getinfo(hp, new Callback<JsonObject>() {
			
			@Override
			public void success(JsonObject arg0, Response arg1) {
				// TODO Auto-generated method stub
				progressDialog.dismiss();
				startActivity(new Intent(SearchActivity.this,SearchResultActivity.class).putExtra("data", arg0.toString()));
				overridePendingTransition(R.anim.magnify_fade_in, R.anim.magnify_fade_out);
			}
			
			@Override
			public void failure(RetrofitError arg0) {
				// TODO Auto-generated method stub
				System.out.println("falure:"+arg0);
				progressDialog.dismiss();
				Toast.makeText(SearchActivity.this, "请重试", Toast.LENGTH_SHORT).show();
				arg0.printStackTrace();
			}
		});
	}
	
}
