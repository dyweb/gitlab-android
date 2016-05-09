package io.dongyue.gitlabandroid.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.dongyue.gitlabandroid.R;
import io.dongyue.gitlabandroid.activity.ProjectActivity;
import io.dongyue.gitlabandroid.adapter.MemberAdapter;
import io.dongyue.gitlabandroid.model.api.Member;
import io.dongyue.gitlabandroid.model.api.Project;
import io.dongyue.gitlabandroid.network.GitlabClient;
import io.dongyue.gitlabandroid.network.GitlabSubscriber;
import io.dongyue.gitlabandroid.view.viewholder.ProjectMemberViewHolder;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProjectMemberFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProjectMemberFragment extends BaseFragment {

    @Bind(R.id.swipe_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.list) RecyclerView mMembersListView;
    @Bind(R.id.message_text) TextView mMessageView;

    private Project mProject;
    private MemberAdapter mAdapter;
    private GridLayoutManager mProjectLayoutManager;
    private Member mMember;
    private boolean mLoading = false;

    private final RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = mProjectLayoutManager.getChildCount();
            int totalItemCount = mProjectLayoutManager.getItemCount();
            int firstVisibleItem = mProjectLayoutManager.findFirstVisibleItemPosition();
            if (firstVisibleItem + visibleItemCount >= totalItemCount && !mLoading) {
                loadMore();
            }
        }
    };

    public static ProjectMemberFragment newInstance() {
        ProjectMemberFragment fragment = new ProjectMemberFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ProjectMemberFragment() {
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
        return inflater.inflate(R.layout.fragment_members, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mAdapter=new MemberAdapter(listener);
        mProjectLayoutManager=new GridLayoutManager(getActivity(),2);
        mProjectLayoutManager.setSpanSizeLookup(mAdapter.getSpanSizeLookup());
        mMembersListView.setLayoutManager(mProjectLayoutManager);
        mMembersListView.setAdapter(mAdapter);
        mMembersListView.addOnScrollListener(mOnScrollListener);

        mSwipeRefreshLayout.setOnRefreshListener(this::loadData);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity() instanceof ProjectActivity){
            mProject= ((ProjectActivity)getActivity()).getProject();
        }
        mSwipeRefreshLayout.setRefreshing(true);
        setNamespace();
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void loadData(){
        if(mSwipeRefreshLayout!=null)mSwipeRefreshLayout.setRefreshing(true);
        mLoading=true;
        addSubscription(GitlabClient.getInstance().getProjectMembers(mProject.getId())
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new GitlabSubscriber<List<Member>>() {
                    @Override
                    public void onNext(List<Member> members) {
                        if(mSwipeRefreshLayout!=null)mSwipeRefreshLayout.setRefreshing(false);
                        mLoading=false;
                        if(members.isEmpty()){
                            mMessageView.setText("no project mmbers found");
                            mMessageView.setVisibility(View.VISIBLE);
                        }else {
                            mMessageView.setVisibility(View.GONE);
                        }
                        mAdapter.setProjectMembers(members);
                    }
                }));
    }

    private void loadMore(){
        if(mSwipeRefreshLayout!=null)mSwipeRefreshLayout.setRefreshing(true);
        mLoading=true;
        addSubscription(GitlabClient.getInstance().getProjectMembers(mProject.getId(),mAdapter.getPage()+1)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new GitlabSubscriber<List<Member>>() {
                    @Override
                    public void onNext(List<Member> members) {
                        if(mSwipeRefreshLayout!=null)mSwipeRefreshLayout.setRefreshing(false);
                        mLoading=false;
                        mAdapter.addProjectMembers(members);
                    }
                }));
    }

    private MemberAdapter.Listener listener=new MemberAdapter.Listener() {
        @Override
        public void onProjectMemberClicked(Member member, ProjectMemberViewHolder memberGroupViewHolder) {
            //TODO: goto user
        }

        @Override
        public void onRemoveMember(Member member) {

        }

        @Override
        public void onChangeAccess(Member member) {

        }

        @Override
        public void onSeeGroupClicked() {

        }
    };


    private void setNamespace() {
        if (mProject == null) {
            return;
        }

        //If there is an owner, then there is no group
        if (mProject.belongsToGroup()) {
            mAdapter.setNamespace(mProject.getNamespace());
        } else {
            mAdapter.setNamespace(null);
        }
    }
}
