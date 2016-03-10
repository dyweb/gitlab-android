package io.dongyue.gitlabandroid.network;

import io.dongyue.gitlabandroid.model.rss.Feed;
import retrofit.http.GET;
import retrofit.http.Url;
import rx.Observable;

public interface GitLabRss {

    String RSS_SUFFIX = "dashboard/projects.atom";

    @GET
    Observable<Feed> getFeed(@Url String url);
}
