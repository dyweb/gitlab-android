package io.dongyue.gitlabandroid.network;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.dongyue.gitlabandroid.App;
import io.dongyue.gitlabandroid.model.Account;
import io.dongyue.gitlabandroid.utils.GsonProvider;
import io.dongyue.gitlabandroid.utils.OkHttpProvider;
import io.dongyue.gitlabandroid.utils.Prefs;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * Created by Brotherjing on 2016/3/4.
 */
public class GitlabClient {

    private static Account sAccount;
    private static GitLab gitLab;
    private static Picasso picasso;

    public static void setAccount(Account account){
        sAccount = account;
        gitLab = null;
        picasso = null;
    }

    public static GitLab getInstance(Account account){

        if(gitLab==null||!account.equals(sAccount)){
            sAccount = account;
            createInstance();
        }
        return gitLab;
    }

    public static GitLab getInstance(){
        if(sAccount==null){
            sAccount = Prefs.getAccount(App.getInstance());
        }
        if(gitLab==null){
            createInstance();
        }
        return gitLab;
    }

    public static Picasso getPicasso(){
        if(picasso==null){
            picasso = new Picasso.Builder(App.getInstance())
                    .downloader(new OkHttpDownloader(OkHttpProvider.getInstance(sAccount)))
                    .build();
        }
        return picasso;
    }

    private static void createInstance(){

        Executor executor = Executors.newCachedThreadPool();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GitLab.BASE_URL)
                .callbackExecutor(executor)
                .addConverterFactory(GsonConverterFactory.create(GsonProvider.createInstance(sAccount)))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(OkHttpProvider.getInstance(sAccount))
                .build();

        gitLab = retrofit.create(GitLab.class);
    }

}
