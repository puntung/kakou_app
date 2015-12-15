package com.sx.kakou.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.example.sx_kakou.R;
import com.square.github.restrofit.Constants;
import com.sx.kakou.tricks.ErrorLogAdapter;
import com.sx.kakou.util.FileHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorlistActivity extends Activity implements OnClickListener{
    private static LinearLayout err_log_back;
    private static RecyclerView r_err_list;
    private static String []list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_errorlist);
        err_log_back = (LinearLayout)findViewById(R.id.errlog_back);
        r_err_list = (RecyclerView)findViewById(R.id.recycler_errlog);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        r_err_list.setLayoutManager(manager);
        err_log_back.setOnClickListener(this);
        FileHelper helper = new FileHelper(this);
        if (helper.hasSD()){
            list = helper.readSDFileList(Constants.ERR_LOG_URL);
            if (list.length>0){
                ErrorLogAdapter adapter = new ErrorLogAdapter();
                adapter.setDataList(list);
                r_err_list.setAdapter(adapter);
                setOnItemClickListener(adapter);
                setOnLongClickListener(adapter);
            }

        }
	}

    public  void setOnItemClickListener(ErrorLogAdapter adapter){
        adapter.setOnItemClickListener(new ErrorLogAdapter.MyItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                Intent intent = new Intent(ErrorlistActivity.this,ErrorlogActivity.class);
                intent.putExtra("FileName",list[position]);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    public boolean setOnLongClickListener(final ErrorLogAdapter adapter){
        adapter.setOnLongClickListener(new ErrorLogAdapter.MyLongClickListener() {
            @Override
            public void OnLongClick(final int position) {
                String []atr = new String[]{"删除","取消"};
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ErrorlistActivity.this);
                alertDialog.setItems(atr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            FileHelper helper = new FileHelper(ErrorlistActivity.this);
                            String[] list = helper.readSDFileList(Constants.ERR_LOG_URL);
                            System.out.println("del"+Constants.ERR_LOG_URL + "/" + list[position]);
                            helper.deleteSDFile(Constants.ERR_LOG_URL + "/" + list[position]);
                            list = helper.readSDFileList(Constants.ERR_LOG_URL);
                            adapter.setDataList(list);
                            adapter.notifyDataSetChanged();
                        } else {
                            dialog.dismiss();
                        }
                    }
                });
                alertDialog.show();
            }
        });
        return true;
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
