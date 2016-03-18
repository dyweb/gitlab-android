package io.dongyue.gitlabandroid.model.rss;

import org.parceler.Parcel;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.Date;

@Parcel
@Root(strict = false)
public class Entry {
    @Element(name = "link", required = true)
    Link mLink;
    @Element(name = "title", required = true)
    String mTitle;
    @Element(name = "updated", required = true)
    Date mUpdated;
    @Element(name = "thumbnail", required = true)
    Thumbnail mThumbnail;
    @Element(name = "summary", required = true)
    String mSummary;

    public Entry() {}

    public Link getLink() {
        return mLink;
    }

    public String getTitle() {
        return mTitle;
    }

    public Date getUpdated() {
        return mUpdated;
    }

    public Thumbnail getThumbnail() {
        return mThumbnail;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setmLink(Link mLink) {
        this.mLink = mLink;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setmUpdated(Date mUpdated) {
        this.mUpdated = mUpdated;
    }

    public void setmThumbnail(Thumbnail mThumbnail) {
        this.mThumbnail = mThumbnail;
    }

    public void setmSummary(String mSummary) {
        this.mSummary = mSummary;
    }
}
