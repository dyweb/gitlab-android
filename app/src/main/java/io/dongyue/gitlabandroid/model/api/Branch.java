package io.dongyue.gitlabandroid.model.api;

import com.google.gson.annotations.SerializedName;

import io.dongyue.gitlabandroid.utils.ObjectUtil;

public class Branch {
    @SerializedName("name")
    String mName;
    @SerializedName("protected")
    boolean mProtected;

    public Branch() {}

    public String getName() {
        return mName;
    }

    public boolean isProtected() {
        return mProtected;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Branch)) {
            return false;
        }

        Branch branch = (Branch) o;
        return ObjectUtil.equals(mName, branch.mName);
    }

    @Override
    public int hashCode() {
        return ObjectUtil.hash(mName);
    }

    @Override
    public String toString() {
        return mName;
    }
}
