package io.dongyue.gitlabandroid.utils;

import android.content.Context;
import android.content.Intent;

import io.dongyue.gitlabandroid.activity.HomeActivity;
import io.dongyue.gitlabandroid.activity.IssueActivity;
import io.dongyue.gitlabandroid.activity.LoginActivity;
import io.dongyue.gitlabandroid.activity.MyActivitesActivity;
import io.dongyue.gitlabandroid.activity.ProjectActivity;
import io.dongyue.gitlabandroid.activity.SettingsActivity;
import io.dongyue.gitlabandroid.activity.UserInfoActivity;
import io.dongyue.gitlabandroid.model.api.Project;

/**
 * Created by Brotherjing on 2016/3/6.
 */
public class NavigationManager {

    public static void toLogin(Context context){
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    public static void toProject(Context context, Project project){
        context.startActivity(ProjectActivity.viewProject(context,project));
    }

    public static void toProjectList(Context context){
        context.startActivity(new Intent(context, HomeActivity.class));
    }

    public static void toMyActivities(Context context){
        context.startActivity(new Intent(context, MyActivitesActivity.class));
    }

    public static void toIssueList(Context context){
        context.startActivity(new Intent(context, IssueActivity.class));
    }

    public static void toSettings(Context context){
        context.startActivity(new Intent(context, SettingsActivity.class));
    }

    public static void toUserInfo(Context context){
        context.startActivity(new Intent(context, UserInfoActivity.class));
    }
}
