package io.dongyue.gitlabandroid;

import android.app.Application;
import android.content.Context;

import io.dongyue.gitlabandroid.utils.ToastUtils;

/**
 * Created by Brotherjing on 2016/3/5.
 */
public class App extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        ToastUtils.register(this);
    }

    public static Context getInstance(){
        return context;
    }
}
