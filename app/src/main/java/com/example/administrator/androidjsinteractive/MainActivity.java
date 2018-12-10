package com.example.administrator.androidjsinteractive;

import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private Button btn1;
    private Button btn2;
    private TextView text;
    private WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inits();
    }

    private void inits() {
        bindViews();
        setOnclick();
        initWebView();
    }

    private void setOnclick() {
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
    }

    private void bindViews() {

        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        text = (TextView) findViewById(R.id.text);
        webview = (WebView) findViewById(R.id.webview);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                // 只需要将第一种方法的loadUrl()换成下面该方法即可
                webview.evaluateJavascript("callJS()", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        //此处为 js 返回的结果
                        text.setText(value);
                    }
                });
                break;
            case R.id.btn2:
                // 通过Handler发送消息
                webview.post(new Runnable() {
                    @Override
                    public void run() {
                        // 注意调用的JS方法名要对应上
                        // 调用javascript的callJS()方法
                        webview.loadUrl("javascript:callJSa()");
                    }
                });
                break;
        }
    }

    private void initWebView() {
        WebSettings webSettings = webview.getSettings();
        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //js调用原生的关键
        webview.addJavascriptInterface(new AndroidtoJs(), "test");

        // 先载入JS代码
        // 格式规定为:file:///android_asset/文件名.html
        webview.loadUrl("file:///android_asset/test.html");

        // 由于设置了弹窗检验调用结果,所以需要支持js对话框
        // webview只是载体，内容的渲染需要使用webviewChromClient类去实现
        // 通过设置WebChromeClient对象处理JavaScript的对话框
        //设置响应js 的Alert()函数
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                b.setTitle("Alert");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setCancelable(false);
                b.create().show();
                return true;
            }

        });

    }

    // 继承自Object类
    public class AndroidtoJs extends Object {

        // 定义JS需要调用的方法
        // 被JS调用的方法必须加入@JavascriptInterface注解
        @JavascriptInterface
        public void hello(String msg) {
            Toast.makeText(MainActivity.this, "JS调用了Android的hello方法", Toast.LENGTH_LONG).show();
        }
    }

}
