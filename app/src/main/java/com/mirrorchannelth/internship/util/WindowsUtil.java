package com.mirrorchannelth.internship.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by boss on 4/21/16.
 */
public class WindowsUtil {

    public static void defaultAlertDialog(String title, String message, String label, Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        
        builder.setNeutralButton(label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public static void alertWithTwoButton(String title, String message, String labelPositive, String labelNegative, Context context, DialogInterface.OnClickListener approveListener, DialogInterface.OnClickListener  cancelListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton(labelPositive, approveListener);
        builder.setNegativeButton(labelNegative, cancelListener);

        AlertDialog dialog = builder.create();
        dialog.show();

    }


}
