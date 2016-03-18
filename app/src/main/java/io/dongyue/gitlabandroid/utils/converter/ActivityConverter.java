package io.dongyue.gitlabandroid.utils.converter;

import io.dongyue.gitlabandroid.model.db.ActivityEntity;
import io.dongyue.gitlabandroid.model.rss.Entry;

/**
 * Created by Brotherjing on 2016/3/11.
 */
public class ActivityConverter {

    public ActivityEntity toEntity(Entry entry,boolean read){
        ActivityEntity activityEntity = new ActivityEntity();
        activityEntity.setLink(entry.getLink().getHref().toString());
        activityEntity.setTitle(entry.getTitle());
        activityEntity.setSummary(entry.getSummary());
        activityEntity.setThumbnail(entry.getThumbnail().getUrl().toString());
        activityEntity.setUpdated(entry.getUpdated());
        activityEntity.setRead(read?0:1);
        return activityEntity;
    }

}
