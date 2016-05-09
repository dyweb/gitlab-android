package io.dongyue.gitlabandroid.model.api;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import io.dongyue.gitlabandroid.R;

@Parcel
public class RepositoryTreeObject {
    @SerializedName("id")
    String mId;
    @SerializedName("name")
    String mName;
    @SerializedName("type")
    Type mType;
    @SerializedName("mode")
    String mMode;

    public RepositoryTreeObject() {}

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public Type getType() {
        return mType;
    }

    public String getMode() {
        return mMode;
    }

    public int getDrawableForType() {
        if (mType == null) {
            return android.R.drawable.ic_dialog_info;
        }
        switch (mType) {
            case FILE:
                return android.R.drawable.ic_dialog_email;
            case FOLDER:
                return android.R.drawable.ic_menu_gallery;
            case REPO:
                return android.R.drawable.ic_menu_agenda;
        }

        return android.R.drawable.ic_dialog_info;
    }

    public Uri getUrl(Project project, String branchName, String currentPath) {
        return project.getWebUrl().buildUpon()
                .appendPath("tree")
                .appendPath(branchName)
                .appendEncodedPath(currentPath)
                .appendPath(mName)
                .build();
    }

    public enum Type {
        @SerializedName("tree")
        FOLDER,
        @SerializedName("submodule")
        REPO,
        @SerializedName("blob")
        FILE
    }
}
