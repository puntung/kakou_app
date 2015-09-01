package com.sx.kakou.tricks;

import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.example.sx_kakou.R;
import com.sx.kakou.wheelview.NumericWheelAdapter;
import com.sx.kakou.wheelview.OnWheelChangedListener;
import com.sx.kakou.wheelview.WheelView;


/**
 * @auther:summer 时间： 2012-7-19 下午2:59:56
 */
public class MyDateTimePickerDialog extends AlertDialog implements
		OnClickListener{
	private static int START_YEAR = 1700,END_YEAR=2700;
	private final OnDateTimeSetListener mCallBack;
	private int curr_year, curr_month, curr_day, curr_hour, curr_minute;
	// 添加大小月月份并将其转换为list,方便之后的判断
	String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
	String[] months_little = { "4", "6", "9", "11" };
	final WheelView wv_year, wv_month, wv_day;
	final List<String> list_big, list_little;

	public MyDateTimePickerDialog(Context context,
			OnDateTimeSetListener callBack,int year,int month,int day) {
		this(context, START_YEAR,END_YEAR,callBack,year,month,day);
	}

	public MyDateTimePickerDialog(Context context, final int START_YEAR,
			 final int END_YEAR,OnDateTimeSetListener callBack, final int year, final int month, final int day) {
		super(context);
		this.START_YEAR = START_YEAR;
		this.END_YEAR = END_YEAR;
		mCallBack = callBack;
		list_big = Arrays.asList(months_big);
		list_little = Arrays.asList(months_little);
	     setButton(context.getText(R.string.ok),this);
	     setButton2(context.getText(R.string.cancle), (OnClickListener) null);
		setTitle("日期选择");
		// 找到dialog的布局文件
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.time_layout, null);
      
		int textSize = 0;
		textSize = adjustFontSize(getWindow().getWindowManager()); 
		// 年
		wv_year = (WheelView) view.findViewById(R.id.year);
		wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
		wv_year.setCyclic(true);// 可循环滚动
		wv_year.setLabel("年");// 添加文字
		wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

		// 月
		wv_month = (WheelView) view.findViewById(R.id.month);
		wv_month.setAdapter(new NumericWheelAdapter(1, 12));
		wv_month.setCyclic(true);
		wv_month.setLabel("月");
		wv_month.setCurrentItem(month);

		// 日
		wv_day = (WheelView) view.findViewById(R.id.day);
		wv_day.setCyclic(true);
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (list_big.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 31));
		} else if (list_little.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 30));
		} else {
			// 闰年
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				wv_day.setAdapter(new NumericWheelAdapter(1, 29));
			else
				wv_day.setAdapter(new NumericWheelAdapter(1, 28));
		}
		wv_day.setLabel("日");
		wv_day.setCurrentItem(day - 1);

//		// 时
//		wv_hours = (WheelView) view.findViewById(R.id.hour);
//		wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
//		wv_hours.setCyclic(true);
//		wv_hours.setCurrentItem(hour);
//
//		// 分
//		wv_mins = (WheelView) view.findViewById(R.id.mins);
//		wv_mins.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
//		wv_mins.setCyclic(true);
//		wv_mins.setCurrentItem(minute);
		// 添加"年"监听
		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int year_num = newValue + START_YEAR;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big
						.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(wv_month
						.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if ((year_num % 4 == 0 && year_num % 100 != 0)
							|| year_num % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
		};
		// 添加"月"监听
		OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int month_num = newValue + 1;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year
							.getCurrentItem() + START_YEAR) % 100 != 0)
							|| (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
		};
		wv_year.addChangingListener(wheelListener_year);
		wv_month.addChangingListener(wheelListener_month);
		wv_day.TEXT_SIZE = textSize;
		wv_month.TEXT_SIZE = textSize;
		wv_year.TEXT_SIZE = textSize;
		setView(view);
	}
	public void onClick(DialogInterface dialog, int which) {

		curr_year = wv_year.getCurrentItem() + START_YEAR;
		curr_month = wv_month.getCurrentItem() + 1;
		curr_day = wv_day.getCurrentItem() + 1;
		if (mCallBack != null) {
			mCallBack.onDateTimeSet(curr_year, curr_month, curr_day);
		}
	}
	 public void show() {
	        super.show();
	 }
	public  interface OnDateTimeSetListener {
		void onDateTimeSet(int year, int monthOfYear, int dayOfMonth);
	}
	public static int adjustFontSize(WindowManager windowmanager) {

		 int screenWidth = windowmanager.getDefaultDisplay().getWidth();
	     int screenHeight = windowmanager.getDefaultDisplay().getHeight();
		if (screenWidth <= 240) { // 240X320 屏幕
			return 20;
		} else if (screenWidth <= 320) { // 320X480 屏幕
			return 24;
		} else if (screenWidth <= 480) { // 480X800 或 480X854 屏幕
			return 34;
		} else if (screenWidth <= 540) { // 540X960 屏幕
			return 36;
		} else if (screenWidth <= 800) { // 800X1280 屏幕
			return 40;
		} else { // 大于 800X1280
			return 40;
		}
	}
}

