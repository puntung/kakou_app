package com.sx.kakou.view;

import com.example.sx_kakou.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.square.github.restrofit.Constants;
import com.square.github.restrofit.KakouClient;
import com.square.github.restrofit.ServiceGenerator;
import com.sx.kakou.tricks.ChangeDateDialog;
import com.sx.kakou.tricks.PopupWindowContentAdapter;
import com.sx.kakou.tricks.PullLoadMoreRecyclerView;
import com.sx.kakou.tricks.RecyclerViewAdapter;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FragmentHistory extends Fragment implements View.OnClickListener{
    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private ProgressDialog progressDialog = null;
    private  KakouClient client ;
	private int mCount = 1;
    private  JsonArray marray = new JsonArray();
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private static SharedPreferences mPreference;
    private PopupWindow popupWindow;
    private TextView t_place;
    private TextView t_fxhb;
    private TextView t_hpys;
    private TextView t_st;
    private TextView t_et;
    private EditText e_hphm;
    private TextView img_go;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragmenthistory, null);
        client = ServiceGenerator.createService(KakouClient.class, Constants.BASE_URL);
        mPreference = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView) view.findViewById(R.id.pullLoadMoreRecyclerView);
        //mPullLoadMoreRecyclerView.setRefreshing(true);
        //getCarinfosList(mCount);

        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
        t_place = (TextView)view.findViewById(R.id.fh_place);
        t_fxhb = (TextView)view.findViewById(R.id.fh_fxhb);
        t_hpys = (TextView)view.findViewById(R.id.fh_hpys);
        t_st = (TextView)view.findViewById(R.id.fh_st);
        t_et = (TextView)view.findViewById(R.id.fh_et);
        e_hphm = (EditText)view.findViewById(R.id.fh_hphm);
        img_go = (TextView)view.findViewById(R.id.fh_nav_go);

        initDate();
        t_place.setOnClickListener(this);
        t_fxhb.setOnClickListener(this);
        t_hpys.setOnClickListener(this);
        t_st.setOnClickListener(this);
        t_et.setOnClickListener(this);
        img_go.setOnClickListener(this);
        mPullLoadMoreRecyclerView.setGridLayout();
        mPullLoadMoreRecyclerView.setPullLoadMoreListener(new PullLoadMoreListener());
		return view;
	}

    class PullLoadMoreListener implements PullLoadMoreRecyclerView.PullLoadMoreListener {
        @Override
        public void onRefresh() {
            setRefresh();
            SaveNavDate();
            getCarinfosList(mCount);
            mPullLoadMoreRecyclerView.setRefreshing(false);
        }

        @Override
        public void onLoadMore() {
            mCount = mCount + 1;
           getCarinfosList(mCount);
        }
    }

    private void setRefresh() {
        mCount = 1;
    }

    private void initDate(){
        String  kakou_place = mPreference.getString("kakou_place","");
        String  kakou_fxbh = mPreference.getString("kakou_fxbh", "");
        String  kakou_hpys = mPreference.getString("kakou_hpys", "");
        String  kakou_st = mPreference.getString("kakou_st", "");
        String kakou_et = mPreference.getString("kakou_et", "");

        if (!kakou_place.equals("")){t_place.setText(kakou_place);}
        if (!kakou_fxbh.equals("")){t_fxhb.setText(kakou_fxbh);}
        if (!kakou_hpys.equals("")){t_hpys.setText(kakou_hpys);}
        if (!kakou_st.equals("")){t_st.setText(kakou_st);}
        if (!kakou_et.equals("")){t_et.setText(kakou_et);}
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fh_place:
                showPopupWindowArea(MainActivity.place_code_list, MainActivity.place_list, t_place, v);
                break;
            case R.id.fh_fxhb:
                showPopupWindowArea(MainActivity.fxbh_code_list,MainActivity.fxbh_list, t_fxhb, v);
                break;
            case R.id.fh_hpys:
                showPopupWindowArea(MainActivity.hpys_code_list,MainActivity.hpys_list,t_hpys,v);
                break;
            case R.id.fh_st:
                SetDate(t_st);
                break;
            case R.id.fh_et:
                SetDate(t_et);
                break;
            case R.id.fh_nav_go:
                if (t_place.getText().toString().equals(getActivity().getResources().getString(R.string.place))|| t_fxhb.getText().equals(getActivity().getResources().getString(R.string.fxbh))
                        ||t_hpys.equals(getActivity().getResources().getString(R.string.hpys)) ||t_st.getText().toString().equals(getActivity().getResources().getString(R.string.start_date))
                        || t_et.getText().equals(getActivity().getResources().getString(R.string.end_date))){
                    Toast.makeText(getActivity(),"请完整查询条件",Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog = ProgressDialog.show(getActivity(), null, "正在加载数据...", true);
                    progressDialog.setCancelable(false);
                    mPullLoadMoreRecyclerView.setRefreshing(true);
                    mRecyclerViewAdapter = null;
                    SaveNavDate();
                    setRefresh();
                    getCarinfosList(mCount);
                    mPullLoadMoreRecyclerView.setRefreshing(false);
                }
                break;

        }
    }
    public void SaveNavDate(){
        SharedPreferences.Editor editor = mPreference.edit();
        if(t_place.getTag()!=null){
            String  kakou_place_code = t_place.getTag()+"";
            editor.putString("kakou_place_code",kakou_place_code);
        }
        if (t_fxhb.getTag()!=null){
            String  kakou_fxbh_code = t_fxhb.getTag()+"";
            editor.putString("kakou_fxbh_code",kakou_fxbh_code);
        }
        editor.putString("kakou_place",t_place.getText().toString());
        editor.putString("kakou_fxbh",t_fxhb.getText().toString());
        editor.putString("kakou_hpys",t_hpys.getText().toString());
        editor.putString("kakou_hphm",e_hphm.getText().toString());
        editor.putString("kakou_st", t_st.getText().toString());
        editor.putString("kakou_et", t_et.getText().toString());
        editor.commit();
    }
    public void SetDate(final TextView view){
        try{
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ChangeDateDialog mChangeDateDialog = new ChangeDateDialog(getActivity());
        Calendar calendar = Calendar.getInstance();
        if (!t_st.getText().toString().equals("起始时间")&& !t_et.getText().toString().equals("结束时间")){
            calendar.clear();
            Date text_date = dateFormat.parse(view.getText().toString());
            calendar.setTime(text_date);

        }
            mChangeDateDialog.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            mChangeDateDialog.show();
        mChangeDateDialog.setDateListener(new ChangeDateDialog.OnDateListener() {
            @Override
            public void onClick(String year, String month, String day, String hour, String minute) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = DateFormat(year) + "-" + DateFormat(month) + "-" + DateFormat(day) + " " + DateFormat(hour) + ":" + DateFormat(minute) + ":" + calendar.get(Calendar.SECOND);
                if (view.getId() == R.id.fh_et) {
                    try {
                        Date s_date = sdf.parse(t_st.getText().toString());
                        Date e_date = sdf.parse(date);
                        if (e_date.before(s_date)) {
                            Toast.makeText(getActivity(), "结束时间不可以小于开始时间", Toast.LENGTH_SHORT).show();
                        } else {
                            view.setText(date);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    view.setText(date);
                }
            }
        });
        }catch (Exception e){e.printStackTrace();}
    }

    private String DateFormat(String date){
       int f_date =  Integer.parseInt(date);
        if (f_date<10){
            return  "0"+f_date;
        }else {
            return f_date+"";
        }
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
    public void showPopupWindowArea(final List<Integer> code ,final List<String> strs, final TextView textView, View v) {

        View contentView = LayoutInflater.from(getActivity()).inflate(
                R.layout.popwin_content_list, null);
        ListView lv_content = (ListView) contentView
                .findViewById(R.id.lv_popupwindow_content_Area);
        PopupWindowContentAdapter adapter = new PopupWindowContentAdapter(getActivity(), strs);
        lv_content.setAdapter(adapter);
        lv_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                textView.setText(strs.get(position).toString());
                textView.setTag(code.get(position));
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
        //WindowManager wm = getActivity().getWindowManager();
        popupWindow = new PopupWindow();
        popupWindow.setContentView(contentView);
        popupWindow.setWidth(200);
        popupWindow.setHeight(300);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(v, -40, 20);
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

        public void getCarinfosList(final int mCount) {
            String  place = "+place:"+ mPreference.getString("kakou_place_code","");
            if (mPreference.getString("kakou_place_code","").equals("0") ){
                place = "";
            }
            String  fxbh = "+fxbh:"+ mPreference.getString("kakou_fxbh_code", "");
            if (mPreference.getString("kakou_fxbh_code", "").equals("0") ){
                fxbh = "";
            }
            String  hpys =  mPreference.getString("kakou_hpys", "");
            String  hphm = mPreference.getString("kakou_hphm","");
            String  ppdm = "";
            if (!hpys.equals("")){
                hpys = "+hpys:"+hpys.substring(0, 1);
            }
            final String  st = mPreference.getString("kakou_st", "");
            String et = mPreference.getString("kakou_et", "");
           // String queryStr = hphm+"%+st:"+st+"+et:"+et+"+place:"+place+"+fxbh:"+fxbh+"+hpys:"+hpys+"+ppdm:114&page=" + mCount + "&per_page=20&sort=ppdm&order=desc";
            String queryStr = hphm+"%+st:"+st+"+et:"+et+place+fxbh+hpys+ppdm+"&page=" + mCount + "&per_page=20&sort=jgsj&order=desc";
            System.out.println(queryStr);
            client.getCarInfosList(queryStr, new Callback<JsonObject>() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void success(JsonObject jsonObject, Response response) {
                    JsonArray iarray = jsonObject.get("items").getAsJsonArray();
                    if (iarray.size()!=0){
                        try {
                            if (mRecyclerViewAdapter == null) {
                                mRecyclerViewAdapter = new RecyclerViewAdapter();
                                mRecyclerViewAdapter.setDataList(iarray);
                                marray = mRecyclerViewAdapter.getDataList();
                                setOnItemClickListener(mRecyclerViewAdapter);
                                mPullLoadMoreRecyclerView.setAdapter(mRecyclerViewAdapter);
                            } else {
                                setOnItemClickListener(mRecyclerViewAdapter);
                                mRecyclerViewAdapter.getDataList().addAll(iarray);
                                marray = mRecyclerViewAdapter.getDataList();
                                mRecyclerViewAdapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getActivity(),"没有新数据",Toast.LENGTH_SHORT).show();
                    }
                    mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                    progressDialog.dismiss();
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    retrofitError.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(),"抱歉！出现了异常",Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void setOnItemClickListener(RecyclerViewAdapter adapter){
            adapter.setOnItemClickListener(new RecyclerViewAdapter.MyItemClickListener() {
                @Override
                public void OnItemClick(int position) {
                    Intent intent = new Intent(getActivity(),HistoryItemActivity.class);
                    intent.putExtra("data",marray.toString());
                    intent.putExtra("position", position);
                    intent.putExtra("count",mCount);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        }

}
