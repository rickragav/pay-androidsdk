package com.ahilysys.cnpdemoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.agilysys.payments.Callback
import com.agilysys.payments.ToastViewer

class MainActivity : AppCompatActivity(), Callback{
    override fun onSuccess(string: String?) {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var button = findViewById<Button>(R.id.checkout);

        button.setOnClickListener {
            var toastViewer = ToastViewer(this,this);
            toastViewer.toastView(this)

        }


    }
}