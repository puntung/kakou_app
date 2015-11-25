package com.sx.kakou.view;


import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import com.example.sx_kakou.R;
import com.google.gson.JsonObject;
import com.square.github.restrofit.Constants;
import com.square.github.restrofit.KakouClient;
import com.square.github.restrofit.ServiceGenerator;
import com.sx.kakou.util.InitData;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

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
		}else{
			Login();
		}
	}
	
	public void Login(){
		progressDialog = ProgressDialog.show(this, null, "请稍等", true);
		progressDialog.setCancelable(true);
		JsonObject object = new JsonObject();
		object.addProperty("username", et_username.getText().toString().trim());
		object.addProperty("password", et_password.getText().toString().trim());
		KakouClient client = ServiceGenerator.createService(KakouClient.class, Constants.BASE_URL);
		client.login(object, new Callback<JsonObject>() {

			@Override
			public void success(JsonObject arg0, Response arg1) {
				System.out.println(arg0.toString());
				int user_id = Integer.parseInt(arg0.get("user_id").toString());
				int role_id = Integer.parseInt(arg0.get("role_id").toString());
				String user_name = arg0.get("username").toString();
				String role_name = arg0.get("rolename").toString();
				InitData.userInfo.setUserID(user_id);
				InitData.userInfo.setRoleID(role_id);
				InitData.userInfo.setUserName(user_name);
				InitData.userInfo.setRoleName(role_name);
				startActivity(new Intent(LoginActivity.this, MainActivity.class));
				progressDialog.dismiss();
			}

			@Override
			public void failure(RetrofitError arg0) {
				arg0.printStackTrace();
                try{
                    System.out.println("for--->"+arg0.getMessage());
                    if (arg0!=null){
                        String errStr = arg0.getMessage().toString();
                        switch (errStr){
                            case "401 Unauthorized":
                                Toast.makeText(LoginActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                                break;
                            case "401 IP not authorized":
                                Toast.makeText(LoginActivity.this, "IP限制访问", Toast.LENGTH_SHORT).show();
                                break;
                            case "403 Forbidden":
                                Toast.makeText(LoginActivity.this, "用户禁止访问", Toast.LENGTH_SHORT).show();
                                break;
                            case "failed to connect to /127.0.0.1 (port 8060): connect failed: ECONNREFUSED (Connection refused)":
                                Toast.makeText(LoginActivity.this, "请检查网络/安全客户端", Toast.LENGTH_SHORT).show();
                                break;
                            case "recvfrom failed: ECONNRESET (Connection reset by peer)":
								Toast.makeText(LoginActivity.this, "请检查网络/安全客户端", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(LoginActivity.this,"未知错误:"+errStr, Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                    progressDialog.dismiss();
                }catch (Exception e){
                    e.printStackTrace();
                }
			}
		});
	}
}
