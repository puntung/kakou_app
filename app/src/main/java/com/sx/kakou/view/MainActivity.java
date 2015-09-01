package com.sx.kakou.view;

import com.example.sx_kakou.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainActivity extends Activity implements OnCheckedChangeListener,View.OnClickListener{

	private static FragmentManager mfragmentManager;
	private static RadioGroup mradioGroup;
	private static ImageView setup_view;
	private View view;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_main);
		init();
		initImageLoader(this);
		selectFragment(1);
	}

	private void init(){
		mfragmentManager = getFragmentManager();
		mradioGroup = (RadioGroup)findViewById(R.id.rg_tab);
		setup_view = (ImageView)findViewById(R.id.setup_v);
		mradioGroup.setOnCheckedChangeListener(this);
		setup_view.setOnClickListener(this);
	}
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
			selectFragment(checkedId);
	}
	
	 public static void selectFragment(int index) {  
		 FragmentTransaction transaction = mfragmentManager.beginTransaction();
	     Fragment fragment = new FragmentRealTime();  
	     switch (index) {  
	         case 1:  
	            fragment = new FragmentRealTime();  
	            break;  
	         case 2:
	            fragment = new FragmentHistory(); 
	            break;
	         case 3:
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
		}
	}
	@SuppressLint("NewApi")
	public void showSetUpwindow(View parent){
		PopupWindow popupWindow;
		ImageLoader imageLoader = ImageLoader.getInstance();
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.setuppopwin, null);

		popupWindow = new PopupWindow(view);
		popupWindow.setWidth(parent.getDisplay().getWidth()*50/100);
		popupWindow.setHeight(parent.getDisplay().getHeight() *80/100);
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
	 
	 public void initImageLoader(Context context) {  
	        // This configuration tuning is custom. You can tune every option, you  
	        // may tune some of them,  
	        // or you can create default configuration by  
	        // ImageLoaderConfiguration.createDefault(this);  
	        // method.  
	    	
	        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
	        .threadPoolSize(3)
	        .threadPriority(Thread.NORM_PRIORITY - 2)
	        .denyCacheImageMultipleSizesInMemory()
	        .memoryCache(new LruMemoryCache(50 * 1024 * 1024))
	        .memoryCacheSize(50 * 1024 * 1024)
	        .discCacheFileNameGenerator(new Md5FileNameGenerator())
	        .tasksProcessingOrder(QueueProcessingType.LIFO)
	       // .writeDebugLogs() // Remove                                                                                                                                                                                                                                                                                // app  
	         .build();
	        ImageLoader.getInstance().init(config);  
	    }



}
