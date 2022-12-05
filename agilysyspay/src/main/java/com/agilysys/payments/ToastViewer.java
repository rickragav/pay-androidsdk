package com.agilysys.payments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.agilysys.payments.view.CheckoutActivity;

public class ToastViewer {
    static Context context = null;
    Callback callbacks = null;
    public ToastViewer(Context context, Callback callback) {
        ToastViewer.context = context;
        callbacks = callback;
    }

    public  void toastView(Activity activity){
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
        activity.startActivityForResult(intent,200);
    }
}
