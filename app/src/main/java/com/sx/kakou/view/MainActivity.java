package com.sx.kakou.view;

import com.example.sx_kakou.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.square.github.restrofit.Constants;
import com.square.github.restrofit.KakouClient;
import com.square.github.restrofit.ServiceGenerator;
import com.sx.kakou.tricks.ControlService;
import com.sx.kakou.util.DataCleanManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends Activity implements OnCheckedChangeListener,View.OnClickListener{
    private KakouClient client ;
	private  FragmentManager mfragmentManager;
	private  RadioGroup mradioGroup;
	private  ImageView setup_view;
    private LayoutInflater inflater;
	private View view;
    private  TextView cache;
    private  TextView tv_appversion;
    private PopupWindow popupWindow;
    private ProgressDialog progressDialog = null;

    public static List<String> place_list,fxbh_list,hpys_list;
    public static List<Integer> place_code_list,fxbh_code_list,hpys_code_list;
    public static JsonArray RealTimeDate;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_main);
		init();
		initImageLoader(this);
		selectFragment(R.id.rb_history);
        mradioGroup.check(R.id.rb_history);
	}


    private void init(){
        progressDialog = ProgressDialog.show(this, null,"正在加载数据...",true);
        progressDialog.setCancelable(false);
		mfragmentManager = getFragmentManager();
		mradioGroup = (RadioGroup)findViewById(R.id.rg_tab);
		setup_view = (ImageView)findViewById(R.id.setup_v);
		mradioGroup.setOnCheckedChangeListener(this);
		setup_view.setOnClickListener(this);
        client = ServiceGenerator.createService(KakouClient.class, Constants.BASE_URL);
		//初始化数据，并保存在内存中
        place_list = new ArrayList<>();
        fxbh_list = new ArrayList<>();
        hpys_list = new ArrayList<>();
        place_code_list = new ArrayList<>();
        hpys_code_list = new ArrayList<>();
        fxbh_code_list = new ArrayList<>();
        RealTimeDate = new JsonArray();
        initPlace();
        initFxbh();
        initHpys();
        progressDialog.dismiss();
	}

    public void initPlace(){
        client.getPlace(new Callback<JsonObject>() {
            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
            }

            @Override
            public void success(JsonObject arg0, Response arg1) {
                try {
                    JSONArray array = new JSONArray(arg0.get("items").toString());
                    place_list.add("全部");
                    place_code_list.add(0);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = new JSONObject(array.get(i).toString());
                        place_list.add(object.getString("name"));
                        place_code_list.add(object.getInt("id"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }
    public void initHpys(){
        client.getHpys(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                try {
                    hpys_list.add("全部");
                    hpys_code_list.add(0);
                    JSONArray array = new JSONArray(jsonObject.get("items").toString());
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject mobject = new JSONObject(array.get(i).toString());
                        hpys_list.add(mobject.getString("name"));
                        hpys_code_list.add(mobject.getInt("id"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }

    public void initFxbh(){
        client.getFxhb(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                try {
                    fxbh_list.add("全部");
                    fxbh_code_list.add(0);
                    JSONArray array = new JSONArray(jsonObject.get("items").toString());
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = new JSONObject(array.get(i).toString());
                        if (!object.getString("name").equals("其他")) {
                            fxbh_list.add(object.getString("name"));
                            fxbh_code_list.add(object.getInt("id"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
                Toast.makeText(MainActivity.this, "抱歉！出现了异常，请与开发者联系", Toast.LENGTH_SHORT).show();
            }
        });
    }

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
			selectFragment(checkedId);
	}
	
	 public  void selectFragment(int index) {
		 FragmentTransaction transaction = mfragmentManager.beginTransaction();
	     Fragment fragment = new FragmentRealTime();
	     switch (index) {  
	         case R.id.rb_realtime:
	            fragment = new FragmentRealTime();  
	            break;  
	         case R.id.rb_history:
	            fragment = new FragmentHistory();
	            break;
	         case R.id.rb_more:
	        	 fragment = new FragmentMore();
	        	 break;
	        }  
	     	transaction.replace(R.id.content, fragment);
			transaction.commit();
	    }
	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.setup_v:
				showSetUpwindow(v);
				break;
            case R.id.setup_close:
                popupWindow.dismiss();
                break;
            case R.id.setup_about:
                inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.activity_login, null);

                break;
            case R.id.setup_update:
                Toast.makeText(this,"功能内测中",Toast.LENGTH_SHORT).show();
                break;
            case R.id.setup_cache:
                DataCleanManager.cleanInternalCache(MainActivity.this);
                try {
                    cache.setText(DataCleanManager.getCacheSize(getCacheDir()));
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case  R.id.setup_logout:
                showLogoutDialog();
                break;
		}
	}


    public void showLogoutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("要退出当前用户吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
				Intent intent = new Intent(MainActivity.this,ControlService.class);
				MainActivity.this.stopService(intent);
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


	public void showSetUpwindow(View parent){
        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.setuppopwin, null);
        ImageView close_img = (ImageView)view.findViewById(R.id.setup_close);
		LinearLayout aboutlayout = (LinearLayout)view.findViewById(R.id.setup_about);
        LinearLayout updatelayout = (LinearLayout)view.findViewById(R.id.setup_update);
        RelativeLayout cachelayout = (RelativeLayout)view.findViewById(R.id.setup_cache);
        LinearLayout logoutlayout = (LinearLayout)view.findViewById(R.id.setup_logout);
        cache = (TextView)view.findViewById(R.id.setup_size_cache);
        tv_appversion = (TextView)view.findViewById(R.id.setup_appversion);
        tv_appversion.setText(getVersion());
        try{
            cache.setText(DataCleanManager.getCacheSize(getCacheDir()));
        }catch (Exception e){
            e.printStackTrace();
        }
        close_img.setOnClickListener(this);
        aboutlayout.setOnClickListener(this);
        updatelayout.setOnClickListener(this);
        cachelayout.setOnClickListener(this);
        logoutlayout.setOnClickListener(this);
		popupWindow = new PopupWindow(view);
		popupWindow.setWidth(350);
		popupWindow.setHeight(500);
		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);
		// 这个是为了点击“返回Back”也能使其消失
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		backgroundAlpha(0.4f);
		popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				backgroundAlpha(1f);
			}
		});

		popupWindow.setAnimationStyle(R.style.PopupAnimation);
		popupWindow.showAtLocation(parent, Gravity.CENTER | Gravity.CLIP_HORIZONTAL, 0, 0);
		popupWindow.update();


	}


	/**
	 * 设置添加屏幕的背景透明度
	 * @param bgAlpha
	 */
	public void backgroundAlpha(float bgAlpha)
	{
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = bgAlpha; //0.0-1.0
		getWindow().setAttributes(lp);
	}

    public String getVersion(){
        try {
        PackageManager manager = this.getPackageManager();
        PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
        String version = info.versionName;
        return this.getString(R.string.app_name) + version;
        } catch (Exception e) {
        e.printStackTrace();
        return this.getString(R.string.app_version_not_found);
        }
    }

    public void initImageLoader(Context context) {
	        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
	        .threadPoolSize(3)
	        .threadPriority(Thread.NORM_PRIORITY - 2)
	        .denyCacheImageMultipleSizesInMemory()
	        .memoryCache(new LruMemoryCache(2* 1024 * 1024)) /// 设置内存缓存 默认为一个当前应用可用内存的1/8大小的LruMemoryCache
	        .memoryCacheSize(2 * 1024 * 1024)  //  设置内存缓存的最大大小 默认为一个当前应用可用内存的1/8
            .discCacheSize(50*1024*1024) //50MB sd卡缓存
	        .discCacheFileNameGenerator(new Md5FileNameGenerator())
            .discCache(new UnlimitedDiscCache(getCacheDir())) //自定义缓存路径
            .imageDownloader(new BaseImageDownloader(context,5*1000,30*1000)) //connecttime 5s,readTimeout 30s
	        .tasksProcessingOrder(QueueProcessingType.LIFO)
	       // .writeDebugLogs() // Remove                                                                                                                                                                                                                                                                                // app
	         .build();
	        ImageLoader.getInstance().init(config);  
	    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(MainActivity.this,ControlService.class);
		stopService(intent);

    }
}
