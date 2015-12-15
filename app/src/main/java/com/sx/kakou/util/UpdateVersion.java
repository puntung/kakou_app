package com.sx.kakou.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.square.github.restrofit.Constants;
import com.square.github.restrofit.KakouClient;
import com.square.github.restrofit.ServiceGenerator;
import com.squareup.okhttp.Response;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by mglory on 2015/12/8.
 * 版本检查更新
 */
public class UpdateVersion {
    private KakouClient client ;
    private Context context;
    private JsonObject vsObject = new JsonObject();

    public UpdateVersion(Context context) {
        this.context = context;
        client = ServiceGenerator.createService(KakouClient.class, Constants.BASE_URL);
    }


    /*
       * 获取当前程序的版本号
       */
    private String getVersionName() throws Exception{
        //获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        return packInfo.versionName;
    }
    /*
    * 获取服务器最新版本号
    */
    public void CheckVersion(){
        final ProgressDialog progressDialog = ProgressDialog.show(context, null, "正在检查更新...", true);
        progressDialog.show();
        client.getVersion(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, retrofit.client.Response response) {
                try {
                    String vs = jsonObject.get("version").toString();
                    String url = jsonObject.get("file_url").toString().replace("\"","");
                    if (vs.equals(getVersionName())){
                        Log.i("tag","版本号相同无需升级");
                        progressDialog.dismiss();
                        Toast.makeText(context,"已是最新版本",Toast.LENGTH_SHORT).show();
                    }else{
                        Log.i("tag", "版本号不同 ,提示用户升级 ");
                        progressDialog.dismiss();
                        Log.i("url-->",url);
                        showUpdataDialog(url);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    protected void showUpdataDialog(final String url) {
        final String[] versionName = url.split("app");

        final AlertDialog.Builder builer = new AlertDialog.Builder(context) ;
        builer.setTitle("系统提示");
        builer.setMessage("发现有新版本，是否更新");
//        当点确定按钮时从服务器上下载 新的apk 然后安装
        builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                final ProgressDialog pd;    //进度条对话框
                pd = new ProgressDialog(context);
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd.setMessage("正在下载更新");
                pd.show();
                new Thread() {
                    @Override
                    public void run() {
                        try {
                           File file = getFileFromServer(url, pd);
                            sleep(3000);
                            installApk(file);
                            pd.dismiss(); //结束掉进度条对话框
                        } catch (Exception e) {
                            e.printStackTrace();
//                            Message msg = new Message();
//                            msg.what = DOWN_ERROR;
//                            handler.sendMessage(msg);
//                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
        //当点取消按钮时进行登录
        builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builer.create();
        dialog.show();
    }

    //安装apk
    protected void installApk(File file) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static File getFileFromServer(String path, ProgressDialog pd) throws Exception{
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.i("URL",path);
            URL url = new URL(path);
            HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            //获取到文件的大小
            pd.setMax(conn.getContentLength());
            InputStream is = conn.getInputStream();
            File file = new File(Environment.getExternalStorageDirectory(), "updata.apk");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len ;
            int total=0;
            while((len =bis.read(buffer))!=-1){
                fos.write(buffer, 0, len);
                total+= len;
                //获取当前下载量
                pd.setProgress(total);
            }
            fos.close();
            bis.close();
            is.close();
            Log.i("File",file.length()+"");
            return file;
        }
        else{
            return null;
        }
    }

}
