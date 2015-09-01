package com.sx.kakou.tricks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.sx_kakou.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sx.kakou.model.SmplCarInfoViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RealTimeCarInfoAdapter extends BaseAdapter{
	
	public static Context mContext= null;
	private JSONArray mJSONArray = null;
	private boolean mBusy = false; 
	private ImageLoader imageLoader;
	public void setFlagBusy(boolean busy) { 
	this.mBusy = busy; 
	} 
	
	public RealTimeCarInfoAdapter(Context mContext, JSONArray mJSONArray) {
		super();
		this.mContext = mContext;
		this.mJSONArray = mJSONArray;
		imageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() { 
		// TODO Auto-generated method stub
		int mcount = 0;
		if (mJSONArray.length()>20) {
			mcount = 20;
		}else {
			mcount = mJSONArray.length();
		}
		return mcount;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//缓存机制，对view的重用
		SmplCarInfoViewHolder holder = null;
		if (convertView == null) {
			System.out.println(mContext==null);
			convertView = LayoutInflater.from(mContext).inflate(R.layout.list_rt_carinfo,null);
			holder = new SmplCarInfoViewHolder();
			holder.carinfo_img = (ImageView)convertView.findViewById(R.id.catinfo_img);
			holder.hphm = (TextView)convertView.findViewById(R.id.hphm);
			holder.jgsj = (TextView)convertView.findViewById(R.id.jgsj);
			holder.place = (TextView)convertView.findViewById(R.id.place);
			holder.fxbh = (TextView)convertView.findViewById(R.id.fxbh);
			convertView.setTag(holder);
		}else {
			holder  = (SmplCarInfoViewHolder)convertView.getTag();
		}
		if (!mBusy) {
			DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.board_gray) //加载时显示的页面
			.showImageForEmptyUri(R.drawable.board_gray)
			.showImageOnFail(R.drawable.board_gray)
			 .delayBeforeLoading(100)
			.cacheInMemory(true)
			.cacheOnDisc(false)
			.build();
			JSONObject object;
			try {
				
				object = new JSONObject(mJSONArray.get(position).toString());
				String url = object.getString("imgurl");
				imageLoader.displayImage(url, holder.carinfo_img,options,null);
				holder.hphm.setText(object.getString("hphm"));
				holder.jgsj.setText(object.getString("jgsj"));
				holder.place.setText(object.getString("place"));
				holder.fxbh.setText(object.getString("fxbh"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			System.out.println("Busy");
		}
		
		return convertView;
	}

}
