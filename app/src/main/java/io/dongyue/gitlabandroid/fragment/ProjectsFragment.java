package io.dongyue.gitlabandroid.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import io.dongyue.gitlabandroid.utils.ToastUtils;
import retrofit.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        linearLayoutManager = new LinearLayoutManager(getActivity());
        projectsListView.setLayoutManager(linearLayoutManager);
        projectsListView.setAdapter(projectsAdapter);

        loadData();
    }

    private void loadData(){
        addSubscription(GitlabClient.getInstance().getAllProjects()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Project>>() {
                    @Override
                    public void call(List<Project> list) {
                        Log.i("yj", "data get "+list.size());
                        projectsAdapter.set(list);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                        if (e instanceof HttpException) {
                            ToastUtils.showShort("error code " + ((HttpException) e).code());
                        }else{
                            if(TextUtils.isEmpty(e.getMessage()))
                                ToastUtils.showShort("not connected");
                            else
                                ToastUtils.showShort(e.getMessage());
                        }
                        e.printStackTrace();
                    }
                }));
    }

}
