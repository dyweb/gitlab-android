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
import io.dongyue.gitlabandroid.adapter.ProjectsAdapter;
import io.dongyue.gitlabandroid.model.api.Project;
import io.dongyue.gitlabandroid.network.GitlabClient;
import io.dongyue.gitlabandroid.network.GitlabSubscriber;
import io.dongyue.gitlabandroid.utils.Logger;
import io.dongyue.gitlabandroid.utils.NavigationManager;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProjectsFragment extends BaseFragment {

    boolean mLoading;

    @Bind(R.id.swipe_layout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.list) RecyclerView projectsListView;

    private ProjectsAdapter projectsAdapter;
    private LinearLayoutManager linearLayoutManager;

    private final RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = linearLayoutManager.getChildCount();
            int totalItemCount = linearLayoutManager.getItemCount();
            int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
            if (firstVisibleItem + visibleItemCount >= totalItemCount && !mLoading && projectsAdapter.hasMore()) {
                loadMore();
            }
        }
    };

    private void loadData(){
        swipeRefreshLayout.setRefreshing(true);
        addSubscription(GitlabClient.getInstance().getAllProjects()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new GitlabSubscriber<List<Project>>() {
                    @Override
                    public void onNext(List<Project> list) {
                        projectsAdapter.set(list);
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
        addSubscription(GitlabClient.getInstance().getAllProjects(projectsAdapter.getPage()+1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new GitlabSubscriber<List<Project>>() {
                    @Override
                    public void onNext(List<Project> list) {
                        projectsAdapter.add(list);
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

    public static ProjectsFragment newInstance() {
        return new ProjectsFragment();
    }

    public ProjectsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_projects, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        projectsAdapter = new ProjectsAdapter();
        projectsAdapter.setOnItemClickListener(project -> NavigationManager.toProject(ProjectsFragment.this.getActivity(), project));
        linearLayoutManager = new LinearLayoutManager(getActivity());
        projectsListView.setLayoutManager(linearLayoutManager);
        projectsListView.setAdapter(projectsAdapter);

        projectsListView.addOnScrollListener(mOnScrollListener);
        swipeRefreshLayout.setOnRefreshListener(this::loadData);

        loadData();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
