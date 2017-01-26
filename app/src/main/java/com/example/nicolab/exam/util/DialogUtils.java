package com.example.nicolab.exam.util;

import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * Created by bianca on 30.11.2016.
 */
public class DialogUtils {
    public static void showError(Context context, Exception e) {
        new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage("Loading failed error "+e.getMessage())
                .setCancelable(true)
                .create()
                .show();
    }
}
