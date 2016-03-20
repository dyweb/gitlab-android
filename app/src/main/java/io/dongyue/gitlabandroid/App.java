package io.dongyue.gitlabandroid;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import io.dongyue.gitlabandroid.network.GitlabClient;
import io.dongyue.gitlabandroid.service.PollingService;
import io.dongyue.gitlabandroid.utils.OkHttpProvider;
import io.dongyue.gitlabandroid.utils.ToastUtils;
import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.rx.RxSupport;
import io.requery.rx.SingleEntityStore;
import io.requery.sql.Configuration;
import io.requery.sql.EntityDataStore;

import io.dongyue.gitlabandroid.model.db.Models;

/**
 * Created by Brotherjing on 2016/3/5.
 */
public class App extends Application {

    private static Context context;

    private static SingleEntityStore<Persistable> dataStore;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        ToastUtils.register(this);
        initPollingService();
    }

    public static Context getInstance(){
        return context;
    }

    private void initPollingService(){
        Intent intent = PollingService.getPollingIntent(this);
        PendingIntent pendingIntent = PendingIntent.getService(this, PollingService.CODE_ACTION_POLL
                , intent, 0);

        Calendar calendar = Calendar.getInstance();
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC,calendar.getTimeInMillis()+60*1000,AlarmManager.INTERVAL_HALF_HOUR,pendingIntent);
    }

    public static SingleEntityStore<Persistable> getData() {
        if (dataStore == null) {
            // override onUpgrade to handle migrating to a new version
            DatabaseSource source = new DatabaseSource(context, Models.DEFAULT, 1);
            Configuration configuration = source.getConfiguration();
            dataStore = RxSupport.toReactiveStore(
                    new EntityDataStore<Persistable>(configuration));
        }
        return dataStore;
    }
}
