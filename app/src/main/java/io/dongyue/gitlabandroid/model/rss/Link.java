package io.dongyue.gitlabandroid.model.rss;

import android.net.Uri;

import org.parceler.Parcel;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Parcel
@Root(strict = false)
public class Link {
    @Attribute(name = "href", required = true)
    Uri mHref;

    public Link() {}

    public Link(Uri mHref) {
        this.mHref = mHref;
    }

    public Uri getHref() {
        return mHref;
    }

    public void setmHref(Uri mHref) {
        this.mHref = mHref;
    }
}
