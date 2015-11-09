package com.sx.kakou.tricks;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sx_kakou.R;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by mglory on 2015/8/13.
 */
public class RealTimeFreshAdapter extends RecyclerView.Adapter<RealTimeFreshAdapter.ViewHolder> {

    private JsonArray marray;
    private MyItemClickListener mItemClickListener;
    public JsonArray getDataList(){
        return  marray;
    }
    public void setDataList(JsonArray jsonArray){
        this.marray = jsonArray;
    }

    public RealTimeFreshAdapter() {
    }



    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public View rootView;
        public TextView rt_ct_hphm;
        public TextView rt_ct_place;
        public TextView rt_ct_fxbh;
        public TextView rt_ct_jgsj;
        public int position;
        public ViewHolder(View itemView) {
            super(itemView);
            rt_ct_hphm = (TextView)itemView.findViewById(R.id.rt_ct_hphm);
            rt_ct_place = (TextView)itemView.findViewById(R.id.rt_ct_place);
            rt_ct_fxbh = (TextView)itemView.findViewById(R.id.rt_ct_fxbh);
            rt_ct_jgsj = (TextView)itemView.findViewById(R.id.rt_ct_jgsj);
            rootView = itemView.findViewById(R.id.rt_ct_card_view);
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
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_rt_carinfo, parent, false);
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
            holder.rt_ct_hphm.setText(mobject.getString("hphm"));
            holder.rt_ct_place.setText(mobject.getString("place"));
            holder.rt_ct_fxbh.setText(mobject.getString("fxbh"));
            holder.rt_ct_jgsj.setText(mobject.getString("jgsj"));
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
