package com.sx.kakou.view;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sx_kakou.R;
import com.sx.kakou.util.FileHelper;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ErrorlogActivity extends Activity implements OnClickListener{
    private static LinearLayout err_log_back;
    private static TextView tv_err_list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_errorlog);
        err_log_back = (LinearLayout)findViewById(R.id.errlog_back);
        tv_err_list = (TextView)findViewById(R.id.tv_errlog);
        err_log_back.setOnClickListener(this);

        String filename = getIntent().getStringExtra("FileName");
        FileHelper helper = new FileHelper(this);
        String errMsg = helper.readSDFile("/CrashInfos/"+filename);
        tv_err_list.setText(errMsg);
	}

    public void ClearErrorLog(){

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.errlog_back:
                finish();
                break;
        }
    }


}
