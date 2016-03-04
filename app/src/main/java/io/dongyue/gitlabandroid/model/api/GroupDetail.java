package io.dongyue.gitlabandroid.model.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GroupDetail extends Group {
    @SerializedName("projects")
    List<Project> mProjects;

    public GroupDetail() {}

    public List<Project> getProjects() {
        return mProjects;
    }
}
