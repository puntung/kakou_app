package com.sx.kakou.view;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import com.example.sx_kakou.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.google.gson.JsonParser;
import com.square.github.restrofit.Constants;
import com.square.github.restrofit.KakouClient;
import com.square.github.restrofit.ServiceGenerator;
import com.sx.kakou.tricks.ControlService;
import com.sx.kakou.tricks.ControlViewAdapter;
import com.sx.kakou.tricks.PopupWindowContentAdapter;
import com.sx.kakou.tricks.PullLoadMoreRecyclerView;
import com.sx.kakou.tricks.RecyclerViewAdapter;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


public class FragmentRealTime extends Fragment implements View.OnClickListener{
	private  PullLoadMoreRecyclerView homeRefreshLayout;
    public RecyclerView controlRecycler;
    private ControlReceiver mReceiver = null;
	private static TextView tv_place;
	private static TextView tv_fxbh;
	private static TextView tv_control;
    private static EditText rt_et_hphm;
    SharedPreferences mPreferences  = null;
    public static SharedPreferences control_list_preferences  = null;
	private  int user_id= -1;
    private PopupWindow popupWindow;
    private Intent mintent;
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragmentrealtime, null);
        mPreferences = getActivity().getSharedPreferences("config",Context.MODE_PRIVATE);
        control_list_preferences = getActivity().getSharedPreferences("control_list",Context.MODE_PRIVATE);
		homeRefreshLayout = (PullLoadMoreRecyclerView)view.findViewById(R.id.home_swip);
        controlRecycler = (RecyclerView)view.findViewById(R.id.control_swip);
        tv_place = (TextView)view.findViewById(R.id.rt_tv_place);
		tv_fxbh = (TextView)view.findViewById(R.id.rt_tv_fxhb);
        rt_et_hphm = (EditText)view.findViewById(R.id.rt_et_hphm);
        tv_place.setText(mPreferences.getString("rt_place", "卡口地点"));
        tv_fxbh.setText(mPreferences.getString("rt_fxbh", "卡口方向"));
        tv_place.setOnClickListener(this);
        tv_fxbh.setOnClickListener(this);
        tv_control = (TextView)view.findViewById(R.id.rt_tv_control);
        tv_control.setOnClickListener(this);
		Intent intent = getActivity().getIntent();
		user_id = intent.getIntExtra("user_id",-1);
        homeRefreshLayout.setLinearLayout();
        homeRefreshLayout.setPullLoadMoreListener(new PullLoadMoreListener());
        //禁止上拉加载
        homeRefreshLayout.setHasMore(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        controlRecycler.setLayoutManager(linearLayoutManager);


        mintent = new Intent(getActivity(), ControlService.class);
       if (mReceiver==null){
           //注册Reciver
           mReceiver = new ControlReceiver(getActivity(),controlRecycler,homeRefreshLayout);
           IntentFilter filter = new IntentFilter();
           filter.addAction(Constants.CONTROL_CARINFO_ACTION);
           getActivity().registerReceiver(mReceiver, filter);
       }

		return view;
	}

    class PullLoadMoreListener implements PullLoadMoreRecyclerView.PullLoadMoreListener {
        @Override
        public void onRefresh() {

        }

        @Override
        public void onLoadMore() {
        }
    }



    public static class ControlReceiver extends BroadcastReceiver {
        private JSONArray controlarray = null;
        private  RecyclerView mRecyclerView;
        private  RecyclerViewAdapter mRecyclerViewAdapter;
        private PullLoadMoreRecyclerView homeRefreshLayout;
        private static Context context;
        public ControlReceiver() {
        }
        public ControlReceiver(Context context,RecyclerView mRecyclerView,PullLoadMoreRecyclerView homeRefreshLayout) {
            this.context = context;
            this.mRecyclerView = mRecyclerView;
            this.homeRefreshLayout = homeRefreshLayout;
        }


        @Override
        public void onReceive(Context context, Intent intent) {
            //更新Ui
            String action = intent.getAction();
            if (action.equals(Constants.CONTROL_CARINFO_ACTION)){
                Bundle bundle = intent.getExtras();
                int index = bundle.getInt("index");
                String control_result = bundle.getString("control_result");
                try {
                    //只监控一个
                    if (mRecyclerView != null){
                        controlarray = new JSONArray();
                        JSONObject object = new JSONObject();
                        object.put("control_info","正在进行第 "+index+" 次监控");
                        object.put("control_hphm", rt_et_hphm.getText().toString());
                        object.put("control_place", tv_place.getText().toString());
                        object.put("control_fxbh", tv_fxbh.getText().toString());
                        controlarray.put(object);
                        ControlViewAdapter control_adapter = new ControlViewAdapter();
                        control_adapter.setDataList(controlarray);
                        mRecyclerView.setAdapter(control_adapter);
                        control_adapter.notifyDataSetChanged();
                    }

                    //刷新列表
                    JsonParser parser = new JsonParser();
                    JsonElement element = parser.parse(control_result);
                    JsonArray mArray = element.getAsJsonArray();

                    if (mArray.size() > 0 && homeRefreshLayout != null) {
                        if (mRecyclerViewAdapter == null) {
                            mRecyclerViewAdapter = new RecyclerViewAdapter();
                            mRecyclerViewAdapter.setDataList(mArray);
                           setOnItemClickListener(mRecyclerViewAdapter);
                            homeRefreshLayout.setAdapter(mRecyclerViewAdapter);
                        } else {
                           setOnItemClickListener(mRecyclerViewAdapter);
                            //添加在前面
                            JsonArray jsonarray = new JsonArray();
                            jsonarray.addAll(mArray);
                            jsonarray.addAll(mRecyclerViewAdapter.getDataList());
                            SharedPreferences.Editor editor = control_list_preferences.edit();
                            editor.putString("control_list",jsonarray.toString());
                            editor.apply();
                            //从本地保存的数据中读取
                            String mlistStr = control_list_preferences.getString("control_list", "");
                            JsonParser p = new JsonParser();
                            JsonElement e = p.parse(mlistStr);
                            JsonArray a = e.getAsJsonArray();
                            mRecyclerViewAdapter.setDataList(a);
                            mRecyclerViewAdapter.notifyDataSetChanged();
                        }
                        homeRefreshLayout.setPullLoadMoreCompleted();
                    }

                }catch (Exception e){e.printStackTrace();}

        }

    }
        public static void setOnItemClickListener(RecyclerViewAdapter adapter){
            adapter.setOnItemClickListener(new RecyclerViewAdapter.MyItemClickListener() {
                @Override
                public void OnItemClick(int position) {
                    Intent intent = new Intent(context,HistoryItemActivity.class);
                    intent.putExtra("data", control_list_preferences.getString("control_list",""));
                    intent.putExtra("position", position);
                    context.startActivity(intent);
                    //context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        }
//
   }


	public void getPlace(final View v){
		KakouClient client = ServiceGenerator.createService(KakouClient.class, Constants.BASE_URL);
		client.getPlace(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                try {
                    List<String> list = new ArrayList<>();
                    JSONArray array = new JSONArray(jsonObject.get("items").toString());
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = new JSONObject(array.get(i).toString());
                        list.add(object.getString("name"));
                    }
                    showPopupWindowArea(list, tv_place, v, "place");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
                Toast.makeText(getActivity(),"抱歉！出现了异常，请与开发者联系",Toast.LENGTH_SHORT).show();
            }
        });
	}

	public void getFxbh(final View v){
		KakouClient client = ServiceGenerator.createService(KakouClient.class,Constants.BASE_URL);
		client.getFxhb(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                try {
                    List<String> list = new ArrayList<>();
                    JSONArray array = new JSONArray(jsonObject.get("items").toString());
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = new JSONObject(array.get(i).toString());
                        list.add(object.getString("name"));
                    }
                    showPopupWindowArea(list, tv_fxbh, v,"fxbh");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
                Toast.makeText(getActivity(),"抱歉！出现了异常，请与开发者联系",Toast.LENGTH_SHORT).show();
            }
        });
	}


    /**
     * 数据1
     *
     * @param strs
     *            popupwindow中要显示的数据
     * @param textView
     *            选择后显示数据的textview
     * @param v
     *            点击的view
     */
    public void showPopupWindowArea(final List<String> strs, final TextView textView, View v,final String tag) {

        View contentView = LayoutInflater.from(getActivity()).inflate(
                R.layout.popwin_content_list, null);
        // 要显示的数据  点多次有BUG要修复
        ListView lv_content = (ListView) contentView
                .findViewById(R.id.lv_popupwindow_content_Area);

        popupWindow = new PopupWindow(contentView,200,300);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), Bitmap.createBitmap(200, 300, Bitmap.Config.ALPHA_8)));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(v, -40, 20);

        Toast.makeText(getActivity(),strs+"",Toast.LENGTH_SHORT).show();
        PopupWindowContentAdapter adapter = new PopupWindowContentAdapter(getActivity(), strs);
        lv_content.setAdapter(adapter);
        lv_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //搜索条件保存到本地
                mPreferences = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString("rt_code_" + tag, position + "");
                editor.putString("rt_" + tag, strs.get(position));
                editor.apply();
                textView.setText(strs.get(position));
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });

        WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
        params.alpha = 0.7f;
        getActivity().getWindow().setAttributes(params);

        popupWindow.update();
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            public void onDismiss() {
                WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
                params.alpha = 1f;
                getActivity().getWindow().setAttributes(params);
            }
        });
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.rt_tv_place:
                getPlace(v);
                break;
			case R.id.rt_tv_fxhb:
                getFxbh(v);
				break;
			case R.id.rt_tv_control:
                try{
                    tv_control.setEnabled(false);
                    mintent.putExtra("user_id",user_id);
                    mintent.putExtra("rt_code_place",mPreferences.getString("rt_code_place", "0"));
                    mintent.putExtra("rt_code_fxbh",mPreferences.getString("rt_code_fxbh", "0"));
                    getActivity().startService(mintent);
                }catch (Exception e){e.printStackTrace();}
                break;


		}
	}

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        System.out.println("destory");
//        getActivity().unregisterReceiver(mReceiver);
//        getActivity().stopService(mintent);
//
//    }
}
