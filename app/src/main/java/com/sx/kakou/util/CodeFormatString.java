package com.sx.kakou.util;

import com.google.gson.JsonObject;
import com.square.github.restrofit.Constants;
import com.square.github.restrofit.KakouClient;
import com.square.github.restrofit.ServiceGenerator;
import com.sx.kakou.models.CxfxInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by mglory on 2015/9/7.
 */
public class CodeFormatString {

    private static CxfxInfo info;
    public CodeFormatString() {
        info = new CxfxInfo();
    }

    //车辆品牌转换
    public String PpdmFormat(String code){
        getPpdm();
        Map<String,String> map = info.getPpdm();
        System.out.println("map-->"+map);
        return  map.get(code);
    }

    public void getPpdm(){
        KakouClient client = ServiceGenerator.createService(KakouClient.class, Constants.BASE_URL);
        client.getPpdm(MyCallback);
    }
    Callback<JsonObject> MyCallback = new Callback<JsonObject>() {
        @Override
        public void success(JsonObject jsonObject, Response response) {
            try {
                JSONArray array = new JSONArray(jsonObject.get("items").toString());
                HashMap<String,String> map = new HashMap<String, String>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = new JSONObject(array.get(i).toString());
                    map.put(object.get("code").toString(), object.get("name").toString());
                }
                info.setPpdm(map);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            retrofitError.printStackTrace();
        }
    };
}
