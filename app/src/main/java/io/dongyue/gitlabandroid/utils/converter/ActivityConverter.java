package io.dongyue.gitlabandroid.utils.converter;

import android.net.Uri;

import io.dongyue.gitlabandroid.model.db.Activity;
import io.dongyue.gitlabandroid.model.db.ActivityEntity;
import io.dongyue.gitlabandroid.model.rss.Entry;
import io.dongyue.gitlabandroid.model.rss.Link;
import io.dongyue.gitlabandroid.model.rss.Thumbnail;

/**
 * Created by Brotherjing on 2016/3/11.
 */
public class ActivityConverter {

    public static ActivityEntity toEntity(Entry entry,boolean read){
        ActivityEntity activityEntity = new ActivityEntity();
        activityEntity.setLink(entry.getLink().getHref().toString());
        activityEntity.setTitle(entry.getTitle());
        activityEntity.setSummary(entry.getSummary());
        activityEntity.setThumbnail(entry.getThumbnail().getUrl().toString());
        activityEntity.setUpdated(entry.getUpdated());
        activityEntity.setRead(read?0:1);
        return activityEntity;
    }

    public static Entry toActivity(Activity activity){
        Entry entry = new Entry();
        entry.setmLink(new Link(Uri.parse(activity.getLink())));
        entry.setmSummary(activity.getSummary());
        entry.setmThumbnail(new Thumbnail(Uri.parse(activity.getThumbnail())));
        entry.setmTitle(activity.getTitle());
        entry.setmUpdated(activity.getUpdated());
        return entry;
    }

}
