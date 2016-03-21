package io.dongyue.gitlabandroid.fragment;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.dongyue.gitlabandroid.App;
import io.dongyue.gitlabandroid.R;
import io.dongyue.gitlabandroid.adapter.FeedAdapter;
import io.dongyue.gitlabandroid.model.db.Activity;
import io.dongyue.gitlabandroid.model.db.ActivityEntity;
import io.dongyue.gitlabandroid.model.rss.Entry;
import io.dongyue.gitlabandroid.model.rss.Feed;
import io.dongyue.gitlabandroid.network.GitLab;
import io.dongyue.gitlabandroid.network.GitLabRss;
import io.dongyue.gitlabandroid.network.GitlabClient;
import io.dongyue.gitlabandroid.network.GitlabSubscriber;
import io.dongyue.gitlabandroid.utils.Logger;
import io.dongyue.gitlabandroid.utils.ToastUtils;
import io.dongyue.gitlabandroid.utils.db.ActivityConverter;
import io.dongyue.gitlabandroid.utils.db.ActivityDBManager;
import io.dongyue.gitlabandroid.utils.eventbus.RxBus;
import io.dongyue.gitlabandroid.utils.eventbus.events.NewActivitiesEvent;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActivitiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivitiesFragment extends BaseFragment {

    private static final String ARG_FEED_URL = "arg_feed_url";
    private static final String ARG_FEED_TYPE = "arg_feed_type";
    public static final int FEED_TYPE_USER = 0;
    public static final int FEED_TYPE_ALL = 1;
    public static final int FEED_TYPE_UNKNOWN = -1;

    private Integer feed_type;
    private Uri feed_url;
    private FeedAdapter feedAdapter;

    @Bind(R.id.swipe_layout)SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.list)RecyclerView activityListView;

    public static ActivitiesFragment newInstance(Uri feedUrl) {
        ActivitiesFragment fragment = new ActivitiesFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_FEED_URL, feedUrl);
        fragment.setArguments(args);
        return fragment;
    }

    public static ActivitiesFragment newInstance(int feedType) {
        ActivitiesFragment fragment = new ActivitiesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FEED_TYPE, feedType);
        fragment.setArguments(args);
        return fragment;
    }

    public ActivitiesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            feed_url = getArguments().getParcelable(ARG_FEED_URL);
            feed_type = getArguments().getInt(ARG_FEED_TYPE,FEED_TYPE_UNKNOWN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_activities, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        feedAdapter = new FeedAdapter(entry -> ToastUtils.showShort(entry.getTitle()));
        activityListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        activityListView.setAdapter(feedAdapter);

        activityListView.addOnScrollListener(feedAdapter.stopLoadingWhenScrollListener);

        swipeRefreshLayout.setOnRefreshListener(ActivitiesFragment.this::loadData);

        loadFromDB();
        loadData();
        addSubscription(RxBus.getBus().observeEvents(NewActivitiesEvent.class).observeOn(AndroidSchedulers.mainThread()
            ).subscribe(newActivitiesEvent -> {
                loadFromDB();
            }));
    }

    private void loadFromDB(){
        //we have only cached activities of this type
        if(feed_type!=FEED_TYPE_ALL)return;
        addSubscription(ActivityDBManager.getActivityList()
                .map(activities -> {
                    List<Entry> entries = new ArrayList<>();
                    for (Activity activity : activities)
                        entries.add(ActivityConverter.toActivity(activity));
                    return entries;
                })
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(entries -> {
                    swipeRefreshLayout.setRefreshing(false);
                    feedAdapter.setEntries(entries);
                }));
    }

    private void loadData(){
        if(feed_url==null&&feed_type==FEED_TYPE_UNKNOWN)return;
        swipeRefreshLayout.setRefreshing(true);

        Subscriber<Feed> subscriber = new GitlabSubscriber<Feed>() {
            @Override
            public void onNext(Feed feed) {
                swipeRefreshLayout.setRefreshing(false);
                feedAdapter.setEntries(feed.getEntries());
            }
        };

        if(feed_url!=null) {
            addSubscription(GitlabClient.getRssInstance().getFeed(feed_url.toString())
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber));
        }else if(feed_type==FEED_TYPE_USER){
            addSubscription(GitlabClient.getInstance().getThisUser()
                .flatMap(userFull -> {
                    Logger.i(userFull.getFeedUrl().toString());
                    return GitlabClient.getRssInstance().getFeed(userFull.getFeedUrl().toString());
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber));
        }else if(feed_type == FEED_TYPE_ALL){
            addSubscription(GitlabClient.getRssInstance().getFeed(GitLab.BASE_URL + GitLabRss.RSS_SUFFIX)
                    .flatMap(ActivityDBManager::storeActivities)
                    .subscribeOn(Schedulers.io())
                    .subscribe(activityEntities -> {
                        loadFromDB();
                    }));
        }
    }
}
