package com.square.github.restrofit;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

import com.google.gson.JsonObject;

import org.json.JSONObject;

public interface KakouClient {
	@GET("/rest_cgs/index.php/v2/cgs/vehicles")
    void getinfo(@Query("q") String hp,Callback<JsonObject> callback);
	@GET("/v1/ping/127.0.0.1")
	void test_auth(Callback<JsonObject> callback);
	@POST("/rest_kakou/index.php/v1/admin/login")
	void login(@Body JsonObject object,Callback<JsonObject> callback);
	@GET("/rest_kakou/index.php/v1/logo/fresh2")
	void getRefresh(@Query("q") String qs,Callback<JsonObject> callback);
	@GET("/rest_kakou/index.php/v1/logo/ppdm")
	 void getPpdm(Callback<JsonObject> callback );
	@GET("/rest_kakou/index.php/v1/logo/place")
	void getPlace(Callback<JsonObject> callback);
	@GET("/rest_kakou/index.php/v1/logo/fxbh")
	void getFxhb(Callback<JsonObject> callback);
	@GET("/rest_kakou/index.php/v1/logo/csys")
	void getCsys(Callback<JsonObject> callback);
	@GET("/rest_kakou/index.php/v1/logo/hpys")
	void getHpys(Callback<JsonObject> callback);
	@GET("/rest_kakou/index.php/v1/logo/carinfos2")
    void getCarInfosList(@Query("q") String qs,Callback<JsonObject> callback);
}
