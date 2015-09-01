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
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
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
    private  KakouClient client ;
	private int mCount = 1;
    private  List<JSONObject> mlist = new ArrayList<JSONObject>();
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private static SharedPreferences mPreference;
    private PopupWindow popupWindow;
    private TextView t_place;
    private TextView t_fxhb;
    private TextView t_hpys;
    private TextView t_st;
    private TextView t_et;
    private DatePickerDialog datePickerDialog;
    private Calendar mCalendar;
    private ImageView img_go;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragmenthistory, null);
        client = ServiceGenerator.createService(KakouClient.class, Constants.BASE_URL);
        mPreference = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView) view.findViewById(R.id.pullLoadMoreRecyclerView);
        //mPullLoadMoreRecyclerView.setRefreshing(true);
        t_place = (TextView)view.findViewById(R.id.fh_place);
        t_fxhb = (TextView)view.findViewById(R.id.fh_fxhb);
        t_hpys = (TextView)view.findViewById(R.id.fh_hpys);
        t_st = (TextView)view.findViewById(R.id.fh_st);
        t_et = (TextView)view.findViewById(R.id.fh_et);
        img_go = (ImageView)view.findViewById(R.id.fh_nav_go);
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
                getPlace(v);
                break;
            case R.id.fh_fxhb:
                getFxbh(v);
                break;
            case R.id.fh_hpys:
                getHpys(v);
                break;
            case R.id.fh_st:
                SetDate(t_st);
                break;
            case R.id.fh_et:
                SetDate(t_et);
                break;
            case R.id.fh_nav_go:
                mPullLoadMoreRecyclerView.setRefreshing(true);
                SaveNavDate();
                setRefresh();
                getCarinfosList(mCount);
                mPullLoadMoreRecyclerView.setRefreshing(false);
                break;
        }
    }
    public void SaveNavDate(){
        SharedPreferences.Editor editor = mPreference.edit();
        if(t_place.getTag()!=null){
            String  kakou_place_code = Integer.parseInt(t_place.getTag().toString())+2+"";
            editor.putString("kakou_place_code",kakou_place_code);
        }
        if (t_fxhb.getTag()!=null){
            String  kakou_fxbh_code = Integer.parseInt(t_fxhb.getTag().toString())+1+"";
            editor.putString("kakou_fxbh_code",kakou_fxbh_code);
        }
        editor.putString("kakou_place",t_place.getText().toString());
        editor.putString("kakou_fxbh",t_fxhb.getText().toString());
        editor.putString("kakou_hpys",t_hpys.getText().toString());
        editor.putString("kakou_st",t_st.getText().toString());
        editor.putString("kakou_et",t_et.getText().toString());
        editor.commit();
    }
    public void SetDate(final TextView view){
        ChangeDateDialog mChangeBirthDialog = new ChangeDateDialog(
                getActivity());
        Calendar calendar = Calendar.getInstance();
        mChangeBirthDialog.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH));
        mChangeBirthDialog.show();
        mChangeBirthDialog.setDateListener(new ChangeDateDialog.OnDateListener() {

            @Override
            public void onClick(String year, String month, String day) {
                // TODO Auto-generated method stub
                String date = year + "-" + month + "-" + day;

                if (view.getId() == R.id.fh_et){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date s_date = sdf.parse(t_st.getText().toString());
                        Date e_date = sdf.parse(date);
                        if (e_date.before(s_date)){
                            Toast.makeText(getActivity(),"结束时间不可以小于开始时间",Toast.LENGTH_SHORT).show();
                        }else {
                            view.setText(date);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else {
                    view.setText(date);
                }
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
    public void showPopupWindowArea(final List<String> strs, final TextView textView, View v) {

        View contentView = LayoutInflater.from(getActivity()).inflate(
                R.layout.popwin_content_list, null);

        // 要显示的数据
        ListView lv_content = (ListView) contentView
                .findViewById(R.id.lv_popupwindow_content_Area);

        PopupWindowContentAdapter adapter = new PopupWindowContentAdapter(getActivity(), strs);
        lv_content.setAdapter(adapter);
        // item点击项
        lv_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                textView.setText(strs.get(position).toString());
                textView.setTag(position);
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
        WindowManager wm = getActivity().getWindowManager();
        popupWindow = new PopupWindow(contentView, 200,
                300);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(v,-40,20);

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

    public void getHpys(final  View view){
       client.getHpys(new Callback<JsonObject>() {
           @Override
           public void success(JsonObject jsonObject, Response response) {
              try {
                  List<String> hpys = new ArrayList<>();
                  JSONArray array = new JSONArray(jsonObject.get("items").toString());
                  for (int i = 0; i < array.length(); i++) {
                      JSONObject mobject = new JSONObject(array.get(i).toString());
                      hpys.add(mobject.getString("name"));
                  }
                  showPopupWindowArea(hpys,t_hpys,view);
              }catch (Exception e){
                  e.printStackTrace();
              }
           }

           @Override
           public void failure(RetrofitError retrofitError) {

           }
       });
    }

    public void getPlace(final View v){
        client.getPlace(new Callback<JsonObject>() {
            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
            }
            @Override
            public void success(JsonObject arg0, Response arg1) {
                try {
                    List<String> mplace = new ArrayList<>();
                    JSONArray array = new JSONArray(arg0.get("items").toString());
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = new JSONObject(array.get(i).toString());
                        mplace.add(object.getString("name"));
                    }
                    showPopupWindowArea(mplace,t_place,v);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }
    public void getFxbh(final View view){
        client.getFxhb(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                try {
                    List<String> mfxbh = new ArrayList<>();
                    JSONArray array = new JSONArray(jsonObject.get("items").toString());
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = new JSONObject(array.get(i).toString());
                        mfxbh.add(object.getString("name"));
                    }
                  showPopupWindowArea(mfxbh,t_fxhb,view);
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
        public void getCarinfosList(final int mCount) {
            String  place = mPreference.getString("kakou_place_code","");
            String  fxbh = mPreference.getString("kakou_fxbh_code", "");
            String  hpys = mPreference.getString("kakou_hpys", "");
            String  st = mPreference.getString("kakou_st", "");
            String et = mPreference.getString("kakou_et", "");

            String q = "粤LD%+st:"+st+"+et:"+et+"+place:"+place+"+fxbh:"+fxbh+"+hpys:"+hpys+"+ppdm:114&page=" + mCount + "&per_page=20&sort=ppdm&order=desc";
            System.out.println(q);
            client.getCarInfosList(q, new Callback<JsonObject>() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void success(JsonObject jsonObject, Response response) {
                    System.out.println(jsonObject.toString());
                    JsonArray marray = jsonObject.get("items").getAsJsonArray();
                    if (marray.size()!=0){
                    try {
                        if (mRecyclerViewAdapter == null) {
                            mRecyclerViewAdapter = new RecyclerViewAdapter(getActivity(), mPullLoadMoreRecyclerView, marray);
                            mPullLoadMoreRecyclerView.setAdapter(mRecyclerViewAdapter);
                        } else {
                            mRecyclerViewAdapter.getDataList().addAll(marray);
                            mRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    }else {
                        Toast.makeText(getActivity(),"没有新数据",Toast.LENGTH_SHORT).show();
                    }
                    mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    System.out.println("failure");
                }
            });
        }

}
