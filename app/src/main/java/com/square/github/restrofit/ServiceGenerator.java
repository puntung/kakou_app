package com.square.github.restrofit;

import android.util.Base64;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class ServiceGenerator {
	 private ServiceGenerator() {
	    }
	 public static <S> S createService(Class<S> serviceClass, String baseUrl) {
	        // call basic auth generator method without user and pass
	        //return createService(serviceClass, baseUrl, null, null);
	        RestAdapter.Builder builder = new RestAdapter.Builder()
	         .setEndpoint(baseUrl)
	         .setClient(new OkClient(new OkHttpClient()));
            //add Header
             builder.setRequestInterceptor(
                     new RequestInterceptor() {
                         @Override
                         public void intercept(RequestFacade request) {
                             request.addHeader("X-API-KEY", Constants.X_API_KEY);
                         }
                     }
             );
	        RestAdapter adapter = builder.build();
	    	return adapter.create(serviceClass);
		 
	    }

	    public static <S> S createService(Class<S> serviceClass, String baseUrl, String username, String password) {
//			BASIC AUTHENTICATION 的验证方法    	
//	       // set endpoint url and use OkHTTP as HTTP client 
//	        RestAdapter.Builder builder = new RestAdapter.Builder()
//	                .setEndpoint(Constants.BASE_URL)
//	                .setClient(new OkClient(new OkHttpClient()));
//
//	        if (username != null && password != null) {
//	            // concatenate username and password with colon for authentication
//	        final String credentials = username + ":" + password;
//
//	        builder.setRequestInterceptor(new RequestInterceptor() {
//	        @Override
//            public void intercept(RequestFacade request) {
//                // create Base64 encodet string
//                String string = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
//                request.addHeader("Authorization", string);
//                request.addHeader("Accept", "application/json");
//            }
//		        });
//		    }
//	        RestAdapter adapter = builder.build();
//	        return adapter.create(serviceClass);
	    	
	    	//Digest authentication 的验证方法
	        RestAdapter.Builder builder = new RestAdapter.Builder()
            .setEndpoint(baseUrl)
            .setClient(new MyClient(username, password));
            builder.setRequestInterceptor(
                    new RequestInterceptor() {
                        @Override
                        public void intercept(RequestFacade request) {
                            request.addHeader("X-API-KEY", Constants.X_API_KEY);
                        }
                    }
            );
	    	RestAdapter adapter = builder.build();
	    	return adapter.create(serviceClass);
	    }
	    
	    
	
}
