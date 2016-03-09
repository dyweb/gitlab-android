package io.dongyue.gitlabandroid.network;

import android.text.TextUtils;

import java.net.UnknownHostException;

import io.dongyue.gitlabandroid.R;
import io.dongyue.gitlabandroid.utils.ToastUtils;
import retrofit.HttpException;
import rx.Subscriber;

/**
 * Created by Brotherjing on 2016/3/9.
 */
public abstract class GitlabSubscriber<T> extends Subscriber<T> {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof HttpException) {
            ToastUtils.showShort("error code " + ((HttpException) e).code());
        }else if(e instanceof UnknownHostException){
            ToastUtils.showShort(R.string.error_unknown_host);
        }else{
            ToastUtils.showShort(R.string.error_not_connected);
        }
        e.printStackTrace();
    }

}
