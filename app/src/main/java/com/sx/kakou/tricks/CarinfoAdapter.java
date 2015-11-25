package com.sx.kakou.tricks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.example.sx_kakou.R;
import com.google.gson.JsonObject;
import com.square.github.restrofit.Constants;
import com.sx.kakou.util.InitData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mglory on 2015/7/31.
 */
public class CarinfoAdapter extends RecyclerView.Adapter {
    private Context mContext = null;
    private JSONObject json = null;
    private String[] tag_array;
    private String[] value_array;
    private int tag;

    public CarinfoAdapter(Context mContext,JSONObject json,String[] tag_array,String[] value_array,int tag){
        this.mContext = mContext;
        this.json = json;
        this.tag_array = tag_array;
        this.value_array = value_array;
        this.tag = tag;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyc_carinfo_item,null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(layoutParams);
        return new CarinfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            //渲染
            CarinfoViewHolder mHolder = (CarinfoViewHolder)holder;
            mHolder.position = position;
            mHolder.tv_tag.setText(value_array[position]);
            try{
               System.out.println(json.toString());
                    if (tag_array!=null){
                        if (tag == Constants.TAG_CGS){
                            //替换
                            if (tag_array[position].equals("csys")){
                                String key = json.getString("csys");
                                mHolder.tv_value.setText(InitData.csysmap.get(key));
                            }else if (tag_array[position].equals("hpzl")){
                                String key = json.getString("hpzl");
                                mHolder.tv_value.setText(InitData.hpzlmap.get(key));
                            }else if (tag_array[position].equals("cllx")){
                                String key = json.getString("cllx");
                                mHolder.tv_value.setText(InitData.cllxmap.get(key));
                            }else {
                                mHolder.tv_value.setText(json.getString(tag_array[position]));
                            }
                        }else {
                            mHolder.tv_value.setText(json.getString(tag_array[position]));
                        }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
    }

    @Override
    public int getItemCount() {
        return value_array.length ;
    }
}
