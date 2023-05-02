package com.agilysys.payments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.agilysys.payments.view.CheckoutActivity;
import com.agilysys.payments.view.IframeActivity;

public class ToastViewer {
    static Context context = null;
    Callback callbacks = null;
    public ToastViewer(Context context, Callback callback) {
        ToastViewer.context = context;
        callbacks = callback;
    }

    public  void toastView(Activity activity, boolean isToken){
//        Rect displayRectangle = new Rect();
//        Window window = activity.getWindow();
//        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
//        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        final AlertDialog alert = builder.create();
//        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
//        final View dialogView = LayoutInflater.from(activity).inflate(R.layout.layout, viewGroup, false);
//        dialogView.setMinimumWidth((int)(displayRectangle.width() * 2.5f));
//        dialogView.setMinimumHeight((int)(displayRectangle.height() * 2.5f));
//        alert.setView(dialogView);
//        alert.show();
//
//        Button btOk = dialogView.findViewById(R.id.buttonOk);
//        btOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                callbacks.onSuccess("Transaction Successful");
//                alert.dismiss();
//            }
//        });

        Intent intent = new Intent(activity, CheckoutActivity.class);
        intent.putExtra("request","Transaction request");
        if (isToken){
            intent.putExtra("isToken",true);
        }else {
            intent.putExtra("isToken",false);
        }
        activity.startActivityForResult(intent,200);


//        Intent intent = new Intent(activity, IframeActivity.class);
//        intent.putExtra("getIframeURL","https://aks-pay-qa.hospitalityrevolution.com/pay-iframe-service/v1/iFrame/tenants/0/6305c954910b9f43bc476052?apiToken=8d0aeaad-a539-415d-8acb-f1bf91eb2e26&submit=Pay&style=https://authorize.rguest.com/AuthorizeStyles/Authorize_Style.css&doVerify=false&version=3&payToken=a0dce7c868e34a7687c464652d0f7c06&language=en&transactionType=sale");
//        activity.startActivityForResult(intent,200);
    }
}
