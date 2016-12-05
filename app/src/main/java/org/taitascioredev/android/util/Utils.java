package org.taitascioredev.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Date;

/**
 * Created by roberto on 28/11/16.
 */

public final class Utils {

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("pref", Context.MODE_PRIVATE);
    }

    public static void saveLoggedUserId(Context context, int id) {
        SharedPreferences pref = getSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("id", id);
        editor.commit();
    }

    public static int getLoggedUserId(Context context) {
        SharedPreferences pref = getSharedPreferences(context);
        return pref.getInt("id", 0);
    }

    public static void hideKeyboard(Activity context) {
        View view = context.getCurrentFocus();

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void vibrate(Context context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(1000);
    }

    public static boolean today(Date date) {
        Date now = new Date();
        return now.getDate() == date.getDate() && now.getMonth() == date.getMonth() && now.getYear() == date.getYear();
    }
}
