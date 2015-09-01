package com.sx.kakou.view;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import com.example.sx_kakou.R;
import com.google.gson.JsonObject;
import com.square.github.restrofit.Constants;
import com.square.github.restrofit.KakouClient;
import com.square.github.restrofit.ServiceGenerator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener{
	private Button btn_login;
	private EditText et_username;
	private EditText et_password;
	private ProgressDialog progressDialog = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_login);
		btn_login = (Button)findViewById(R.id.login);
		et_username = (EditText)findViewById(R.id.account);
		et_password = (EditText)findViewById(R.id.password);
		btn_login.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (et_username.getText().toString().equals("") || et_password.getText().toString().equals("")) {
			Toast.makeText(this, "用户名或者密码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		Login();
	}
	
	public void Login(){
		progressDialog = ProgressDialog.show(this, null,"请稍等",true);
		progressDialog.setCancelable(true);
		JsonObject object = new JsonObject();
		object.addProperty("username", et_username.getText().toString());
		object.addProperty("password", et_password.getText().toString());
		KakouClient client = ServiceGenerator.createService(KakouClient.class, Constants.BASE_URL);
		System.out.println(object.toString());
		client.login(object, new Callback<JsonObject>() {
			
			@Override
			public void success(JsonObject arg0, Response arg1) {
				int user_id = Integer.parseInt(arg0.get("user_id").toString());
				startActivity(new Intent(LoginActivity.this,MainActivity.class).putExtra("user_id",user_id));
				progressDialog.dismiss();
			}
			
			@Override
			public void failure(RetrofitError arg0) {
				System.out.println(arg0.getMessage());
				System.out.println(arg0.getBody());
				Toast.makeText(LoginActivity.this, "验证失败", Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
			}
		});
	}
}
