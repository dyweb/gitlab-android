package io.dongyue.gitlabandroid.utils.eventbus.events;

import io.dongyue.gitlabandroid.model.api.APIError;

/**
 * Created by Brotherjing on 2016/3/9.
 */
public class APIErrorEvent extends UniqueEvent{

    private int code;
    private APIError apiError;

    public APIErrorEvent(int code, APIError apiError) {
        this.code = code;
        this.apiError = apiError;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public APIError getApiError() {
        return apiError;
    }

    public void setApiError(APIError apiError) {
        this.apiError = apiError;
    }
}
