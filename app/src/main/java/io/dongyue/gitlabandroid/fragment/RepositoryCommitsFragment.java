package io.dongyue.gitlabandroid.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.dongyue.gitlabandroid.R;
import io.dongyue.gitlabandroid.adapter.RepositoryCommitsAdapter;
import io.dongyue.gitlabandroid.model.api.RepositoryCommit;
import io.dongyue.gitlabandroid.network.GitlabClient;
import io.dongyue.gitlabandroid.network.GitlabSubscriber;
import io.dongyue.gitlabandroid.utils.ToastUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by cody_local on 2016/3/22.
 */
public class RepositoryCommitsFragment extends BaseFragment {

    private static final String PROJECT_ID = "project_id";
    boolean mLoading;
    private long projectId;

    @Bind(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.list)
    RecyclerView repositoryCommitsListView;

    private RepositoryCommitsAdapter repositoryCommitsAdapter;
    private LinearLayoutManager linearLayoutManager;

    private final RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = linearLayoutManager.getChildCount();
            int totalItemCount = linearLayoutManager.getItemCount();
            int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
            if (firstVisibleItem + visibleItemCount >= totalItemCount && !mLoading && repositoryCommitsAdapter.hasMore()) {
                loadMore();
            }
        }
    };

    private void loadData(){
        swipeRefreshLayout.setRefreshing(true);
        addSubscription(GitlabClient.getInstance().getCommits(projectId, null, 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new GitlabSubscriber<List<RepositoryCommit>>() {
                    @Override
                    public void onNext(List<RepositoryCommit> list) {
                        repositoryCommitsAdapter.set(list);
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }));
    }

    private void loadMore(){
        mLoading = true;
        swipeRefreshLayout.setRefreshing(true);
        addSubscription(GitlabClient.getInstance().getCommits(projectId,null,repositoryCommitsAdapter.getPage()+1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new GitlabSubscriber<List<RepositoryCommit>>() {
                    @Override
                    public void onNext(List<RepositoryCommit> list) {
                        repositoryCommitsAdapter.add(list);
                        mLoading = false;
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mLoading = false;
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }));
    }

    public static RepositoryCommitsFragment newInstance(long projectId) {
        RepositoryCommitsFragment fragment = new RepositoryCommitsFragment();
        Bundle args = new Bundle();
        args.putLong(PROJECT_ID,projectId);
        fragment.setArguments(args);
        return fragment;
    }

    public RepositoryCommitsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            projectId = getArguments().getLong(PROJECT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project_commits, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);



        repositoryCommitsAdapter = new RepositoryCommitsAdapter();
        repositoryCommitsAdapter.setOnItemClickListener(repositoryCommit -> ToastUtils.showShort(repositoryCommit.getMessage()));
        linearLayoutManager = new LinearLayoutManager(getActivity());
        repositoryCommitsListView.setLayoutManager(linearLayoutManager);
        repositoryCommitsListView.setAdapter(repositoryCommitsAdapter);

        repositoryCommitsListView.addOnScrollListener(mOnScrollListener);
        swipeRefreshLayout.setOnRefreshListener(this::loadData);

        loadData();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}

