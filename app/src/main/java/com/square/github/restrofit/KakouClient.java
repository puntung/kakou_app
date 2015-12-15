package com.square.github.restrofit;


import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

import com.google.gson.JsonObject;
import com.squareup.okhttp.Response;


public interface KakouClient {
	@GET("/rest_cgs/index.php/v2/cgs/vehicles")
    void getinfo(@Query("q") String hp,Callback<JsonObject> callback);
	@POST("/rest_kakou/index.php/v1/admin/login")
	void login(@Body JsonObject object,Callback<JsonObject> callback);
	@GET("/rest_kakou/index.php/v1/logo/fresh")
	void getRefresh(@Query("q") String qs,Callback<JsonObject> callback);
	@GET("/rest_kakou/index.php/v1/logo/kkdd")
	void getKkdd(Callback<JsonObject> callback);
	@GET("/rest_kakou/index.php/v1/logo/fxbh")
	void getFxbh(Callback<JsonObject> callback);
	@GET("/rest_kakou/index.php/v1/logo/csys")
	void getCsys(Callback<JsonObject> callback);
	@GET("/rest_kakou/index.php/v1/logo/hpys")
	void getHpys(Callback<JsonObject> callback);
	@GET("/rest_kakou/index.php/v1/logo/hpzl")
	void getHpzl(Callback<JsonObject> callback);
	@GET("/rest_kakou/index.php/v1/logo/cllx")
	void getCllx(Callback<JsonObject> callback);
	@GET("/rest_kakou/index.php/v1/logo/ppdmall")
	void getPpdm(Callback<JsonObject> callback);
	@GET("/rest_kakou/index.php/v1/logo/carinfos")
    void getCarInfosList(@Query("q") String qs,Callback<JsonObject> callback);
	@GET("/rest_kakou/index.php/upload/latest_version")
	void getVersion(Callback<JsonObject> callback);
	//@GET("/rest_kakou/index.php/upload/do_upload")
}
