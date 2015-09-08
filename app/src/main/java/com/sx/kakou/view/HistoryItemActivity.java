package com.sx.kakou.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sx_kakou.R;
import com.sx.kakou.tricks.ViewPagerAdapter;
import org.json.JSONArray;

public class HistoryItemActivity extends Activity implements View.OnClickListener{

    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private static   TextView back;
    private static String intentdata;
    private static  int count;
    private static int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_history_item);
        init();
        System.out.println(position);
    }

    public void init(){
        intentdata = getIntent().getStringExtra("data");
        position = getIntent().getIntExtra("position",-1);
        count = getIntent().getIntExtra("count", -1);
        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        try {
            JSONArray dataArray = new JSONArray(intentdata);
            mViewPagerAdapter = new ViewPagerAdapter(this,dataArray,count);
        }catch (Exception e){
            e.printStackTrace();
        }
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setCurrentItem(position);
        back = (TextView)findViewById(R.id.ah_back);
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
