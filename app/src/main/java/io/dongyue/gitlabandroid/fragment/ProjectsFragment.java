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
import io.dongyue.gitlabandroid.utils.NavigationManager;
import io.dongyue.gitlabandroid.utils.eventbus.events.CloseDrawerEvent;
import io.dongyue.gitlabandroid.utils.eventbus.RxBus;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProjectsFragment extends BaseFragment {

    @Bind(R.id.swipe_layout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.list) RecyclerView projectsListView;

    private ProjectsAdapter projectsAdapter;
    private LinearLayoutManager linearLayoutManager;

    public static ProjectsFragment newInstance() {
        ProjectsFragment fragment = new ProjectsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        projectsAdapter.setOnItemClickListener(new ProjectsAdapter.OnProjectListener() {
            @Override
            public void onProjectClick(Project project) {
                NavigationManager.toProject(getActivity(),project);
            }
        });
        linearLayoutManager = new LinearLayoutManager(getActivity());
        projectsListView.setLayoutManager(linearLayoutManager);
        projectsListView.setAdapter(projectsAdapter);

        loadData();

    }

    private void loadData(){
        addSubscription(GitlabClient.getInstance().getAllProjects()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new GitlabSubscriber<List<Project>>() {
                    @Override
                    public void onNext(List<Project> list) {
                        projectsAdapter.set(list);
                    }
                }));
    }

}
