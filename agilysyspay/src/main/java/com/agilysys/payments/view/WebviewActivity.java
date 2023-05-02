package com.agilysys.payments.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.agilysys.payments.R;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WebviewActivity extends Activity {


    static Context context1;
    String issuerUrl = "";
    String paRequest = "";
    String termUrl = "";
    String md = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        context1 = getApplicationContext();

//        Intent datass = new Intent();
//        datass.putExtra("response", "okay 200");
//        setResult(RESULT_OK,datass);
//        finish();

        Intent intent = getIntent();
        String cardresponse = intent.getStringExtra("3dsRequest");

        JsonObject jsonObject = (JsonObject) new JsonParser().parse(cardresponse);
        issuerUrl = jsonObject.get("issuerUrl").getAsString();
        paRequest = jsonObject.get("paRequest").getAsString();
        termUrl = jsonObject.get("termUrl").getAsString();
        md = jsonObject.get("md").getAsString();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        WebView web_view = findViewById(R.id.webview);
        web_view.requestFocus();
        web_view.getSettings().setLightTouchEnabled(true);
        web_view.getSettings().setJavaScriptEnabled(true);
        web_view.getSettings().setGeolocationEnabled(true);
        web_view.setSoundEffectsEnabled(true);
        web_view.addJavascriptInterface(new JsObject(this),"Android");
        String htmlContent = "<!DOCTYPE html>\n" +
                "<html xmlns=\"http://www.w3.org/1999/html\">\n" +
                "  <head>\n" +
                "    <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js\">\n" +
                "    </script>\n" +
                "    <style>\n" +
                "    </style>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <div id=\"agisFrame\">\n" +
                "    </div>\n" +
                "  </body>\n" +
                "  <script language=\"javascript\" type=\"text/javascript\">\n" +
                "    var container = document.getElementById(\"agisFrame\");\n" +
                "    container.style.display = 'inline-block';\n" +
                "    const adyenform = document.createElement('form');\n" +
                "    var childIframe = document.createElement('iframe');\n" +
                "    childIframe.width = 500;\n" +
                "    childIframe.height = 600;\n" +
                "    childIframe.frameBorder = 0;\n" +
                "    childIframe.name = 'adyenIframe';\n" +
                "    container.appendChild(childIframe);\n" +
                "    container.appendChild(adyenform);\n" +
                "    adyenform.method = 'POST';\n" +
                "    const paReq = document.createElement('input');\n" +
                "    paReq.type = 'hidden';\n" +
                "    paReq.name = 'PaReq';\n" +
                "    paReq.value = "+"'"+paRequest+"'"+";" +
                "    adyenform.appendChild(paReq);\n" +
                "    const md = document.createElement('input');\n" +
                "    md.type = 'hidden';\n" +
                "    md.name = 'MD';\n" +
                "    md.value = "+"'"+md+"'"+";" +
                "    adyenform.appendChild(md);\n" +
                "    const termUrl = document.createElement('input');\n" +
                "    termUrl.type = 'hidden';\n" +
                "    termUrl.name = 'TermUrl';\n" +
                "    termUrl.value = "+"'"+termUrl+"'"+";" +
                "    adyenform.appendChild(termUrl);\n" +
                "    adyenform.action = "+"'"+issuerUrl+"'"+";" +
                "    adyenform.target = 'adyenIframe';\n" +
                "    adyenform.submit();\n" +
                "    var f = function(ev){\n" +
//                "\t\talert(JSON.stringify(ev.data));\n" +
                "        Android.receiveMessage(JSON.stringify(ev.data));\n" +
                "\t}\n" +
                "    window.addEventListener('message', f, true);\n" +
                "  </script>\n" +
                "</html>\n";


        web_view.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressDialog.dismiss();
            }
        });
        web_view.loadData(htmlContent,
                "text/html", "UTF-8");
        web_view.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100) {
                    progressDialog.show();
                }
                if (progress == 100) {
                    progressDialog.dismiss();
                }
            }
        });

    }
    static class JsObject {
        WebviewActivity webviewActivity;
        public JsObject(WebviewActivity activity) {
            webviewActivity = activity;
        }

        @JavascriptInterface
        public void receiveMessage(String data) {
            Toast.makeText(webviewActivity, data + " fine ", Toast.LENGTH_SHORT).show();
            Log.i("JsObject", "postMessage data="+data);
            //handle data here
            Intent datass = new Intent();
            datass.putExtra("response", data);
            webviewActivity.setResult(RESULT_OK,datass);
            webviewActivity.finish();
        }
    }}