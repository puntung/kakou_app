package com.sx.kakou.view;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.sx_kakou.R;
import com.sx.kakou.tricks.ViewPagerAdapter;
import com.sx.kakou.tricks.ViewPagerFixed;

import org.json.JSONArray;

public class HistoryItemActivity extends Activity implements View.OnClickListener{

    private ViewPagerFixed mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private static LinearLayout back;
    private static String intentdata;
    private static  int count;
    private static int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_history_item);
        init();
    }

    public void init(){
        intentdata = getIntent().getStringExtra("data");
        position = getIntent().getIntExtra("position", -1);
        count = getIntent().getIntExtra("count", -1);
        mViewPager = (ViewPagerFixed)findViewById(R.id.viewpager);
        try {
            JSONArray dataArray = new JSONArray(intentdata);
            mViewPagerAdapter = new ViewPagerAdapter(this,dataArray,count);
        }catch (Exception e){
            e.printStackTrace();
        }
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setCurrentItem(position);
        back = (LinearLayout)findViewById(R.id.ah_back);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ah_back:
                finish();
                break;
        }
    }

}
