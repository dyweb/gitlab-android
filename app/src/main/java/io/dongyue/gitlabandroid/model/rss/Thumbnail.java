package io.dongyue.gitlabandroid.model.rss;

import android.net.Uri;

import org.parceler.Parcel;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Parcel
@Root(strict = false)
public class Thumbnail {
    @Attribute(name = "url", required = true)
    Uri mUrl;

    public Thumbnail() {}

    public Thumbnail(Uri mUrl) {
        this.mUrl = mUrl;
    }

    public Uri getUrl() {
        return mUrl;
    }

    public void setmUrl(Uri mUrl) {
        this.mUrl = mUrl;
    }
}
