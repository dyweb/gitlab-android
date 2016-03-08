package io.dongyue.gitlabandroid.network;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import io.dongyue.gitlabandroid.model.Account;

/**
 * Created by Brotherjing on 2016/3/4.
 */
public class AuthentificationInterceptor implements Interceptor {

    private static final String PRIVATE_TOKEN_GET_PARAMETER = "private_token";
    private static final String PRIVATE_TOKEN_HEADER_FIELD = "PRIVATE-TOKEN";

    private Account account;

    public AuthentificationInterceptor(Account account) {
        this.account = account;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
//        HttpUrl url = request.httpUrl();
        String privateToken =account.getPrivateToken();
        if(privateToken!=null) {
            request = request.newBuilder()
                    .header(PRIVATE_TOKEN_HEADER_FIELD, privateToken)
                    //.url(url)
                    .build();
        }
        return chain.proceed(request);
    }
}
