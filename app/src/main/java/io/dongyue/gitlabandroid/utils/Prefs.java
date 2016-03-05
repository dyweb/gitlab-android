package io.dongyue.gitlabandroid.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import io.dongyue.gitlabandroid.model.Account;

/**
 * Created by Brotherjing on 2016/3/5.
 */
public class Prefs {

    private static final String KEY_ACCOUNTS = "accounts";

    private static SharedPreferences getSharedPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setAccount(Context context,Account account){
        getSharedPrefs(context)
                .edit()
                .putString(KEY_ACCOUNTS,GsonProvider.getInstance().toJson(account))
                .commit();
    }

    public static Account getAccount(Context context){
        String json = getSharedPrefs(context).getString(KEY_ACCOUNTS,null);
        if(!TextUtils.isEmpty(json)){
            return GsonProvider.getInstance().fromJson(json,Account.class);
        }else{
            return new Account();
        }
    }

}
