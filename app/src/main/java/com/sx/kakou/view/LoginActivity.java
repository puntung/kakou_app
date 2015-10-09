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
				arg0.printStackTrace();
                // 401 IP not authorize
                //403 服务  forbiden
				//ECONNREFUSED  //安全客户端没开
                // unauthrion 密码错误
                try{
                    System.out.println("for--->"+arg0.getMessage());
                    if (arg0!=null){
//						System.out.println(arg0.getBody().toString());
//                        JSONObject errobject = new JSONObject(arg0.getBody().toString());
//                        String errStr = errobject.getString("message");
                        String errStr = arg0.getMessage().toString();
                        switch (errStr){
                            case "401 Unauthorized":
                                Toast.makeText(LoginActivity.this, "账号或密码错误或限制访问", Toast.LENGTH_SHORT).show();
                                break;
                            case "401 IP not authorized":
                                Toast.makeText(LoginActivity.this, "IP限制访问", Toast.LENGTH_SHORT).show();
                                break;
                            case "403 Forbidden":
                                Toast.makeText(LoginActivity.this, "用户禁止访问", Toast.LENGTH_SHORT).show();
                                break;
                            case "403 ECONNREFUSED":
                                Toast.makeText(LoginActivity.this, "请检查安全客户端是否开启", Toast.LENGTH_SHORT).show();
                                break;
                            case "ETIMEDOUT":
                                Toast.makeText(LoginActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(LoginActivity.this,"其他错误:"+errStr, Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }else {
//                        System.out.println(arg0.getMessage());
//                        if (arg0.getMessage().contains("ECONNREFUSED")){
//                            Toast.makeText(LoginActivity.this, "请检查安全客户端是否开启", Toast.LENGTH_SHORT).show();
//                        }else if (arg0.getMessage().contains("ETIMEDOUT")){
//                            Toast.makeText(LoginActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
//                        }else {
//                            Toast.makeText(LoginActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
//                        }

                    }

                    progressDialog.dismiss();
                }catch (Exception e){
                    e.printStackTrace();
                }
			}
		});
	}
}
