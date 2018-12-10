# 简单Android和JS交互
[github地址](https://github.com/whwlu/AndroidAndJs)
#### // 设置与Js交互的权限
 ```
WebSettings webSettings = webview.getSettings();
webview.getSettings().setJavaScriptEnabled(true);
```

#### //设置允许JS弹窗
```
// 设置允许JS弹窗
webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
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
```
#### //js调用原生的关键 往js里面注入方法 ， test相当于js调用java的时候的一个别名  （ "test" = new AndroidtoJs() ）  然后定义方法的时候要加一个注解（ @JavascriptInterface）
```
//js调用原生的关键
webview.addJavascriptInterface(new AndroidtoJs(), "test");
// 继承自Object类
public class AndroidtoJs extends Object {

    // 定义JS需要调用的方法
    // 被JS调用的方法必须加入@JavascriptInterface注解
    @JavascriptInterface
    public void hello(String msg) {
        Toast.makeText(MainActivity.this, "JS调用了Android的hello方法", Toast.LENGTH_LONG).show();
    }
}
```

#### java调用js有两种方法（一种可以接收返回值  一种不行）
 ```
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
 ```

#### //js调用java
```
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Carson_Ho</title>
    // JS代码
    <script>
            //原生调用js方法  有返回值的方式
            function callJS(){
                return "Android调用了JS的callJS方法";
            }
            //原生调用js方法  alert的方式
            function callJSa(){
                alert("Android调用了JS的callJSa方法");
            }
            //JS调用原生   这里需要和原生对接  test为定义的别名  hollo（）为原生方法
            function callAndroid(){
                // 由于对象映射，所以调用test对象等于调用Android映射的对象
                test.hello("js调用了android中的hello方法");
            }

    </script>
</head>
<body>
//点击按钮则调用callAndroid函数
<button type="button" id="button1" onclick="callAndroid()">调用android</button>
</body>
</html>
```         
