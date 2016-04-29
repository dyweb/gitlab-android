package io.dongyue.gitlabandroid.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.nio.charset.Charset;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.uncod.android.bypass.Bypass;
import io.dongyue.gitlabandroid.R;
import io.dongyue.gitlabandroid.activity.ProjectActivity;
import io.dongyue.gitlabandroid.activity.base.BaseActivity;
import io.dongyue.gitlabandroid.model.api.Project;
import io.dongyue.gitlabandroid.model.api.RepositoryFile;
import io.dongyue.gitlabandroid.model.api.RepositoryTreeObject;
import io.dongyue.gitlabandroid.network.GitlabClient;
import io.dongyue.gitlabandroid.network.GitlabSubscriber;
import io.dongyue.gitlabandroid.utils.PicassoImageGetter;
import io.dongyue.gitlabandroid.utils.eventbus.RxBus;
import io.dongyue.gitlabandroid.utils.eventbus.events.SwitchBranchEvent;
import io.dongyue.gitlabandroid.view.GitlabSwipeRefreshLayout;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OverviewFragment extends BaseFragment{

    @Bind(R.id.project_title) TextView tvTitle;
    @Bind(R.id.project_readme) TextView tvReadme;
    @Bind(R.id.refresh_layout) GitlabSwipeRefreshLayout swipeRefreshLayout;

    private Bypass bypass;
    private Project project;
    private String mBranchName;

    public static OverviewFragment newInstance() {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public OverviewFragment() {
        // Required empty public constructor
    }

    private void loadData(){
        addSubscription(GitlabClient.getInstance().getTree(project.getId(), mBranchName, null)
                .flatMap((List<RepositoryTreeObject> repositoryTreeObjects) -> {
                    for(RepositoryTreeObject item:repositoryTreeObjects){
                        if(item.getName().equalsIgnoreCase("README.md")){
                            return GitlabClient.getInstance().getFile(project.getId(),item.getName(),mBranchName);
                        }
                    }
                    return Observable.just(RepositoryFile.empty());
                })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new GitlabSubscriber<RepositoryFile>() {
                    @Override
                    public void onNext(RepositoryFile repositoryFile) {
                        swipeRefreshLayout.setRefreshing(false);
                        String text = new String(Base64.decode(repositoryFile.getContent(),Base64.DEFAULT), Charset.forName("UTF-8"));
                        tvReadme.setText(bypass.markdownToSpannable(text,new PicassoImageGetter(tvReadme,GitlabClient.getPicasso())));
                    }
                }));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSubscription(RxBus.getBus().observeEvents(SwitchBranchEvent.class)
            .observeOn(AndroidSchedulers.mainThread()).subscribe(switchBranchEvent -> {
                mBranchName=switchBranchEvent.getBranchName();
                loadData();
            }));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bypass = new Bypass(getActivity());
        if(getActivity() instanceof ProjectActivity){
            project = ((ProjectActivity)getActivity()).getProject();
            mBranchName=((ProjectActivity)getActivity()).getBranchName();
        }

        tvTitle.setText(project.getName());
        swipeRefreshLayout.setRefreshing(true);
        loadData();
    }
}
