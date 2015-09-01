package com.sx.kakou.tricks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.sx_kakou.R;

import java.util.List;

/**
 * Created by mglory on 2015/8/17.
 */
public class PopupWindowContentAdapter  extends BaseAdapter {
    private LayoutInflater inflater;
    private Holder holder;
    private List<String> strs;
    private Context context;

    private int position = 0;

    public PopupWindowContentAdapter(Context context, List<String> strs) {
        super();
        this.strs = strs;
        this.context = context;
    }
    @Override
    public int getCount() {
        return strs.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            inflater = LayoutInflater.from(context);
            holder = new Holder();
            convertView = inflater.inflate(R.layout.list_pop_item, null);
            holder.tv_popupwindow_text = (TextView) convertView
                    .findViewById(R.id.tv_popupwindow_text);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.tv_popupwindow_text.setText(strs.get(position).toString());
        return convertView;
    }

    class Holder {
        TextView tv_popupwindow_text;
    }
}
