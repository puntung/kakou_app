package com.sx.kakou.tricks;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sx_kakou.R;
import com.google.gson.JsonArray;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by mglory on 2015/8/13.
 */
public class ControlViewAdapter extends RecyclerView.Adapter<ControlViewAdapter.ViewHolder> {

    private JSONArray marray;
    private MyItemClickListener mItemClickListener;
    public JSONArray getDataList(){
        return  marray;
    }
    public void setDataList(JSONArray jsonArray){
        this.marray = jsonArray;
    }

    public ControlViewAdapter() {
    }



    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public View rootView;
        public TextView control_hphm;
        public TextView control_info;
        public TextView control_place;
        public TextView control_fxbh;
        public ImageView control_img;
        public int position;
        public ViewHolder(View itemView) {
            super(itemView);
            control_hphm = (TextView)itemView.findViewById(R.id.control_hphm);
            control_info = (TextView)itemView.findViewById(R.id.control_info);
            control_place = (TextView)itemView.findViewById(R.id.control_place);
            control_fxbh = (TextView)itemView.findViewById(R.id.control_fxbh);
            control_img = (ImageView)itemView.findViewById(R.id.control_img);
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
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_rt_control, parent, false);
        return new ViewHolder(view);
    }


    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //这里更新数据
        try{
            JSONObject mobject = new JSONObject(marray.get(position).toString());
            holder.control_hphm.setText(mobject.getString("control_hphm"));
            holder.control_info.setText(mobject.getString("control_info"));
            holder.control_place.setText(mobject.getString("control_place"));
            holder.control_fxbh.setText(mobject.getString("control_fxbh"));
            //holder.control_img
        }catch(Exception e){
        e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return marray.length();
    }

    public interface MyItemClickListener{
         void OnItemClick(int position);
    }

}
