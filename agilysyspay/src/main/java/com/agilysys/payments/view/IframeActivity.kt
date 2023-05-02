package com.agilysys.payments.view

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.agilysys.payments.R

class IframeActivity : AppCompatActivity() {
    var context1: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iframe)
        context1 = applicationContext


//        Intent datass = new Intent();
//        datass.putExtra("response", "okay 200");
//        setResult(RESULT_OK,datass);
//        finish();
        val intent = intent
        val getIframeURL = intent.getStringExtra("getIframeURL")
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        val web_view = findViewById<WebView>(R.id.webvieww)
        web_view.requestFocus()
        web_view.settings.lightTouchEnabled = true
        web_view.settings.javaScriptEnabled = true
        web_view.settings.setGeolocationEnabled(true)
        web_view.isSoundEffectsEnabled = true
        web_view.addJavascriptInterface(JsObject(this), "Android")
        val htmlContent = "<html>\n" +
                "<iframe style=\"width: 100%;height: 600px\" allowPaymentRequest=\"true\" " +
                "src=\"'${getIframeURL}'\"></iframe>\n" +
                "<script>\n" +
                "var f = function(ev){\n" +
                "if (ev.data) {\n" +
                "console.log(ev.data);\n" +
                "}\n" +
                "if (ev.data.code === 9300 || ev.data.transactionReferenceData || ev.data.code === '9300') {\n" +
                "window.removeEventListener(\"message\", f, false);\n" +
                "}\n" +
                "}\n" +
                "window.addEventListener('message', f, false);\n" +
                "</script>\n" +
                "</html>";

//        web_view.webViewClient = object : WebViewClient() {
//            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
//                super.onPageStarted(view, url, favicon)
//                progressDialog.show()
//            }
//
//            override fun onPageFinished(view: WebView, url: String) {
//                super.onPageFinished(view, url)
//                progressDialog.dismiss()
//            }
//        }
        println("valueget " + getIframeURL)
        web_view.loadUrl(getIframeURL!!);
        web_view.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                if (progress < 100) {
                    progressDialog.show()
                }
                if (progress == 100) {
                    progressDialog.dismiss()
                }
            }
        }
    }

    internal class JsObject(var webviewActivity: IframeActivity) {
        @JavascriptInterface
        fun receiveMessage(data: String) {
            Toast.makeText(webviewActivity, "$data fine ", Toast.LENGTH_SHORT).show()
            Log.i("JsObject", "postMessage data=$data")
            //handle data here
            val datass = Intent()
            datass.putExtra("response", data)
            webviewActivity.setResult(RESULT_OK, datass)
            webviewActivity.finish()
        }
    }
}
