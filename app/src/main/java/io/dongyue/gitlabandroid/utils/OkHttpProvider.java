package io.dongyue.gitlabandroid.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.dongyue.gitlabandroid.App;
import io.dongyue.gitlabandroid.R;
import io.dongyue.gitlabandroid.model.Account;
import io.dongyue.gitlabandroid.model.api.APIError;
import io.dongyue.gitlabandroid.network.AuthentificationInterceptor;
import io.dongyue.gitlabandroid.network.ConnectivityHelper;
import io.dongyue.gitlabandroid.utils.eventbus.RxBus;
import io.dongyue.gitlabandroid.utils.eventbus.events.APIErrorEvent;
import io.dongyue.gitlabandroid.utils.eventbus.events.NoNetworkEvent;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

/**
 * Created by Brotherjing on 2016/3/5.
 */
public class OkHttpProvider {

    private static OkHttpClient client;

    public static OkHttpClient getInstance(Account account){
        if(client==null){
            client = new OkHttpClient();
            client.setConnectTimeout(10, TimeUnit.SECONDS);
            client.interceptors().add(new HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT));
            client.networkInterceptors().add(new AuthentificationInterceptor(account));
            client.interceptors().add(mErrorInterceptor);
        }
        return client;
    }


    private static final Interceptor mErrorInterceptor = chain -> {

        Request request = chain.request();
        if(!ConnectivityHelper.isConnected(App.getInstance())){
            RxBus.getBus().post(new NoNetworkEvent());
            //Log.i("yj",mContext.getResources().getString(R.string.error_not_connected));
            return null;
        }
        Response response = chain.proceed(request);
        if(!response.isSuccessful()){
            //String msg = response.message();
            APIError error = GsonProvider.getInstance().fromJson(response.body().string(),APIError.class);
            RxBus.getBus().post(new APIErrorEvent(response.code(),error));
            return null;
        }
        return response;
    };

}
