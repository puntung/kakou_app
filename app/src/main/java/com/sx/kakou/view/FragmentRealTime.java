package com.sx.kakou.view;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import com.example.sx_kakou.R;
import com.google.gson.JsonObject;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.square.github.restrofit.Constants;
import com.square.github.restrofit.KakouClient;
import com.square.github.restrofit.ServiceGenerator;
import com.sx.kakou.tricks.CarinfoAdapter;
import com.sx.kakou.tricks.CustomDrawerLayout;
import com.sx.kakou.tricks.RealTimeCarInfoAdapter;
import com.sx.kakou.tricks.SwitchButton;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class FragmentRealTime extends Fragment implements OnRefreshListener,OnScrollListener
		,OnItemClickListener,View.OnClickListener,SwitchButton.OnChangeListener{
	private static SwipeRefreshLayout homeRefreshLayout;
	private static ListView mCtxListView;
	private static TextView tv_place;
	private static TextView tv_fxbh;
	private static TextView tv_csys;
	private static SwitchButton sw_btn;
    private static Button mnav_btn;
    SharedPreferences mPreferences  = null;
	private RealTimeCarInfoAdapter adapter = null;
	private static View view;
    private  PopupWindow popupWindow;
    private CustomDrawerLayout mDrawerLayout;
    private ListView mNavListView = null;
    private static List<String> mlist = null;
	JSONArray jsonArray = null;
	private boolean isAutoRefresh = false;
	private Context mContext = null;
	private static int item_position = -1;
	private  int user_id= -1;
    private static  int mTag = 0;   //导航标签
	private static List nav_place = null;
    private static List nav_fxhb = null;
    private static List nav_csys = null;
	private List<String> mList = new ArrayList<String>();
	@SuppressLint("InlinedApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragmentrealtime, null);
        mPreferences = getActivity().getSharedPreferences("config",Context.MODE_PRIVATE);
		homeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.home_swip);
        mDrawerLayout = (CustomDrawerLayout)view.findViewById(R.id.drawer_layout);
        mNavListView = (ListView)view.findViewById(R.id.left_drawer_list);
        mCtxListView = (ListView)view.findViewById(R.id.con_list);
		tv_place = (TextView)view.findViewById(R.id.rt_tv_place);
		tv_fxbh = (TextView)view.findViewById(R.id.rt_tv_fxhb);
		tv_csys = (TextView)view.findViewById(R.id.rt_tv_csys);
        mnav_btn = (Button)view.findViewById(R.id.nav_completed_btn);
        nav_place = new ArrayList<>();
        nav_fxhb = new ArrayList<>();
        nav_fxhb = new ArrayList<>();
        tv_place.setOnClickListener(this);
        tv_fxbh.setOnClickListener(this);
        tv_csys.setOnClickListener(this);
        mnav_btn.setOnClickListener(this);
		sw_btn = (SwitchButton)view.findViewById(R.id.sw_autoRefresh);
		sw_btn.setOnChangeListener(this);
        mCtxListView.setOnItemClickListener(this);
        mNavListView.setOnItemClickListener(this);
		Intent intent = getActivity().getIntent();
		user_id = intent.getIntExtra("user_id",-1);
		homeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		homeRefreshLayout.setOnRefreshListener(this);
		return view;
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mContext = activity;
		}
	
	@Override
	public void onRefresh() {
		if (!isAutoRefresh){
            RefreshCarInfo(user_id);
		}
	}
	
	public void RefreshCarInfo(final int user_id){
			homeRefreshLayout.setRefreshing(true);
            int place =Integer.parseInt(mPreferences.getString("place","0").substring(0,1))+2;
            int fxbh = Integer.parseInt(mPreferences.getString("fxbh", "0").substring(0,1))+1;
			String queryString = user_id+"+place:"+place+"+fxbh:"+fxbh;
			KakouClient client = ServiceGenerator.createService(KakouClient.class, Constants.BASE_URL);
			client.getRefresh(queryString, new Callback<JsonObject>() {
                @Override
                public void failure(RetrofitError arg0) {
                    homeRefreshLayout.setRefreshing(false);
                    if (isAutoRefresh) {
                        RefreshCarInfo(user_id);
                    }
                }

                @SuppressLint("NewApi")
                @Override
                public void success(JsonObject arg0, Response arg1) {
                    homeRefreshLayout.setRefreshing(false);
                    try {
                        //只保留前100条数据
                        if (mList.size() > 99) {
                            mList = mList.subList(0, 50);
                        }
                        JSONArray mArray = new JSONArray(arg0.get("items").toString());
                        if (mArray.length() > 1) {
                            for (int i = 0; i < mArray.length(); i++) {
                                mList.add(0, mArray.get(i).toString());
                            }
                        } else if (mArray.length() != 0) {
                            mList.add(0, mArray.get(0).toString());
                        }

                        jsonArray = new JSONArray(mList.toString());
                        if (mContext != null) {
                            adapter = new RealTimeCarInfoAdapter(mContext, jsonArray);
                        } else {
                            adapter = new RealTimeCarInfoAdapter(getActivity(), jsonArray);
                        }
                        mCtxListView.setAdapter(adapter);
                        if (isAutoRefresh) {
                            RefreshCarInfo(user_id);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        homeRefreshLayout.setRefreshing(false);
                    }

                }

            });
	}
	
	public void getPlace(){
		KakouClient client = ServiceGenerator.createService(KakouClient.class, Constants.BASE_URL);
		client.getPlace(MyCallback);
	}

	public void getFxbh(){
		KakouClient client = ServiceGenerator.createService(KakouClient.class,Constants.BASE_URL);
		client.getFxhb(MyCallback);
	}

    Callback<JsonObject> MyCallback = new Callback<JsonObject>() {
        @Override
        public void success(JsonObject jsonObject, Response response) {
            try {
                List<HashMap<String,Object>> list = new ArrayList<HashMap<String, Object>>();
                JSONArray array = new JSONArray(jsonObject.get("items").toString());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = new JSONObject(array.get(i).toString());
                    HashMap<String,Object> map = new HashMap<String, Object>();
                    map.put("tv_name", object.get("name").toString());
                    map.put("img_value",R.drawable.navigation_term_check);
                    list.add(map);
                }
                SimpleAdapter adapter  = new SimpleAdapter(getActivity(),list,R.layout.list_pop_navitem
                        ,new String[]{"tv_name","img_value"},new int[]{R.id.tv_popnav_text,R.id.img_popnav_statu});
                mNavListView.setAdapter(adapter);
                switchDrawer(Gravity.START);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            retrofitError.printStackTrace();
        }
    };

	public void getCgsInfo(final RecyclerView sb_cys, String qs){
		KakouClient client = ServiceGenerator.createService(KakouClient.class,Constants.BASE_URL,"kakou","pingworker");
		client.getinfo(qs, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                String cgs_tag[] = mContext.getResources().getStringArray(R.array.cgs_label_en);
                String cgs_value[] = mContext.getResources().getStringArray(R.array.cgs_label_cn);
                try {
                    JSONObject cgsinfo = new JSONObject(jsonObject.toString());
                    if (cgsinfo.getInt("total_count") == 0) {
                        cgs_tag = null;
                    }
                    CarinfoAdapter cgsinfoAdapter = new CarinfoAdapter(getActivity(), cgsinfo, cgs_tag, cgs_value);
                    sb_cys.setAdapter(cgsinfoAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
            }
        });
	}

	public void showwindow(View parent,int position){
			ImageLoader imageLoader = ImageLoader.getInstance();
			LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.popwin_carinfo, null);
			ImageView car_img = (ImageView)view.findViewById(R.id.carinfo_img_hd);
			RecyclerView cgs_cys = (RecyclerView)view.findViewById(R.id.cgs_rcy);
			RecyclerView sb_cys = (RecyclerView)view.findViewById(R.id.sb_rcy);
			ImageView close_img = (ImageView)view.findViewById(R.id.pop_close);

			popupWindow = new PopupWindow(view);
			popupWindow.setWidth(parent.getDisplay().getWidth() * 90 / 100);
			popupWindow.setHeight(parent.getDisplay().getHeight() * 90 / 100);
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
            close_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
			popupWindow.setAnimationStyle(R.style.PopupAnimation);
	        popupWindow.showAtLocation(parent, Gravity.CENTER | Gravity.CLIP_HORIZONTAL, 0, 0);
	        popupWindow.update();
	        try {
	        JSONObject info = new JSONObject(jsonArray.getString(position));

	        String imgurl = info.getString("imgurl");
	        DisplayImageOptions options = getImageLoaderOpt();
				cgs_cys.setHasFixedSize(true);
				sb_cys.setHasFixedSize(true);
				RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
				RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(getActivity());
				cgs_cys.setLayoutManager(layoutManager);
				sb_cys.setLayoutManager(layoutManager2);
				getCgsInfo(sb_cys, info.getString("hphm"));

				String carinfo_tag[] = mContext.getResources().getStringArray(R.array.catinfo_label_en);
				String carinfo_value[] = mContext.getResources().getStringArray(R.array.catinfo_label_cn);
				CarinfoAdapter carinfoAdapter = new CarinfoAdapter(getActivity(),info,carinfo_tag,carinfo_value);
				cgs_cys.setAdapter(carinfoAdapter);
				imageLoader.displayImage(imgurl,car_img, options);
			} catch (Exception e) {
				e.printStackTrace();
			}

	}
	/**
	 * 设置添加屏幕的背景透明度
	 * @param bgAlpha
	 */
	public void backgroundAlpha(float bgAlpha)
	{
		WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
		lp.alpha = bgAlpha; //0.0-1.0
		getActivity().getWindow().setAttributes(lp);
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		switch (arg0.getId()){
            case R.id.con_list:
                item_position = position;
                if (popupWindow==null||!popupWindow.isShowing()){
                    showwindow(view, position);
                }
                break;
            case R.id.left_drawer_list:
                System.out.println(position);
                SelectedItem(position);
                break;

        }
	}

    public void SelectedItem(int position){
        ImageView imgstatus = (ImageView) mNavListView.getChildAt(position).findViewById(R.id.img_popnav_statu);
        if (imgstatus.getVisibility()==View.GONE){
            imgstatus.setVisibility(View.VISIBLE);
            switch (mTag){
                case 0:
                   if (!nav_place.contains(position)){nav_place.add(position+"");}
                    break;
                case 1:
                    if (!nav_fxhb.contains(position)){ nav_fxhb.add(position+"");}
                    break;
                case 2:
                    if (!nav_csys.contains(position)){ nav_csys.add(position+"");}
                    break;
            }
        }else{
            imgstatus.setVisibility(View.GONE);
            switch (mTag){
                case 0:
                    if (nav_place.contains(position)){ nav_place .remove(position + "");}
                    break;
                case 1:
                    if (nav_fxhb.contains(position)){ nav_fxhb .remove(position + "");}
                    break;
                case 2:
                    if (nav_csys.contains(position)){ nav_csys .remove(position + "");}
                    break;
            }
        }
    }
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
    }

    /*
     * 滚动不加载
     * */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		System.out.println("onscroll");
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_FLING:
			adapter.setFlagBusy(true);
			break;
		case OnScrollListener.SCROLL_STATE_IDLE:
			adapter.setFlagBusy(false);
			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			adapter.setFlagBusy(false);
			break;
		}
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.rt_tv_place:
                mTag = 0 ;
                int mp = Integer.parseInt(mPreferences.getString("place",-1+""));
                getPlace();
                break;
			case R.id.rt_tv_fxhb:
                mTag = 1 ;
                int mf = Integer.parseInt(mPreferences.getString("fxbh",-1+""));
                getFxbh();
				break;
			case R.id.rt_tv_csys:
                mTag = 2 ;
				System.out.println("csys");
				break;
            case R.id.nav_completed_btn:
                SharedPreferences.Editor editor = mPreferences.edit();
                if (nav_place.size()!=0){editor.putString("place", nav_place.get(0).toString());}
                if (nav_fxhb.size()!=0){editor.putString("fxbh", nav_fxhb.get(0).toString());}
                editor.commit();
                if (nav_place.size()!=0){nav_place.clear();}
                if (nav_fxhb.size()!=0){nav_fxhb.clear();}
                RefreshCarInfo(user_id);
                switchDrawer(Gravity.START);
                break;

		}
	}

    public void switchDrawer(int DrawerGrivity)
    {
        if (mDrawerLayout.isDrawerVisible(DrawerGrivity))
        {
            mDrawerLayout.closeDrawer(DrawerGrivity);
        }
        else
        {
            mDrawerLayout.openDrawer(DrawerGrivity);
        }
    }

	@Override
	public void onChange(SwitchButton sb, boolean state) {
		isAutoRefresh = state;
		Toast.makeText(getActivity(),state?"已开启自动更新功能":"已关闭自动更新功能",Toast.LENGTH_SHORT).show();
		if (isAutoRefresh){
            tv_place.setEnabled(false);
            tv_fxbh.setEnabled(false);
            tv_csys.setEnabled(false);
            RefreshCarInfo(user_id);
		}else {
            tv_place.setEnabled(true);
            tv_fxbh.setEnabled(true);
            tv_csys.setEnabled(true);
		}
	}
	/*
	 * Imageloader的配置
	 *
	 * */
	public DisplayImageOptions getImageLoaderOpt(){
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.board_gray) //加载时显示的页面
		.showImageForEmptyUri(R.drawable.board_gray)
		.showImageOnFail(R.drawable.board_gray)
		 .delayBeforeLoading(400)
		.cacheInMemory(true)
		.cacheOnDisc(false)
		.build();
		return options;
	}

}
