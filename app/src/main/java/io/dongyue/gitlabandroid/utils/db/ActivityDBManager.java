package io.dongyue.gitlabandroid.utils.db;

import java.util.List;

import io.dongyue.gitlabandroid.App;
import io.dongyue.gitlabandroid.model.db.Activity;
import io.dongyue.gitlabandroid.model.db.ActivityEntity;
import io.dongyue.gitlabandroid.model.rss.Feed;
import rx.Observable;

/**
 * Created by Brotherjing on 2016/3/18.
 */
public class ActivityDBManager {

    public static final int LIMIT = 20;

    public static Observable<List<Activity>> getActivityList(){
        return Observable.just(App.getData().select(Activity.class)
                .orderBy(ActivityEntity.UPDATED.desc())
                .limit(LIMIT).get().toList());
    }

    public static Observable<Iterable<ActivityEntity>> storeActivities(Feed feed){
        List<ActivityEntity> list = ActivityConverter.extractNewActivities(feed);
        return App.getData().insert(list).toObservable();
    }

}
