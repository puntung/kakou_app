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
    private MyItemClickListener mItemClickListener;
    public JsonArray getDataList(){
        return  marray;
    }
    public RecyclerViewAdapter(Context context, PullLoadMoreRecyclerView pullLoadMoreRecyclerView,JsonArray marray) {
        this.marray = marray;
        mContext = context;
        imageLoader = ImageLoader.getInstance();
        mPullLoadMoreRecyclerView = pullLoadMoreRecyclerView;
    }

    public void setMyItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public View rootView;
        public ImageView carinfo_img;
        public TextView hphm;
        public TextView jgsj;
        public TextView cllx;
        public TextView clpp;
        public TextView csys;
        public TextView hpys;
        public int position;
        public ViewHolder(View itemView,MyItemClickListener listener) {
            super(itemView);
            carinfo_img = (ImageView) itemView.findViewById(R.id.h_catinfo_img);
            hphm = (TextView)itemView.findViewById(R.id.h_hphm);
            jgsj = (TextView)itemView.findViewById(R.id.h_jgsj);
            cllx = (TextView)itemView.findViewById(R.id.h_cllx);
            clpp = (TextView)itemView.findViewById(R.id.h_clpp);
            csys = (TextView)itemView.findViewById(R.id.h_csys);
            hpys = (TextView)itemView.findViewById(R.id.h_hpys);
            rootView = itemView.findViewById(R.id.card_view);
            rootView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener!=null){
                mItemClickListener.OnItemClick(getPosition());
            }
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)  {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_hsty_carinfo, parent, false);
        return new ViewHolder(view,mItemClickListener);
    }


    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //这里更新数据
        try{
            JSONObject mobject = new JSONObject(marray.get(position).toString());
            imageLoader.displayImage(mobject.getString("imgurl"),holder.carinfo_img);
            holder.hphm.setText(mobject.getString("hphm"));
            holder.jgsj.setText(mobject.getString("jgsj").substring(0,10));
            holder.cllx.setText(mobject.getString("cllx"));
            holder.clpp.setText(mobject.getString("clpp"));
            holder.csys.setText(mobject.getString("csys"));
            holder.hpys.setText(mobject.getString("hpys"));
        }catch(Exception e){
        e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return marray.size();
    }

    public interface MyItemClickListener{
         void OnItemClick(int position);
    }
}
