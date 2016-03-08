package io.dongyue.gitlabandroid.model.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Brotherjing on 2016/3/8.
 */
public class APIError {

    @SerializedName("message")
    String message;

    public APIError() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
