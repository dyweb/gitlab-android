package io.dongyue.gitlabandroid.network;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.dongyue.gitlabandroid.model.Account;
import io.dongyue.gitlabandroid.utils.GsonProvider;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * Created by Brotherjing on 2016/3/4.
 */
public class GitlabClient {

    private static final String BASE_URL = "https://git.tongqu.me/";

    private static Account sAccount;
    private static GitLab gitLab;

    public static GitLab getInstance(Account account){
        sAccount = account;

        if(gitLab==null){
            createInstance();
        }
        return gitLab;
    }

    private static void createInstance(){

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(10, TimeUnit.SECONDS);
        client.interceptors().add(new HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT));
        client.interceptors().add(new AuthentificationInterceptor(sAccount));

        Executor executor = Executors.newCachedThreadPool();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .callbackExecutor(executor)
                .addConverterFactory(GsonConverterFactory.create(GsonProvider.createInstance(sAccount)))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();

        gitLab = retrofit.create(GitLab.class);
    }

}
