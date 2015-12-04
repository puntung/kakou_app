package com.sx.kakou.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

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
import com.sx.kakou.util.InitData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CarControlActivity extends Activity implements OnClickListener{
	private PullLoadMoreRecyclerView homeRefreshLayout;
	public RecyclerView controlRecycler;
	private ControlReceiver mReceiver = null;
	private static TextView cc_place;
	private static TextView cc_fxbh;
	private static TextView cc_control;
	private static TextView cc_back;
	private static EditText cc_et_hphm;
	SharedPreferences mPreferences  = null;
	public static SharedPreferences control_list_preferences  = null;
	private  int user_id= -1;
	private PopupWindow popupWindow;
	private Intent mintent;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_carcontrol);
        init();
	}


	public void init(){
		mPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);
		control_list_preferences = getSharedPreferences("control_list",Context.MODE_PRIVATE);
		homeRefreshLayout = (PullLoadMoreRecyclerView)findViewById(R.id.cc_home_swip);
		controlRecycler = (RecyclerView)findViewById(R.id.cc_control_swip);
		cc_place = (TextView)findViewById(R.id.cc_tv_place);
		cc_fxbh = (TextView)findViewById(R.id.cc_tv_fxhb);
		cc_et_hphm = (EditText)findViewById(R.id.cc_et_hphm);
		cc_place.setText(mPreferences.getString("cc_place", "卡口地点"));
		cc_fxbh.setText(mPreferences.getString("cc_fxbh", "卡口方向"));
		cc_place.setOnClickListener(this);
		cc_fxbh.setOnClickListener(this);
		cc_control = (TextView)findViewById(R.id.cc_tv_control);
		cc_back = (TextView)findViewById(R.id.cc_back);
		cc_control.setOnClickListener(this);
        cc_back.setOnClickListener(this);
		Intent intent = getIntent();
		user_id = intent.getIntExtra("user_id",-1);
		homeRefreshLayout.setLinearLayout();
		//homeRefreshLayout.setPullLoadMoreListener(new PullLoadMoreListener());
		//禁止上拉加载
		homeRefreshLayout.setHasMore(false);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		controlRecycler.setLayoutManager(linearLayoutManager);


		mintent = new Intent(this, ControlService.class);
		if (mReceiver==null){
			//注册Reciver
			mReceiver = new ControlReceiver(this,controlRecycler,homeRefreshLayout);
			IntentFilter filter = new IntentFilter();
			filter.addAction(Constants.CONTROL_CARINFO_ACTION);
			registerReceiver(mReceiver, filter);
		}
	}

	public static class ControlReceiver extends BroadcastReceiver {
		private JSONArray controlarray = null;
		private  RecyclerView mRecyclerView;
		private RecyclerViewAdapter mRecyclerViewAdapter;
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
						object.put("control_hphm", cc_et_hphm.getText().toString());
						object.put("control_place", cc_place.getText().toString());
						object.put("control_fxbh", cc_fxbh.getText().toString());
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

        View contentView = LayoutInflater.from(this).inflate(
                R.layout.popwin_content_list, null);
        // 要显示的数据  点多次有BUG要修复
        ListView lv_content = (ListView) contentView
                .findViewById(R.id.lv_popupwindow_content_Area);

        popupWindow = new PopupWindow(contentView,200,300);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), Bitmap.createBitmap(200, 300, Bitmap.Config.ALPHA_8)));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(v, -40, 20);

        Toast.makeText(this,strs+"",Toast.LENGTH_SHORT).show();
        PopupWindowContentAdapter adapter = new PopupWindowContentAdapter(this, strs);
        lv_content.setAdapter(adapter);
        lv_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //搜索条件保存到本地
                mPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString("cc_code_" + tag, position + "");
                editor.putString("cc_" + tag, strs.get(position));
                editor.apply();
                textView.setText(strs.get(position));
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 0.7f;
        getWindow().setAttributes(params);

        popupWindow.update();
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            public void onDismiss() {
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.alpha = 1f;
                getWindow().setAttributes(params);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cc_tv_place:
                showPopupWindowArea(InitData.kkdd_list, cc_place, v,"place");
                break;
            case R.id.cc_tv_fxhb:
                showPopupWindowArea(InitData.fxbh_list, cc_fxbh, v, "fxbh");
                break;
            case R.id.cc_tv_control:
                try{
                    cc_control.setEnabled(false);
                    mintent.putExtra("user_id", user_id);
                    mintent.putExtra("cc_code_place",mPreferences.getString("cc_code_place", "0"));
                    mintent.putExtra("cc_code_fxbh",mPreferences.getString("cc_code_fxbh", "0"));
                    mintent.putExtra("cc_hphm",cc_et_hphm.getText().toString());
                    startService(mintent);
                }catch (Exception e){e.printStackTrace();}
                break;
            case R.id.cc_back:
                finish();
                break;
        }
    }

}
