package io.dongyue.gitlabandroid.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.dongyue.gitlabandroid.R;
import io.dongyue.gitlabandroid.activity.base.BaseActivity;
import io.dongyue.gitlabandroid.adapter.IssuesAdapter;
import io.dongyue.gitlabandroid.model.MyIssueInfo;
import io.dongyue.gitlabandroid.network.GitlabClient;
import io.dongyue.gitlabandroid.network.GitlabSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IssueActivity extends BaseActivity {

    @Bind(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.list)
    RecyclerView issuesListView;

    private IssuesAdapter issuesAdapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);
        initListener();
        initListView();
    }

    private void initListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onOptionsItemSelected(item)) {
                //NavUtils.navigateUpFromSameTask(this);
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initListView() {
        issuesAdapter = new IssuesAdapter();
        issuesAdapter.setOnItemClickListener(new IssuesAdapter.OnMyIssueInfoListener() {
            @Override
            public void onMyIssueInfoClick(MyIssueInfo issueInfo) {
                //TODO: Open IssueDetailActivity
            }
        });
        linearLayoutManager = new LinearLayoutManager(this);
        issuesListView.setLayoutManager(linearLayoutManager);
        issuesListView.setAdapter(issuesAdapter);

        loadData();
    }

    public void loadData() {
        addSubscription(GitlabClient.getInstance().getAllIssues()
                .flatMap(issues ->
                                Observable.from(issues)
                                        .flatMap(
                                                issue ->
                                                        GitlabClient.getInstance()
                                                                .getProject(issue.getProjectId())
                                                                .map(project -> new MyIssueInfo(issue, project))
                                        )
                )
                .toSortedList((info1, info2) ->
                                -info1.getIssue().getCreatedAt().compareTo(
                                        info2.getIssue().getCreatedAt())
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new GitlabSubscriber<List<MyIssueInfo>>() {
                               @Override
                               public void onNext(List<MyIssueInfo> myIssueInfos) {
                                   issuesAdapter.set(myIssueInfos);
                                   swipeRefreshLayout.setRefreshing(false);
                               }
                           }
                ));
    }
}
