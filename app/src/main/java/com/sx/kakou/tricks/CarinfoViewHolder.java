package com.sx.kakou.tricks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.sx_kakou.R;

import org.json.JSONObject;

/**
 * Created by mglory on 2015/7/31.
 */
public class CarinfoViewHolder extends RecyclerView.ViewHolder {
    public TextView tv_tag;
    public TextView tv_value;

    public  View rootView;
    public  int position;

    public CarinfoViewHolder(View itemView) {
       super(itemView);
        tv_tag = (TextView)itemView.findViewById(R.id.carinfo_name);
        tv_value = (TextView)itemView.findViewById(R.id.carinfo_value);


        rootView = itemView.findViewById(R.id.carinfo_view);
    }
}
