package com.msr.dnsdemo.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.msr.dnsdemo.R;

/**
 * Created by SANDEEP on 06-05-2017.
 */

public class CommonMethods {
    /**
     * to create and return the dialog
     *
     * @param context context as parameter
     * @return dialog object
     */
    public static Dialog progressDialog(Context context) {
        Dialog dialog = null;
        try {
            dialog = new Dialog(context);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.view_progress_dialog);
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;
    }

}
