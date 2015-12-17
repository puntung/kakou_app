package com.sx.kakou.view;


import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import com.example.sx_kakou.R;
import com.google.gson.JsonObject;
import com.square.github.restrofit.Constants;
import com.square.github.restrofit.KakouClient;
import com.square.github.restrofit.ServiceGenerator;
import com.sx.kakou.util.Global;
import com.sx.kakou.util.UpdateVersion;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends Activity implements OnClickListener{
	private Button btn_login;
	private EditText et_username;
	private EditText et_password;
	private ProgressDialog progressDialog;
    private static SharedPreferences config_Preference;
	private CheckBox chkbox;
    private KakouClient client;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_login);
        config_Preference = getSharedPreferences("config", Context.MODE_PRIVATE);
        client = ServiceGenerator.createService(KakouClient.class, Constants.BASE_URL);
        //checkVersion(this);
        UpdateVersion uv = new UpdateVersion(this);
        uv.CheckVersion();
		btn_login = (Button)findViewById(R.id.login);
		et_username = (EditText)findViewById(R.id.account);
		et_password = (EditText)findViewById(R.id.password);
		chkbox = (CheckBox)findViewById(R.id.remind_chkb);
		btn_login.setOnClickListener(this);

        if (config_Preference.getBoolean("chkbox_state",false)){
            et_username.setText(config_Preference.getString("username", ""));
            chkbox.setChecked(true);
        }
	}

	@Override
	public void onClick(View v) {
		if (et_username.getText().toString().equals("") || et_password.getText().toString().equals("")) {
			Toast.makeText(this, "用户名或者密码不能为空", Toast.LENGTH_SHORT).show();
		}else{
			Login();
		}
	}
	
	public void Login(){
		progressDialog = ProgressDialog.show(this, null, "请稍等...", true);
		progressDialog.setCancelable(true);
		JsonObject object = new JsonObject();
		object.addProperty("username", et_username.getText().toString().trim());
		object.addProperty("password", et_password.getText().toString().trim());
		client.login(object, new Callback<JsonObject>() {

			@Override
			public void success(JsonObject arg0, Response arg1) {
				System.out.println(arg0.toString());
				int user_id = Integer.parseInt(arg0.get("user_id").toString());
				int role_id = Integer.parseInt(arg0.get("role_id").toString());
				String user_name = arg0.get("username").toString();
				String role_name = arg0.get("rolename").toString();
				Global.userInfo.setUserID(user_id);
				Global.userInfo.setRoleID(role_id);
				Global.userInfo.setUserName(user_name);
				Global.userInfo.setRoleName(role_name);
                if (chkbox.isChecked()){
                    SharedPreferences.Editor editor = config_Preference.edit();
                    editor.putBoolean("chkbox_state",true);
                    editor.putString("username",user_name.replace("\"",""));
                    editor.apply();
                }
				startActivity(new Intent(LoginActivity.this, MainActivity.class));
				progressDialog.dismiss();
			}

			@Override
			public void failure(RetrofitError arg0) {
				arg0.printStackTrace();
				try {
					System.out.println("for--->" + arg0.getMessage());
					if (arg0 != null) {
						String errStr = arg0.getMessage().toString();
						switch (errStr) {
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
								Toast.makeText(LoginActivity.this, "未知错误:" + errStr, Toast.LENGTH_SHORT).show();
								break;
						}
					}
					progressDialog.dismiss();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 检查版本更新,启动UpdateService更新，是另一种更新方式
	 */
//	public void checkVersion(final Context context) {
//        try {
//            //获取当前版本号
//            PackageManager packageManager = getPackageManager();
//            //getPackageName()是你当前类的包名，0代表是获取版本信息
//            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
//            final String localVersion = packInfo.versionName;
//            client.getVersion(new Callback<JsonObject>() {
//                @Override
//                public void success(JsonObject jsonObject, Response response) {
//                    String serverVersion = jsonObject.get("version").toString();
//                    final String url = jsonObject.get("file_url").toString().replace("\"", "");
//                    // 判断本地版本是否小于服务器端的版本号
//                    if (!localVersion.equals(serverVersion)) {
//                        // 发现新版本，提示用户更新
//                        AlertDialog.Builder alert = new AlertDialog.Builder(context);
//                        alert.setTitle("软件升级")
//                                .setMessage("发现新版本，建议您立即更新使用.")
//                                .setPositiveButton("更新",
//                                        new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog,
//                                                                int which) {
//                                                // 开启更新服务UpdateService
//                                                // 这里为了把update更好模块化，可以传一些updateService依赖的值
//                                                // 如布局ID，资源ID，动态获取的标题,这里以app_name为例
//                                                Intent updateIntent = new Intent(
//                                                        LoginActivity.this,
//                                                        UpdateService.class);
//                                                updateIntent.putExtra("titleId",
//                                                        R.string.app_name);
//                                                updateIntent.putExtra("downloadurl",url);
//                                                startService(updateIntent);
//                                            }
//                                        })
//                                .setNegativeButton("取消",
//                                        new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog,
//                                                                int which) {
//                                                dialog.dismiss();
//                                            }
//                                        });
//                        alert.create().show();
//                    } else {
//                        // 清理工作，略去
//                        cheanUpdateFile();
//                    }
//                }
//
//                @Override
//                public void failure(RetrofitError error) {
//                    error.printStackTrace();
//                }
//            });
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//	}
//
//	/**
//	 * 清理缓存的下载文件
//	 */
//	private void cheanUpdateFile() {
//		File updateFile = new File(Constants.downloadDir, getResources()
//				.getString(R.string.app_name) + ".apk");
//		if (updateFile.exists()) {
//			// 当不需要的时候，清除之前的下载文件，避免浪费用户空间
//			updateFile.delete();
//		}
//	}
}
