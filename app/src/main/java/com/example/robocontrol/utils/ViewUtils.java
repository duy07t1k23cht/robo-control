package com.example.robocontrol.utils;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.example.robocontrol.R;

/**
 * Created by Duy M. Nguyen on 5/14/2020.
 */
public class ViewUtils {
    public static void show(View view) {
        view.setVisibility(View.VISIBLE);
    }

    public static void hide(View view) {
        view.setVisibility(View.GONE);
    }

    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void toast(Context context, @StringRes int stringResID) {
        Toast.makeText(context, context.getString(stringResID), Toast.LENGTH_SHORT).show();
    }
}
