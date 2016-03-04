package io.dongyue.gitlabandroid.model.api;

import com.google.gson.annotations.SerializedName;

public class UserLogin extends UserFull {
    @SerializedName("private_token")
    String mPrivateToken;

    public UserLogin() {}

    public String getPrivateToken() {
        return mPrivateToken;
    }
}
