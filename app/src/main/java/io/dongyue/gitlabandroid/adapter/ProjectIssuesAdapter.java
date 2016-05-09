package io.dongyue.gitlabandroid.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.dongyue.gitlabandroid.R;
import io.dongyue.gitlabandroid.model.api.Issue;
import io.dongyue.gitlabandroid.network.GitlabClient;
import io.dongyue.gitlabandroid.utils.DateUtils;
import io.dongyue.gitlabandroid.utils.ImageUtil;
import io.dongyue.gitlabandroid.view.CircleTransformation;
import io.dongyue.gitlabandroid.view.viewholder.LoadingFooterViewHolder;

/**
 * Issues adapter
 * Created by Jawn on 7/28/2015.
 */
public class ProjectIssuesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FOOTER_COUNT = 1;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private int page = 1;

    public interface Listener {
        void onIssueClicked(Issue issue);
    }

    private Listener mListener;
    private ArrayList<Issue> mValues;
    private boolean mLoading = false;

    public ProjectIssuesAdapter(Listener listener) {
        mListener = listener;
        mValues = new ArrayList<>();
    }

    private final View.OnClickListener onProjectClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag(R.id.list_position);
            mListener.onIssueClicked(getValueAt(position));
        }
    };

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ITEM:
                ProjectIssueViewHolder holder = ProjectIssueViewHolder.inflate(parent);
                holder.itemView.setOnClickListener(onProjectClickListener);
                return holder;
            case TYPE_FOOTER:
                return LoadingFooterViewHolder.inflate(parent);
        }
        throw new IllegalStateException("No holder for view type " + viewType);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProjectIssueViewHolder) {
            Issue issue = getValueAt(position);
            ((ProjectIssueViewHolder) holder).bind(issue);
            holder.itemView.setTag(R.id.list_position, position);
        } else if (holder instanceof LoadingFooterViewHolder) {
            ((LoadingFooterViewHolder) holder).bind(mLoading);
        } else {
            throw new IllegalStateException("What is this holder?");
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size() + FOOTER_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mValues.size()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    public int getPage() {
        return page;
    }

    public void setIssues(Collection<Issue> issues) {
        mValues.clear();
        addIssues(issues);
        page = 1;
    }

    public void addIssues(Collection<Issue> issues) {
        if (issues != null) {
            mValues.addAll(issues);
            page++;
        }
        notifyDataSetChanged();
    }

    public void addIssue(Issue issue) {
        mValues.add(0, issue);
        notifyItemInserted(0);
    }

    public void updateIssue(Issue issue) {
        int indexToDelete = -1;
        for (int i=0; i<mValues.size(); i++) {
            if (mValues.get(i).getId() == issue.getId()) {
                indexToDelete = i;
                break;
            }
        }
        if (indexToDelete != -1) {
            mValues.remove(indexToDelete);
            mValues.add(indexToDelete, issue);
        }
        notifyItemChanged(indexToDelete);
    }

    public Issue getValueAt(int position) {
        return mValues.get(position);
    }

    public void setLoading(boolean loading) {
        mLoading = loading;
        notifyItemChanged(mValues.size());
    }
}

class ProjectIssueViewHolder extends RecyclerView.ViewHolder {

    public static ProjectIssueViewHolder inflate(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project_issue, parent, false);
        return new ProjectIssueViewHolder(view);
    }

    @Bind(R.id.issue_image)
    ImageView mImageView;
    @Bind(R.id.issue_message)
    TextView mMessageView;
    @Bind(R.id.issue_creator) TextView mCreatorView;

    public ProjectIssueViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bind(Issue issue) {

        if (issue.getAssignee() != null) {
            GitlabClient.getPicasso()
                    .load(ImageUtil.getAvatarUrl(issue.getAssignee(), itemView.getResources().getDimensionPixelSize(R.dimen.image_size)))
                    .transform(new CircleTransformation())
                    .into(mImageView);
        } else {
            mImageView.setImageBitmap(null);
        }

        mMessageView.setText(issue.getTitle());

        String time = "";
        if (issue.getCreatedAt() != null) {
            time += DateUtils.getRelativeTimeSpanString(itemView.getContext(), issue.getCreatedAt());
        }
        String author = "";
        if (issue.getAuthor() != null) {
            author += issue.getAuthor().getUsername();
        }
        String id = "";
        long issueId = issue.getIid();
        if (issueId < 1) {
            issueId = issue.getId();
        }
        id = "#" + issueId;

        mCreatorView.setText(String.format(itemView.getContext().getString(R.string.opened_time), id, time, author));
    }
}
