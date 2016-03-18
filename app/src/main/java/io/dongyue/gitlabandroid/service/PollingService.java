package io.dongyue.gitlabandroid.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import io.dongyue.gitlabandroid.R;
import io.dongyue.gitlabandroid.activity.HomeActivity;
import io.dongyue.gitlabandroid.model.rss.Feed;
import io.dongyue.gitlabandroid.network.GitLab;
import io.dongyue.gitlabandroid.network.GitLabRss;
import io.dongyue.gitlabandroid.network.GitlabClient;
import io.dongyue.gitlabandroid.network.GitlabSubscriber;
import io.dongyue.gitlabandroid.utils.Logger;
import io.dongyue.gitlabandroid.utils.eventbus.RxBus;
import io.dongyue.gitlabandroid.utils.eventbus.events.NewActivitiesEvent;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PollingService extends IntentService {

    private static final String ACTION_POLL = "io.dongyue.gitlabandroid.service.action.POLL_ACTIVITIES";
    public static final int CODE_ACTION_POLL = 12345;

    private static final String EXTRA_PARAM1 = "io.dongyue.gitlabandroid.service.extra.PARAM1";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startPolling(Context context) {
        /*Intent intent = new Intent(context, PollingService.class);
        intent.setAction(ACTION_POLL);*/
        context.startService(getPollingIntent(context));
    }

    public static Intent getPollingIntent(Context context){
        Intent intent = new Intent(context, PollingService.class);
        intent.setAction(ACTION_POLL);
        return intent;
    }

    public PollingService() {
        super("PollingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_POLL.equals(action)) {
                poll();
            }
        }
    }

    private final Subscriber<Feed> subscriber = new GitlabSubscriber<Feed>() {
        @Override
        public void onNext(Feed feed) {
            Logger.i("receive msg:"+feed.getEntries().size());
            RxBus.getBus().post(new NewActivitiesEvent(feed.getEntries().size()));
            showNotification();
        }
    };
    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void poll() {
        GitlabClient.getRssInstance().getFeed(GitLab.BASE_URL+ GitLabRss.RSS_SUFFIX)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    private void showNotification(){
        Intent intent = new Intent(this, HomeActivity.class);
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.ic_menu_recent_history)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_recent_history))
                .setContentTitle("hello world")
                .setContentText("hello world!")
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
                .setAutoCancel(true).build();
        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1,notification);
    }
}
