package io.dongyue.gitlabandroid.model.db;

import android.os.Parcelable;

import java.util.Date;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.Persistable;
import rx.Observable;

/**
 * Created by Brotherjing on 2016/3/11.
 */
@Entity
public interface Activity extends Parcelable,Persistable{

    @Key @Generated
    int getId();

    int getRead();

    String getLink();

    String getTitle();

    String getThumbnail();

    String getSummary();

    Date getUpdated();

}
