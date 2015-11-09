package com.sx.kakou.view;
import java.util.List;


import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import com.example.sx_kakou.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.square.github.restrofit.Constants;
import com.square.github.restrofit.KakouClient;
import com.square.github.restrofit.ServiceGenerator;
import com.sx.kakou.tricks.PopupWindowContentAdapter;
import com.sx.kakou.tricks.PullLoadMoreRecyclerView;
import com.sx.kakou.tricks.RealTimeFreshAdapter;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;


public class FragmentRealTime extends Fragment implements View.OnClickListener {
    private PullLoadMoreRecyclerView homeRefreshLayout;
    private RealTimeFreshAdapter freshAdapter;
    private static TextView tv_place;
    private static TextView tv_fxbh;
    private static TextView tv_auto;
    private static ImageView img_waiting;
    private int user_id = -1;

    private static boolean isAuto = true;
    private static boolean isCompleted= true;
    private PopupWindow popupWindow;

    SharedPreferences mPreferences = null;
    Animation operatingAnim;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentrealtime, null);
        mPreferences = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        homeRefreshLayout = (PullLoadMoreRecyclerView) view.findViewById(R.id.home_swip);
        tv_place = (TextView) view.findViewById(R.id.rt_tv_place);
        tv_fxbh = (TextView) view.findViewById(R.id.rt_tv_fxhb);
        img_waiting = (ImageView) view.findViewById(R.id.rt_img_waiting);
        tv_place.setText(mPreferences.getString("rt_place", "卡口地点"));
        tv_fxbh.setText(mPreferences.getString("rt_fxbh", "卡口方向"));
        tv_place.setOnClickListener(this);
        tv_fxbh.setOnClickListener(this);
        tv_auto = (TextView) view.findViewById(R.id.rt_tv_auto);
        tv_auto.setOnClickListener(this);
        Intent intent = getActivity().getIntent();
        user_id = intent.getIntExtra("user_id", -1);
        homeRefreshLayout.setLinearLayout();
        homeRefreshLayout.setPullLoadMoreListener(new PullLoadMoreListener());
        operatingAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        if (operatingAnim != null) {
            img_waiting.startAnimation(operatingAnim);
        }
        //禁止上拉加载
        homeRefreshLayout.setHasMore(false);
        RefreshCarInfo(user_id);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isAuto = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isAuto = true;
    }

    class PullLoadMoreListener implements PullLoadMoreRecyclerView.PullLoadMoreListener {
        @Override
        public void onRefresh() {
            homeRefreshLayout.setPullLoadMoreCompleted();
        }

        @Override
        public void onLoadMore() {
        }
    }
    public void RefreshCarInfo(final int userid){
                if (isCompleted){
                    isCompleted = false;
                    String place = mPreferences.getString("rt_code_place", "");
                    String fxbh = mPreferences.getString("rt_code_fxbh", "");
                    if (place.equals("0")){
                        place = "";
                    }else {
                        place = "+place:"+place;
                    }
                    if (fxbh.equals("0")){
                        fxbh = "";
                    }else {
                        fxbh = "+fxbh:"+fxbh;
                    }
                    String queryString = userid+place+fxbh;
                    System.out.println(queryString);
                    KakouClient client = ServiceGenerator.createService(KakouClient.class, Constants.BASE_URL);
                    MyCallback mc = new MyCallback();
                    client.getRefresh(queryString, mc);
                }

    }
        class MyCallback implements Callback{

            @Override
            public void success(Object o, Response response) {
                JsonArray mArray = ((JsonObject)o).get("items").getAsJsonArray();
                if (mArray.size() > 0 && homeRefreshLayout != null) {
                    if (freshAdapter == null) {
                        freshAdapter = new RealTimeFreshAdapter();
                        freshAdapter.setDataList(mArray);
                        setOnItemClickListener(freshAdapter);
                        homeRefreshLayout.setAdapter(freshAdapter);
                    } else {
                        setOnItemClickListener(freshAdapter);
                        //添加在前面
                        JsonArray jsonarray = new JsonArray();
                        jsonarray.addAll(mArray);
                        jsonarray.addAll(freshAdapter.getDataList());
                        freshAdapter.setDataList(jsonarray);
                        freshAdapter.notifyDataSetChanged();
                    }
                }
                homeRefreshLayout.setRefreshing(false);
                homeRefreshLayout.setPullLoadMoreCompleted();
                isCompleted = true;
                if (isAuto) {
                    RefreshCarInfo(user_id);
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                isCompleted = true;
                if (isAuto) {
                    RefreshCarInfo(user_id);
                }
                retrofitError.printStackTrace();
            }

        }

    public void setOnItemClickListener(RealTimeFreshAdapter adapter) {
        adapter.setOnItemClickListener(new RealTimeFreshAdapter.MyItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                Intent intent = new Intent(getActivity(), HistoryItemActivity.class);
                intent.putExtra("data", freshAdapter.getDataList().toString());
                intent.putExtra("position", position);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    public void showPopupWindowArea(final List<Integer> code, final List<String> strs, final TextView textView, final View v) {

        View contentView = LayoutInflater.from(getActivity()).inflate(
                R.layout.popwin_content_list, null);
        // 要显示的数据  点多次有BUG
        ListView lv_content = (ListView) contentView
                .findViewById(R.id.lv_popupwindow_content_Area);
        popupWindow = new PopupWindow(contentView, 200, 300);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), Bitmap.createBitmap(200, 300, Bitmap.Config.ALPHA_8)));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(v, -40, 20);
        PopupWindowContentAdapter adapter = new PopupWindowContentAdapter(getActivity(), strs);
        lv_content.setAdapter(adapter);
        lv_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //搜索条件保存到本地
                mPreferences = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString("rt_code_" + v.getTag(), code.get(position) + "");
                editor.putString("rt_" + v.getTag(), strs.get(position));
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
        switch (v.getId()) {
            case R.id.rt_tv_place:
                v.setTag("place");
                showPopupWindowArea(MainActivity.place_code_list, MainActivity.place_list, tv_place, v);
                break;
            case R.id.rt_tv_fxhb:
                v.setTag("fxbh");
                showPopupWindowArea(MainActivity.fxbh_code_list, MainActivity.fxbh_list, tv_fxbh, v);
                break;
            case R.id.rt_tv_auto:
                String auto = isAuto ? "开始刷新":"正在刷新中...";
                tv_auto.setText(auto);

                if (isAuto) {
                    isAuto = false;
                    img_waiting.clearAnimation();
                } else {
                    if (operatingAnim != null) {
                        img_waiting.startAnimation(operatingAnim);
                    }
                    isAuto = true;
                    RefreshCarInfo(user_id);
                }
                break;
        }
    }
}