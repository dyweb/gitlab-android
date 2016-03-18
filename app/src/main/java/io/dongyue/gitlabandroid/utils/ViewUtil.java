package io.dongyue.gitlabandroid.utils;

import android.content.Context;
import android.util.TypedValue;

import io.dongyue.gitlabandroid.App;

/**
 * Created by Brotherjing on 2016/3/18.
 */
public class ViewUtil {

    public static int dp2px(int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, App.getInstance().getResources().getDisplayMetrics());
    }

    public static int sp2px(int sp){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, App.getInstance().getResources().getDisplayMetrics());
    }

}
