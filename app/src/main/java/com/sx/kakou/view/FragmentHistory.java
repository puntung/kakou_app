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
import com.sx.kakou.util.InitData;
import com.sx.kakou.util.LruCacheUtil;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.logging.StreamHandler;

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
    private static SharedPreferences config_Preference;
    private PopupWindow popupWindow;
    private TextView t_place;
    private TextView t_fxhb;
    private TextView t_hpys;
    private TextView t_ppdm;
    private TextView t_st;
    private TextView t_et;
    private EditText e_hphm;
    private TextView img_go;
    private TextView order_ppdm;
    private TextView order_jgsj;
    private ImageView order_icon;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragmenthistory, null);
        client = ServiceGenerator.createService(KakouClient.class, Constants.BASE_URL);
        config_Preference = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView) view.findViewById(R.id.pullLoadMoreRecyclerView);

        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
        t_place = (TextView)view.findViewById(R.id.fh_place);
        t_fxhb = (TextView)view.findViewById(R.id.fh_fxhb);
        t_hpys = (TextView)view.findViewById(R.id.fh_hpys);
        t_ppdm = (TextView)view.findViewById(R.id.fh_ppdm);
        t_st = (TextView)view.findViewById(R.id.fh_st);
        t_et = (TextView)view.findViewById(R.id.fh_et);
        e_hphm = (EditText)view.findViewById(R.id.fh_hphm);
        img_go = (TextView)view.findViewById(R.id.fh_nav_go);
        order_ppdm = (TextView)view.findViewById(R.id.fh_order_ppdm);
        order_jgsj = (TextView)view.findViewById(R.id.fh_order_jcsj);
        order_icon = (ImageView)view.findViewById(R.id.fh_order_icon);
        order_jgsj.setSelected(true);
        initDate();
        t_place.setOnClickListener(this);
        t_fxhb.setOnClickListener(this);
        t_hpys.setOnClickListener(this);
        t_ppdm.setOnClickListener(this);
        t_st.setOnClickListener(this);
        t_et.setOnClickListener(this);
        img_go.setOnClickListener(this);
        order_ppdm.setOnClickListener(this);
        order_jgsj.setOnClickListener(this);
        order_icon.setOnClickListener(this);
        mPullLoadMoreRecyclerView.setGridLayout();
        mPullLoadMoreRecyclerView.setPullLoadMoreListener(new PullLoadMoreListener());
		return view;
	}

    class PullLoadMoreListener implements PullLoadMoreRecyclerView.PullLoadMoreListener {
        @Override
        public void onRefresh() {
            if (t_place.getText().toString().equals(getActivity().getResources().getString(R.string.place))|| t_fxhb.getText().equals(getActivity().getResources().getString(R.string.fxbh))
                    ||t_hpys.equals(getActivity().getResources().getString(R.string.hpys)) ||t_st.getText().toString().equals(getActivity().getResources().getString(R.string.start_date))
                    || t_et.getText().equals(getActivity().getResources().getString(R.string.end_date))){
                Toast.makeText(getActivity(),"请完整查询条件",Toast.LENGTH_SHORT).show();
            }else{
                mRecyclerViewAdapter = null;
                SaveNavDate();
                setRefresh();
                getCarinfosList(mCount);
            }
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
        String  kakou_place = config_Preference.getString("kakou_kkdd", "");
        String  kakou_fxbh = config_Preference.getString("kakou_fxbh", "");
        String  kakou_hpys = config_Preference.getString("kakou_hpys", "");
        String  kakou_ppdm = config_Preference.getString("kakou_ppdm", "");
        String  kakou_st = config_Preference.getString("kakou_st", "");
        String kakou_et = config_Preference.getString("kakou_et", "");

        if (!kakou_place.equals("")){t_place.setText(kakou_place);}
        if (!kakou_fxbh.equals("")){t_fxhb.setText(kakou_fxbh);}
        if (!kakou_hpys.equals("")){t_hpys.setText(kakou_hpys);}
        if (!kakou_ppdm.equals("")){t_ppdm.setText(kakou_ppdm);}
        if (!kakou_st.equals("")){t_st.setText(kakou_st);}
        if (!kakou_et.equals("")){t_et.setText(kakou_et);}
    }
    @Override
    public void onClick(View v) {
        SharedPreferences.Editor editor = config_Preference.edit();
        switch (v.getId()){
            case R.id.fh_place:
                showPopupWindowArea(InitData.kkdd_code_list, InitData.kkdd_list, t_place);
                break;
            case R.id.fh_fxhb:
                showPopupWindowArea(InitData.fxbh_code_list,InitData.fxbh_list, t_fxhb);
                break;
            case R.id.fh_hpys:
                showPopupWindowArea(InitData.hpys_code_list, InitData.hpys_list, t_hpys);
                break;
            case R.id.fh_ppdm:
                showPopupWindowArea(InitData.ppdm_code_list,InitData.ppdm_list,t_ppdm);
                break;
            case R.id.fh_order_jcsj:
                if(!order_jgsj.isSelected()){
                    editor.putString("kakou_sort", "jgsj");
                    order_ppdm.setSelected(false);
                    order_jgsj.setSelected(true);
                    editor.apply();
                    getCarinfosList(mCount);
                }
                break;
            case R.id.fh_order_ppdm:
                if (!order_ppdm.isSelected()){
                    editor.putString("kakou_sort", "ppdm");
                    order_jgsj.setSelected(false);
                    order_ppdm.setSelected(true);
                    editor.apply();
                    getCarinfosList(mCount);
                }
                break;
            case R.id.fh_order_icon:
                if (order_icon.isSelected()){
                    order_icon.setSelected(false);
                    editor.putString("kakou_order","asc");
//                    Toast.makeText(getActivity(),"升序",Toast.LENGTH_SHORT).show();
                }else {
                    order_icon.setSelected(true);
                    editor.putString("kakou_order", "desc");
//                    Toast.makeText(getActivity(),"降序",Toast.LENGTH_SHORT).show();
                }
                editor.apply();
                getCarinfosList(mCount);
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
                    mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                    SaveNavDate();
                    setRefresh();
                    getCarinfosList(mCount);
                }
                break;

        }
    }
    public void SaveNavDate(){
        SharedPreferences.Editor editor = config_Preference.edit();
        if(t_place.getTag()!=null){
            String  kakou_kkdd_code = t_place.getTag()+"";
            editor.putString("kakou_kkdd_code",kakou_kkdd_code);
        }
        if (t_fxhb.getTag()!=null){
            String  kakou_fxbh_code = t_fxhb.getTag()+"";
            editor.putString("kakou_fxbh_code",kakou_fxbh_code);
        }
        if (t_hpys.getTag()!=null){
            String  kakou_hpys_code = t_hpys.getTag()+"";
            editor.putString("kakou_hpys_code",kakou_hpys_code);
        }
        if (t_ppdm.getTag()!=null){
            String  kakou_ppdm_code = t_ppdm.getTag()+"";
            editor.putString("kakou_ppdm_code",kakou_ppdm_code);
        }
        editor.putString("kakou_kkdd",t_place.getText().toString());
        editor.putString("kakou_fxbh",t_fxhb.getText().toString());
        editor.putString("kakou_hpys",t_hpys.getText().toString());
        editor.putString("kakou_hphm",e_hphm.getText().toString());
        editor.putString("kakou_ppdm",t_ppdm.getText().toString());
        editor.putString("kakou_st", t_st.getText().toString());
        editor.putString("kakou_et", t_et.getText().toString());
        editor.apply();
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
                SimpleDateFormat sdf;
                sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

    public void showPopupWindowArea(final List code ,final List<String> strs, final TextView textView) {
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.popwin_content_list, null);
        final ListView lv_content = (ListView) contentView.findViewById(R.id.lv_popupwindow_content_Area);
        final TextView load_tip = (TextView)contentView.findViewById(R.id.pop_load_tip);
        PopupWindowContentAdapter adapter = new PopupWindowContentAdapter(getActivity(), strs);
        lv_content.setAdapter(adapter);
        if (popupWindow == null){
            popupWindow = new PopupWindow();
            popupWindow.setWidth(200);
            popupWindow.setHeight(300);
        }
        popupWindow.setContentView(contentView);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(textView, -40, 0);
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
        //list item click
        lv_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (textView.getId() == R.id.fh_ppdm  && position !=0) {
                    //show ppdm2,add progressdialog
                    load_tip.setVisibility(View.VISIBLE);
                    lv_content.setEnabled(false);
                    loadPpdm2(lv_content,load_tip, textView, code.get(position).toString().trim());
                } else {
                    textView.setText(strs.get(position));
                    textView.setTag(code.get(position));
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                }
            }
        });
    }
    public void loadPpdm2(final ListView ls,final TextView loadtip,final TextView textView,final String code){

        client.getPpdm2(code, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                try {
                    final List<String> list = new ArrayList<>();
                    final List<String> code_list = new ArrayList<>();
                    JSONArray array = new JSONArray(jsonObject.get("items").toString());
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = new JSONObject(array.get(i).toString());
                        list.add(object.getString("name"));
                        code_list.add(object.getString("code"));
                    }
                    //initview
                    PopupWindowContentAdapter adapter = new PopupWindowContentAdapter(getActivity(),list);
                    ls.setAdapter(adapter);
                    loadtip.setVisibility(View.GONE);
                    ls.setEnabled(true);
                    ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            textView.setText(list.get(position));
                            textView.setTag(code_list.get(position));
                            popupWindow.dismiss();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }
        public void getCarinfosList(final int mCount) {
            progressDialog = ProgressDialog.show(getActivity(), null, "正在加载数据...", true);
            progressDialog.setCancelable(false);
            mRecyclerViewAdapter = null;
            //when selected all code was 0
            String  kkdd = config_Preference.getString("kakou_kkdd_code","").equals("0")?"":"+kkdd:"+ config_Preference.getString("kakou_kkdd_code","").trim();
            String  fxbh = config_Preference.getString("kakou_fxbh_code","").equals("0")?"":"+fxbh_id:"+ config_Preference.getString("kakou_fxbh_code", "").trim();
            String  hpys =  config_Preference.getString("kakou_hpys", "").equals("0")?"":"+hpys:"+config_Preference.getString("kakou_hpys_code", "").trim();
            String  hphm = config_Preference.getString("kakou_hphm","").trim();
            String  sort = config_Preference.getString("kakou_sort","jgsj").trim();
            String  order = config_Preference.getString("kakou_order","desc").trim();
            String  ppdm =config_Preference.getString("kakou_ppdm_code","").equals("0")?"":"+ppdm:"+ config_Preference.getString("kakou_ppdm_code", "").trim();
            final String  st = config_Preference.getString("kakou_st", "");
            String et = config_Preference.getString("kakou_et", "");
           // String queryStr = hphm+"%+st:"+st+"+et:"+et+"+place:"+place+"+fxbh:"+fxbh+"+hpys:"+hpys+"+ppdm:114&page=" + mCount + "&per_page=20&sort=ppdm&order=desc";
            String queryStr = hphm+"%+st:"+st+"+et:"+et+kkdd+fxbh+hpys+ppdm+"&page=" + mCount + "&per_page=20&sort="+sort+"&order="+order;
            System.out.println(queryStr);
            client.getCarInfosList(queryStr, new Callback<JsonObject>() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void success(JsonObject jsonObject, Response response) {
                    JsonArray iarray = jsonObject.get("items").getAsJsonArray();
                    System.out.println(iarray.toString());
                    if (iarray.size() != 0) {
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
                        Toast.makeText(getActivity(), "没有数据", Toast.LENGTH_SHORT).show();
                    }
                    mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    retrofitError.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();
                }
            });
        }


        public void setOnItemClickListener(RecyclerViewAdapter adapter){
            adapter.setOnItemClickListener(new RecyclerViewAdapter.MyItemClickListener() {
                @Override
                public void OnItemClick(int position) {
                    Intent intent = new Intent(getActivity(), HistoryItemActivity.class);
                    intent.putExtra("data", marray.toString());
                    intent.putExtra("position", position);
                    intent.putExtra("count", mCount);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        }
}
