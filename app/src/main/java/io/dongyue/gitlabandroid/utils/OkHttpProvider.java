package io.dongyue.gitlabandroid.utils;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.util.concurrent.TimeUnit;

import io.dongyue.gitlabandroid.model.Account;
import io.dongyue.gitlabandroid.network.AuthentificationInterceptor;

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
            client.interceptors().add(new AuthentificationInterceptor(account));
        }
        return client;
    }

}
