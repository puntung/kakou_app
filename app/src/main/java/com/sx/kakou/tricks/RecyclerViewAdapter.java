package com.sx.kakou.tricks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sx_kakou.R;
import com.google.gson.JsonArray;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Random;

/**
 * Created by mglory on 2015/8/13.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private JsonArray marray;
    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    ImageLoader imageLoader;

    public JsonArray getDataList(){
        return  marray;
    }
    public RecyclerViewAdapter(Context context, PullLoadMoreRecyclerView pullLoadMoreRecyclerView,JsonArray marray) {
        this.marray = marray;
        mContext = context;
        imageLoader = ImageLoader.getInstance();
        mPullLoadMoreRecyclerView = pullLoadMoreRecyclerView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView carinfo_img;
        public TextView hphm;

        public ViewHolder(View itemView) {
            super(itemView);
            carinfo_img = (ImageView) itemView.findViewById(R.id.h_catinfo_img);
            hphm = (TextView)itemView.findViewById(R.id.h_hphm);
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_hsty_carinfo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //这里更新数据
        try{
            JSONObject mobject = new JSONObject(marray.get(position).toString());
            //imageLoader.displayImage(mobject.getString("imgurl"),holder.carinfo_img);
            holder.hphm.setText(mobject.getString("hphm"));
        }catch(Exception e){
        e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return marray.size();
    }
    public int getViewHeight(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return view.getMeasuredHeight();
    }
}
