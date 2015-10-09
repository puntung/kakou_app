package com.sx.kakou.tricks;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.square.github.restrofit.Constants;
import com.square.github.restrofit.KakouClient;
import com.square.github.restrofit.ServiceGenerator;
import com.sx.kakou.view.FragmentRealTime;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ControlService extends Service {
    private JsonArray array = null;
    private Bundle bundle = null;
    private Intent intent = null;
    private int index = 0;
    private boolean isLinster = true;
    @Override
    public void onCreate() {
        System.out.println("开启监听服务");
        array = new JsonArray();
        intent = new Intent();
        bundle = new Bundle();

    }


    @Override
    public void onDestroy() {
        System.out.println("服务销毁");
        isLinster = false;
        index = 0;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int user_id = intent.getIntExtra("user_id",-1);
        String place_code = intent.getStringExtra("rt_code_place");
        String fxbh_code = intent.getStringExtra("rt_code_fxbh");
        String hphm = intent.getStringExtra("rt_hphm");
        RefreshCarInfo(user_id,place_code,fxbh_code,hphm);
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");

    }

    private  void msendBroadcast(JsonArray array){
        index++;
        System.out.println(index);
        bundle.putInt("index",index);
        bundle.putString("control_result", array.toString());
        intent.putExtras(bundle);
        intent.setAction(Constants.CONTROL_CARINFO_ACTION);
        sendBroadcast(intent);
    }
    public void RefreshCarInfo(final int user_id,final String place,final String fxbh,final String hphm){
        int mplace =Integer.parseInt(place)+2;
        int mfxbh =Integer.parseInt(fxbh)+1;
        String queryString = user_id+"+place:"+mplace+"+fxbh:"+mfxbh;
        System.out.println("querystr"+queryString);
        KakouClient client = ServiceGenerator.createService(KakouClient.class, Constants.BASE_URL);
        client.getRefresh(queryString, new Callback<JsonObject>() {
            @Override
            public void failure(RetrofitError arg0) {
                arg0.printStackTrace();
                if (isLinster){
                    RefreshCarInfo(user_id, place, fxbh, hphm);
                }
            }

            @Override
            public void success(JsonObject arg0, Response arg1) {
                JsonArray mArray = arg0.get("items").getAsJsonArray();
                msendBroadcast(mArray);
                if (isLinster){
                    RefreshCarInfo(user_id, place, fxbh, hphm);
                }
            }
        });
    }

}
