package io.dongyue.gitlabandroid.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.uncod.android.bypass.Bypass;
import io.dongyue.gitlabandroid.R;
import io.dongyue.gitlabandroid.activity.ProjectActivity;
import io.dongyue.gitlabandroid.adapter.BreadcrumbAdapter;
import io.dongyue.gitlabandroid.adapter.FilesAdapter;
import io.dongyue.gitlabandroid.model.api.Project;
import io.dongyue.gitlabandroid.model.api.RepositoryTreeObject;
import io.dongyue.gitlabandroid.network.GitlabClient;
import io.dongyue.gitlabandroid.network.GitlabSubscriber;
import io.dongyue.gitlabandroid.utils.eventbus.RxBus;
import io.dongyue.gitlabandroid.utils.eventbus.events.SwitchBranchEvent;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilesFragment extends BaseFragment {

    @Bind(R.id.swipe_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.list) RecyclerView mFilesListView;
    @Bind(R.id.breadcrumb) RecyclerView mBreadcrumbListView;
    @Bind(R.id.message_text) TextView mMessageView;

    private FilesAdapter filesAdapter;
    private BreadcrumbAdapter breadcrumbAdapter;

    private String mCurrentPath = "";
    private Project project;
    private String mBranchName;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FilesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FilesFragment newInstance() {
        FilesFragment fragment = new FilesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public FilesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSubscription(RxBus.getBus().observeEvents(SwitchBranchEvent.class)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(switchBranchEvent -> {
                    mBranchName = switchBranchEvent.getBranchName();
                    loadData();
                }));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_files, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        filesAdapter=new FilesAdapter(listener);
        mFilesListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFilesListView.setAdapter(filesAdapter);

        breadcrumbAdapter=new BreadcrumbAdapter();
        mBreadcrumbListView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        mBreadcrumbListView.setAdapter(breadcrumbAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(this::loadData);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity() instanceof ProjectActivity){
            project = ((ProjectActivity)getActivity()).getProject();
            mBranchName=((ProjectActivity)getActivity()).getBranchName();
        }

        mSwipeRefreshLayout.setRefreshing(true);
        loadData();
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void loadData(){
        loadData("");
    }

    private void loadData(String path){
        mSwipeRefreshLayout.setRefreshing(true);
        addSubscription(GitlabClient.getInstance().getTree(project.getId(),
                mBranchName, path).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new GitlabSubscriber<List<RepositoryTreeObject>>() {
                    @Override
                    public void onNext(List<RepositoryTreeObject> repositoryTreeObjects) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        filesAdapter.setData(repositoryTreeObjects);
                        mFilesListView.scrollToPosition(0);
                        mCurrentPath = path;
                        if (repositoryTreeObjects.isEmpty()) {
                            mMessageView.setVisibility(View.VISIBLE);
                            mMessageView.setText("no files found.");
                        } else {
                            mMessageView.setVisibility(View.GONE);
                        }
                        updateBreadcrumbs();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mSwipeRefreshLayout.setRefreshing(false);
                        mMessageView.setVisibility(View.VISIBLE);
                        mMessageView.setText("connection failed");
                        mCurrentPath=path;
                        updateBreadcrumbs();
                    }
                }));
    }

    private void updateBreadcrumbs(){
        List<BreadcrumbAdapter.Breadcrumb> breadcrumbs=new ArrayList<>();
        breadcrumbs.add(new BreadcrumbAdapter.Breadcrumb("ROOT",this::loadData));
        String newPath="";
        String []segments=mCurrentPath.split("/");
        for(String segment:segments){
            if(segment.isEmpty())continue;
            newPath+=segment+"/";
            final String path=newPath;
            breadcrumbs.add(new BreadcrumbAdapter.Breadcrumb(segment, () -> loadData(path)));
        }
        breadcrumbAdapter.setData(breadcrumbs);
        mBreadcrumbListView.scrollToPosition(breadcrumbAdapter.getItemCount());
    }

    private FilesAdapter.Listener listener=new FilesAdapter.Listener() {
        @Override
        public void onFolderClicked(RepositoryTreeObject treeItem) {
            loadData(mCurrentPath+treeItem.getName()+"/");
        }

        @Override
        public void onFileClicked(RepositoryTreeObject treeItem) {
            //TODO: goto file activity
        }

        @Override
        public void onCopyClicked(RepositoryTreeObject treeItem) {
            //TODO: copy
        }

        @Override
        public void onShareClicked(RepositoryTreeObject treeItem) {

        }

        @Override
        public void onOpenInBrowserClicked(RepositoryTreeObject treeItem) {

        }
    };
}
