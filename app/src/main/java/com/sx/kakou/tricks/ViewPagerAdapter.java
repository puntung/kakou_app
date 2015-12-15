package com.sx.kakou.tricks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sx_kakou.R;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.square.github.restrofit.Constants;
import com.square.github.restrofit.KakouClient;
import com.square.github.restrofit.ServiceGenerator;
import com.sx.kakou.util.Global;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by mglory on 2015/9/8.
 */
public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private JSONArray array;
    private LayoutInflater inflater;
    private int count;
    public ViewPagerAdapter(Context context, JSONArray array,int count) {
        this.context = context;
        this.array = array;
        this.count = count;
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
            final   RoundProgressBar pb = (RoundProgressBar)itemLayout.findViewById(R.id.loading);
            PhotoView car_img = (PhotoView)itemLayout.findViewById(R.id.ah_photoview_carinfo);
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
                String thumb_url = object.getString("thumb_url");
                String qs = object.getString("hphm")+"+hpys:"+object.getString("hpys").substring(0,1);
                String url = object.getString("imgurl");
                DisplayImageOptions options = getImageLoaderOpt(thumb_url);
               // DisplayImageOptions bigoptions = getImageLoaderOptNoPreLoad();
                //PhotoViewAttacher mAttacher = new PhotoViewAttacher(car_img);
                String carinfo_tag[] = context.getResources().getStringArray(R.array.catinfo_label_en);
                String carinfo_value[] = context.getResources().getStringArray(R.array.catinfo_label_cn);
                CarinfoAdapter carinfoAdapter = new CarinfoAdapter(context,object,carinfo_tag,carinfo_value,Constants.TAG_SB);
                sb_cys.setAdapter(carinfoAdapter);
                getCgsInfo(cgs_cys, nodata_tv, qs);
                ImageLoader.getInstance().displayImage(url, car_img, options, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        super.onLoadingStarted(imageUri, view);
                        pb.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        pb.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        super.onLoadingFailed(imageUri, view, failReason);
                        pb.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        super.onLoadingCancelled(imageUri, view);
                        pb.setVisibility(View.GONE);
                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {
                        pb.setMax(total);
                        pb.setProgress(current);
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
        String data = Global.lcu.getJsonLruCache(qs);
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
                    //cgsinfoAdapter.notifyDataSetChanged();
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
                    Global.lcu.addJsonLruCache(qs,jsonObject.toString());
                    String cgs_tag[] = context.getResources().getStringArray(R.array.cgs_label_en);
                    String cgs_value[] =context.getResources().getStringArray(R.array.cgs_label_cn);
                    try {
                        JSONObject cgsinfo = new JSONObject(jsonObject.toString());
                        if (cgsinfo.getInt("total_count") != 0) {
                            JSONArray array = new JSONArray(cgsinfo.getString("items"));
                            JSONObject itemobject = new JSONObject(array.get(0).toString());
                            CarinfoAdapter cgsinfoAdapter = new CarinfoAdapter(context,itemobject, cgs_tag, cgs_value,Constants.TAG_CGS);
                            sb_cys.setAdapter(cgsinfoAdapter);
                            //cgsinfoAdapter.notifyDataSetChanged();
                        }else {
                            tv.setVisibility(View.VISIBLE);
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
    public DisplayImageOptions getImageLoaderOpt(String url){
        //Bitmap bp = ImageLoader.getInstance().loadImageSync(url);
        final BitmapFactory.Options bo = new BitmapFactory.Options();
        bo.inJustDecodeBounds = true;
        bo.inSampleSize = calculateInSampleSize(bo,625,500);
        bo.inJustDecodeBounds = false;
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.skin_loading_icon) //加载时显示的页面
                //.showImageOnLoading(new BitmapDrawable(bp))
                .showImageForEmptyUri(R.drawable.image_notfound)
                .showImageOnFail(R.drawable.image_notfound)
                //.delayBeforeLoading(400) //设置下载前延时时间
                .bitmapConfig(Bitmap.Config.RGB_565)
                .decodingOptions(bo)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(100)) // 设置加载后渐入动画时间
                .build();
    }
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
}
