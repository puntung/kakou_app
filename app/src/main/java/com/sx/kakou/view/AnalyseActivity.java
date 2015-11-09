package com.sx.kakou.view;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


import com.example.sx_kakou.R;

/**
 * Created by mglory on 2015/10/28.
 */
public class AnalyseActivity extends Activity {
    private WebView analyseView;
    private WebSettings mWebSetting;
    private ProgressBar bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_analyser);
        analyseView = (WebView) findViewById(R.id.analyse_view);
        bar = (ProgressBar)findViewById(R.id.myProgressBar);
        mWebSetting = analyseView.getSettings();
        mWebSetting.setJavaScriptEnabled(true);
        mWebSetting.setLoadWithOverviewMode(true);
        analyseView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        analyseView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    bar.setVisibility(View.GONE);
                } else {
                    if (View.GONE == bar.getVisibility()) {
                        bar.setVisibility(View.VISIBLE);
                    }
                    bar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        analyseView.loadUrl("http://www.hcharts.cn/demo/index.php?p=10&theme=grid-light");

    }
}