package com.sx.kakou.tricks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sx_kakou.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.square.github.restrofit.Constants;
import com.square.github.restrofit.KakouClient;
import com.square.github.restrofit.ServiceGenerator;
import com.squareup.okhttp.Response;
import com.sx.kakou.view.HistoryItemActivity;
import com.sx.kakou.view.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by mglory on 2015/9/8.
 */
public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private JSONArray array;
    private int count;
    private LayoutInflater inflater;
    private SharedPreferences mPreference;
    public ViewPagerAdapter(Context context, JSONArray array,int count) {
        this.context = context;
        this.array = array;
        this.count = count;
        inflater = LayoutInflater.from(context);
        mPreference = context.getSharedPreferences("config", Context.MODE_PRIVATE);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }
    @Override
    public int getCount() {
        return array != null ? array.length():0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        //自定义的view
        View itemLayout = inflater.inflate(R.layout.viewpager_history_item, view, false);
        ImageView car_img = (ImageView)itemLayout.findViewById(R.id.ah_carinfo_img_hd);
        RecyclerView cgs_cys = (RecyclerView)itemLayout.findViewById(R.id.ah_cgs_rcy);
        RecyclerView sb_cys = (RecyclerView)itemLayout.findViewById(R.id.ah_sb_rcy);
        //填充数据
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(context);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(context);
        cgs_cys.setHasFixedSize(true);
        sb_cys.setHasFixedSize(true);
        cgs_cys.setLayoutManager(layoutManager1);
        sb_cys.setLayoutManager(layoutManager2);
        try {
            JSONObject object = new JSONObject(array.get(position).toString());
            String url = object.getString("imgurl");
            DisplayImageOptions options = getImageLoaderOpt();
            ImageLoader loader = ImageLoader.getInstance();
            loader.displayImage(url, car_img, options);
            String carinfo_tag[] = context.getResources().getStringArray(R.array.catinfo_label_en);
            String carinfo_value[] = context.getResources().getStringArray(R.array.catinfo_label_cn);
            CarinfoAdapter carinfoAdapter = new CarinfoAdapter(context,object,carinfo_tag,carinfo_value);
            sb_cys.setAdapter(carinfoAdapter);
            getCgsInfo(cgs_cys, object.getString("hphm"));
        }catch (Exception e){
            e.printStackTrace();
        }
        ((ViewPager) view).addView(itemLayout, 0);
        return itemLayout;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {}
    @Override
    public Parcelable saveState() {
        return null;
    }
    @Override
    public void startUpdate(View container) {

    }

        //获取车管所信息
    public void getCgsInfo(final RecyclerView sb_cys, String qs){
        KakouClient client = ServiceGenerator.createService(KakouClient.class, Constants.BASE_URL, "kakou", "pingworker");
        client.getinfo(qs, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, retrofit.client.Response response) {
                String cgs_tag[] = context.getResources().getStringArray(R.array.cgs_label_en);
                String cgs_value[] =context.getResources().getStringArray(R.array.cgs_label_cn);
                try {
                    JSONObject cgsinfo = new JSONObject(jsonObject.toString());
                    if (cgsinfo.getInt("total_count") == 0) {
                        cgs_tag = null;
                    }
                    JSONArray array = new JSONArray(cgsinfo.getString("items"));
                    JSONObject itemobject = new JSONObject(array.get(0).toString());
                    CarinfoAdapter cgsinfoAdapter = new CarinfoAdapter(context,itemobject, cgs_tag, cgs_value);
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
        //加载更多信息
        public void getCarinfosList(final int mCount) {
        KakouClient client = ServiceGenerator.createService(KakouClient.class,Constants.BASE_URL);
        String  place = mPreference.getString("kakou_place_code","");
        String  fxbh = mPreference.getString("kakou_fxbh_code", "");
        String  hpys = mPreference.getString("kakou_hpys", "").substring(0, 1);
        final String  st = mPreference.getString("kakou_st", "");
        String et = mPreference.getString("kakou_et", "");
        String queryStr = "粤LD%+st:"+st+"+et:"+et+"+place:"+place+"+fxbh:"+fxbh+"+hpys:"+hpys+"+ppdm:114&page=" + mCount + "&per_page=20&sort=ppdm&order=desc";
        client.getCarInfosList(queryStr, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, retrofit.client.Response response) {
                JsonArray iarray = jsonObject.get("items").getAsJsonArray();
                if (iarray.size()>0){

                }else {
                    Toast.makeText(context,"没有新数据", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
            }
        });
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
