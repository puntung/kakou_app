package com.sx.kakou.util;

import android.content.SharedPreferences;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.square.github.restrofit.Constants;
import com.square.github.restrofit.KakouClient;
import com.square.github.restrofit.ServiceGenerator;
import com.sx.kakou.model.UserInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by mglory on 2015/9/7.
 */
public class InitData {
    private KakouClient client ;
    public static List<String> place_list,fxbh_list,hpys_list;
    public static List<Integer> place_code_list,fxbh_code_list,hpys_code_list;
    public static Map<String,String> hpzlmap;
    public static Map<String,String> csysmap;
    public static Map<String,String> cllxmap;
    public static UserInfo userInfo = new UserInfo();
    public static LruCacheUtil lcu ;
    public InitData() {
        //初始化数据，并保存在内存中
        client = ServiceGenerator.createService(KakouClient.class, Constants.BASE_URL);
        place_list = new ArrayList<>();
        fxbh_list = new ArrayList<>();
        hpys_list = new ArrayList<>();
        place_code_list = new ArrayList<>();
        hpys_code_list = new ArrayList<>();
        fxbh_code_list = new ArrayList<>();
        hpzlmap = new HashMap<>();
        csysmap = new HashMap<>();
        cllxmap = new HashMap<>();
        lcu = new LruCacheUtil();
        getPlace();
        getFxbh();
        getHpys();
        getHpzl();
        getCsys();
        getCllx();
    }

    public void getPlace(){
        client.getPlace(new Callback<JsonObject>() {
            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
            }

            @Override
            public void success(JsonObject arg0, Response arg1) {
                try {
                    JSONArray array = new JSONArray(arg0.get("items").toString());
                    place_list.add("全部");
                    place_code_list.add(0);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = new JSONObject(array.get(i).toString());
                        place_list.add(object.getString("name"));
                        place_code_list.add(object.getInt("id"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }
    public void getHpys(){
        client.getHpys(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                try {
                    hpys_list.add("全部");
                    hpys_code_list.add(0);
                    JSONArray array = new JSONArray(jsonObject.get("items").toString());
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject mobject = new JSONObject(array.get(i).toString());
                        hpys_list.add(mobject.getString("name"));
                        hpys_code_list.add(mobject.getInt("id"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }

    public void getFxbh(){
        client.getFxhb(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                try {
                    fxbh_list.add("全部");
                    fxbh_code_list.add(0);
                    JSONArray array = new JSONArray(jsonObject.get("items").toString());
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = new JSONObject(array.get(i).toString());
                        if (!object.getString("name").equals("其他")) {
                            fxbh_list.add(object.getString("name"));
                            fxbh_code_list.add(object.getInt("id"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
            }
        });
    }

    public void getHpzl(){
        client.getHpzl(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                JsonArray hpzlarray = jsonObject.getAsJsonArray("items");
                for (int i = 0; i < hpzlarray.size(); i++) {
                    JsonObject object = hpzlarray.get(i).getAsJsonObject();
                    hpzlmap.put(object.get("code").toString().replace("\"", ""), object.get("name").toString().replace("\"", ""));
                }
                System.out.println(hpzlmap.toString());
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
            }
        });
    }

    public void getCsys() {
        client.getCsys(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                JsonArray csysarray = jsonObject.getAsJsonArray("items");
                for (int i = 0; i < csysarray.size(); i++) {
                    JsonObject object = csysarray.get(i).getAsJsonObject();
                    csysmap.put(object.get("code").toString().replace("\"", ""), object.get("name").toString().replace("\"", ""));
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
            }
        });
    }

    public void getCllx() {
        client.getCllx(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                JsonArray cllxarray = jsonObject.getAsJsonArray("items");
                for (int i = 0; i < cllxarray.size(); i++) {
                    JsonObject object = cllxarray.get(i).getAsJsonObject();
                    cllxmap.put(object.get("code").toString().replace("\"", ""), object.get("name").toString().replace("\"", ""));
                }
            }
            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
            }
        });
    }

    public void getPpdm(int id){
        client.getPpdm(id, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                retrofitError.printStackTrace();
            }
        });
    }

}
