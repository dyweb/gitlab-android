package io.dongyue.gitlabandroid.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.dongyue.gitlabandroid.R;
import io.dongyue.gitlabandroid.model.MyIssueInfo;
import io.dongyue.gitlabandroid.model.api.Issue;
import io.dongyue.gitlabandroid.model.api.Project;
import io.dongyue.gitlabandroid.network.GitlabClient;
import io.dongyue.gitlabandroid.view.CircleTransformation;

public class IssuesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MyIssueInfo> issueInfos;
    private OnMyIssueInfoListener listener;

    public IssuesAdapter() {
        issueInfos = new ArrayList<>();
    }

    public void add(List<MyIssueInfo> list) {
        issueInfos.addAll(list);
        notifyDataSetChanged();
    }

    public void set(List<MyIssueInfo> list) {
        issueInfos = list;
        notifyDataSetChanged();
    }

    public interface OnMyIssueInfoListener {
        void onMyIssueInfoClick(MyIssueInfo issueInfo);
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = (int) v.getTag(R.id.list_position);
            if (listener != null) listener.onMyIssueInfoClick(issueInfos.get(pos));
        }
    };

    public void setOnItemClickListener(OnMyIssueInfoListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        IssueViewHolder holder = IssueViewHolder.inflate(parent);
        holder.itemView.setOnClickListener(onClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyIssueInfo issueInfo = issueInfos.get(position);
        if (holder instanceof IssueViewHolder) {
            ((IssueViewHolder) holder).bind(issueInfo);
            holder.itemView.setTag(R.id.list_position, position);
        }
    }

    @Override
    public int getItemCount() {
        return issueInfos.size();
    }

}

class IssueViewHolder extends RecyclerView.ViewHolder {

    public static IssueViewHolder inflate(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_issue, parent, false);
        return new IssueViewHolder(view);
    }

    @Bind(R.id.project_title)
    TextView projectTitleView;
    @Bind(R.id.issue_title)
    TextView issueTitleView;
    @Bind(R.id.issue_author)
    TextView issueAuthorView;
    @Bind(R.id.issue_create_time)
    TextView issueTimeView;
    @Bind(R.id.issue_id)
    TextView issueIdView;

    public IssueViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(MyIssueInfo issueInfo) {
        Issue issue = issueInfo.getIssue();
        projectTitleView.setText(issueInfo.getProject().getNameWithNamespace());
        issueTitleView.setText(issue.getTitle());
        issueIdView.setText(String.format(issueTitleView.getResources().getString(R.string.issue_id), issue.getIid()));
        issueAuthorView.setText(String.format(issueAuthorView.getResources().getString(R.string.issue_author), issue.getAuthor().getName()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/mm/dd");
        String dateStr = dateFormat.format(issue.getCreatedAt());
        issueTimeView.setText(dateStr);
        //TODO: add issue state and label; add links to author and project
    }
}