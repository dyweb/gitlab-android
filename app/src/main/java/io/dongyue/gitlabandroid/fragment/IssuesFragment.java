package io.dongyue.gitlabandroid.fragment;


import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.uncod.android.bypass.Bypass;
import io.dongyue.gitlabandroid.R;
import io.dongyue.gitlabandroid.activity.ProjectActivity;
import io.dongyue.gitlabandroid.adapter.ProjectIssuesAdapter;
import io.dongyue.gitlabandroid.model.api.Issue;
import io.dongyue.gitlabandroid.model.api.Project;
import io.dongyue.gitlabandroid.network.GitlabClient;
import io.dongyue.gitlabandroid.network.GitlabSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IssuesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IssuesFragment extends BaseFragment {

    @Bind(R.id.swipe_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.list) RecyclerView mIssueListView;
    @Bind(R.id.message_text) TextView mMessageView;
    @Bind(R.id.issue_spinner) Spinner mSpinner;


    private Project mProject;
    private ProjectIssuesAdapter mIssuesAdapter;
    private LinearLayoutManager mIssuesLayoutManager;

    String mState;
    private String[] mStates;
    private boolean mLoading = false;

    private final RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = mIssuesLayoutManager.getChildCount();
            int totalItemCount = mIssuesLayoutManager.getItemCount();
            int firstVisibleItem = mIssuesLayoutManager.findFirstVisibleItemPosition();
            if (firstVisibleItem + visibleItemCount >= totalItemCount && !mLoading) {
                loadMore();
            }
        }
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment IssuesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IssuesFragment newInstance() {
        IssuesFragment fragment = new IssuesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public IssuesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mState=getResources().getString(R.string.issue_state_value_default);
        mStates=getResources().getStringArray(R.array.issue_state_values);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_issues, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mIssuesAdapter=new ProjectIssuesAdapter(listener);
        mIssuesLayoutManager=new LinearLayoutManager(getActivity());
        mIssueListView.setLayoutManager(mIssuesLayoutManager);
        mIssueListView.setAdapter(mIssuesAdapter);
        mIssueListView.addOnScrollListener(mOnScrollListener);

        mSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,mStates));
        mSpinner.setOnItemSelectedListener(mSpinnerItemSelectedListener);

        mSwipeRefreshLayout.setOnRefreshListener(this::loadData);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity() instanceof ProjectActivity){
            mProject = ((ProjectActivity)getActivity()).getProject();
        }
        mSwipeRefreshLayout.setRefreshing(true);
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void loadMore(){
        mLoading=true;
        mIssuesAdapter.setLoading(true);
        addSubscription(GitlabClient.getInstance().getIssues(mProject.getId(),mState,mIssuesAdapter.getPage()+1)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new GitlabSubscriber<List<Issue>>() {
                    @Override
                    public void onNext(List<Issue> issues) {
                        mLoading=false;
                        mIssuesAdapter.setLoading(false);
                        mIssuesAdapter.addIssues(issues);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mIssuesAdapter.setLoading(false);
                        mLoading=false;
                    }
                }));
    }

    private void loadData(){
        mLoading=true;
        mSwipeRefreshLayout.setRefreshing(true);
        addSubscription(GitlabClient.getInstance().getIssues(mProject.getId(), mState)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new GitlabSubscriber<List<Issue>>() {
                    @Override
                    public void onNext(List<Issue> issues) {
                        mLoading=false;
                        mSwipeRefreshLayout.setRefreshing(false);
                        mIssuesAdapter.setIssues(issues);
                        if (issues.isEmpty()) {
                            mMessageView.setVisibility(View.VISIBLE);
                            mMessageView.setText("no issues");
                        } else {
                            mMessageView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mLoading=false;
                        mSwipeRefreshLayout.setRefreshing(false);
                        mMessageView.setVisibility(View.VISIBLE);
                        mMessageView.setText("connection failed");
                        mIssuesAdapter.setIssues(null);
                    }
                }));
    }

    private final AdapterView.OnItemSelectedListener mSpinnerItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mState = mStates[position];
            loadData();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    };

    private ProjectIssuesAdapter.Listener listener = new ProjectIssuesAdapter.Listener() {
        @Override
        public void onIssueClicked(Issue issue) {
            //TODO: goto issue
        }
    };
}
