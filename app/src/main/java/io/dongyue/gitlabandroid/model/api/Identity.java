package io.dongyue.gitlabandroid.model.api;

import com.google.gson.annotations.SerializedName;

public class Identity {
    @SerializedName("provider")
    String mProvider;
    @SerializedName("extern_uid")
    String mExternUid;

    public Identity() {}

    public String getProvider() {
        return mProvider;
    }

    public String getExternUid() {
        return mExternUid;
    }
}
