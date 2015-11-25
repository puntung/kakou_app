package com.sx.kakou.tricks;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sx_kakou.R;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.square.github.restrofit.Constants;
import com.square.github.restrofit.KakouClient;
import com.square.github.restrofit.ServiceGenerator;
import com.sx.kakou.util.InitData;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by mglory on 2015/9/8.
 */
public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private JSONArray array;
    private LayoutInflater inflater;
    public ViewPagerAdapter(Context context, JSONArray array) {
        this.context = context;
        this.array = array;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
    @Override
    public int getCount() {
        return array == null ? 0:array.length();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        //自定义的view
        View itemLayout = inflater.inflate(R.layout.viewpager_history_item, view, false);
        final ProgressBar spinner = (ProgressBar) itemLayout.findViewById(R.id.loading);
        ImageView car_img = (ImageView)itemLayout.findViewById(R.id.ah_photoview_carinfo);
        RecyclerView cgs_cys = (RecyclerView)itemLayout.findViewById(R.id.ah_cgs_rcy);
        RecyclerView sb_cys = (RecyclerView)itemLayout.findViewById(R.id.ah_sb_rcy);
        TextView nodata_tv = (TextView)itemLayout.findViewById(R.id.ah_cgs_nodata_tv);
        //填充数据
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(context);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(context);
        cgs_cys.setHasFixedSize(true);
        sb_cys.setHasFixedSize(true);
        cgs_cys.setLayoutManager(layoutManager1);
        sb_cys.setLayoutManager(layoutManager2);
        try {
            JSONObject object = new JSONObject(array.get(position).toString());
            //System.out.println(object.toString());
            String thumb_url = object.getString("thumb_url");
            String qs = object.getString("hphm")+"+hpys:"+object.getString("hpys").substring(0,1);
            String url = object.getString("imgurl");
            ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
            DisplayImageOptions options = getImageLoaderOpt();
            //PhotoViewAttacher mAttacher = new PhotoViewAttacher(car_img);
            String carinfo_tag[] = context.getResources().getStringArray(R.array.catinfo_label_en);
            String carinfo_value[] = context.getResources().getStringArray(R.array.catinfo_label_cn);
            CarinfoAdapter carinfoAdapter = new CarinfoAdapter(context,object,carinfo_tag,carinfo_value,Constants.TAG_SB);
            sb_cys.setAdapter(carinfoAdapter);
            getCgsInfo(cgs_cys,nodata_tv,qs);
            imageLoader.displayImage(url, car_img, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    spinner.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    String message = null;
                    switch (failReason.getType()) {     // 获取图片失败类型
                        case IO_ERROR:              // 文件I/O错误
                            message = "Input/Output error";
                            break;
                        case DECODING_ERROR:        // 解码错误
                            message = "Image can't be decoded";
                            break;
                        case NETWORK_DENIED:        // 网络延迟
                            message = "Downloads are denied";
                            break;
                        case OUT_OF_MEMORY:         // 内存不足
                            message = "Out Of Memory error";
                            break;
                        case UNKNOWN:               // 原因不明
                            message = "Unknown error";
                            break;
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    spinner.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    spinner.setVisibility(View.GONE);       // 不显示圆形进度条
                }
            });
          //  mAttacher.update();
        }catch (Exception e){
            e.printStackTrace();
        }
       view.addView(itemLayout, 0);
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
    public void getCgsInfo(final RecyclerView sb_cys,final TextView tv, final String qs){
        // LruCache缓存机制
        String data = InitData.lcu.getJsonLruCache(qs);
        if(data != null){
            try {
                String cgs_tag[] = context.getResources().getStringArray(R.array.cgs_label_en);
                String cgs_value[] =context.getResources().getStringArray(R.array.cgs_label_cn);
                JSONObject cgsinfo = new JSONObject(data);
                if (cgsinfo.getInt("total_count") != 0) {
                    JSONArray array = new JSONArray(cgsinfo.getString("items"));
                    JSONObject itemobject = new JSONObject(array.get(0).toString());
                    CarinfoAdapter cgsinfoAdapter = new CarinfoAdapter(context,itemobject, cgs_tag, cgs_value,Constants.TAG_CGS);
                    sb_cys.setAdapter(cgsinfoAdapter);
                    cgsinfoAdapter.notifyDataSetChanged();
                }else {
                    tv.setVisibility(View.VISIBLE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            KakouClient client = ServiceGenerator.createService(KakouClient.class, Constants.BASE_URL, Constants.CGS_USER, Constants.CGS_PWD);
            client.getinfo(qs, new Callback<JsonObject>() {
                @Override
                public void success(JsonObject jsonObject, retrofit.client.Response response) {
                    InitData.lcu.addJsonLruCache(qs,jsonObject.toString());
                    String cgs_tag[] = context.getResources().getStringArray(R.array.cgs_label_en);
                    String cgs_value[] =context.getResources().getStringArray(R.array.cgs_label_cn);
                    try {
                        JSONObject cgsinfo = new JSONObject(jsonObject.toString());
                        if (cgsinfo.getInt("total_count") != 0) {
                            JSONArray array = new JSONArray(cgsinfo.getString("items"));
                            JSONObject itemobject = new JSONObject(array.get(0).toString());
                            CarinfoAdapter cgsinfoAdapter = new CarinfoAdapter(context,itemobject, cgs_tag, cgs_value,Constants.TAG_CGS);
                            sb_cys.setAdapter(cgsinfoAdapter);
                            cgsinfoAdapter.notifyDataSetChanged();
                        }
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
    }

    /*
    * Imageloader的配置
    *
    * */
    public DisplayImageOptions getImageLoaderOpt(){
        return new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.board_gray) //加载时显示的页面
                .showImageForEmptyUri(R.drawable.image_notfound)
                .showImageOnFail(R.drawable.image_notfound)
                //.delayBeforeLoading(400) //设置下载前延时时间
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .displayer(new FadeInBitmapDisplayer(100)) // 设置加载后渐入动画时间
                .build();
    }
}
