package com.sx.kakou.tricks;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sx_kakou.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by mglory on 2015/8/13.
 */
public class ErrorLogAdapter extends RecyclerView.Adapter<ErrorLogAdapter.ViewHolder> {

    private String []list;
    private MyItemClickListener mItemClickListener;
    private MyLongClickListener mLongClickListener;
    public String[] getDataList(){
        return  list;
    }
    public void setDataList(String[] list){
        this.list = list;
    }

    public ErrorLogAdapter() {
    }



    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        public View rootView;
        public TextView err_list;
        public int position;
        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.card_view);
            err_list = (TextView)rootView.findViewById(R.id.tv_err_log);
            rootView.setOnClickListener(this);
            rootView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener!=null){
                mItemClickListener.OnItemClick(getPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mLongClickListener !=null){
                mLongClickListener.OnLongClick(getPosition());
            }
            return true;
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)  {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_err_log, parent, false);
        return new ViewHolder(view);
    }


    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public void setOnLongClickListener(MyLongClickListener listener){
        this.mLongClickListener = listener;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //这里更新数据
        try{
            holder.err_list.setText(list[position]);
        }catch(Exception e){
        e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    public interface MyItemClickListener{
         void OnItemClick(int position);
    }

    public interface  MyLongClickListener{
        void OnLongClick(int position);
    }


}
