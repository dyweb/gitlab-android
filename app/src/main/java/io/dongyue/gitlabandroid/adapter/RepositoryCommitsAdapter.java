package io.dongyue.gitlabandroid.adapter;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.dongyue.gitlabandroid.R;
import io.dongyue.gitlabandroid.model.api.Project;
import io.dongyue.gitlabandroid.model.api.RepositoryCommit;
import io.dongyue.gitlabandroid.network.GitlabClient;
import io.dongyue.gitlabandroid.utils.ConversionUtil;
import io.dongyue.gitlabandroid.utils.ImageUtil;
import io.dongyue.gitlabandroid.utils.ViewUtil;
import io.dongyue.gitlabandroid.view.CircleTransformation;

/**
 * Created by cody_local on 2016/3/22.
 */
public class RepositoryCommitsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<RepositoryCommit> RepositoryCommits;
    private int page = 1;
    private boolean finished;

    private OnRepositoryCommitListener listener;

    public RepositoryCommitsAdapter() {
        RepositoryCommits = new ArrayList<>();
    }

    public void add(List<RepositoryCommit> list) {
        if (list.isEmpty()) finished = true;
        RepositoryCommits.addAll(list);
        page++;
        notifyDataSetChanged();
    }

    public void set(List<RepositoryCommit> list) {
        RepositoryCommits = list;
        page = 0;
        finished = false;
        notifyDataSetChanged();
    }

    public int getPage() {
        return page;
    }

    public boolean hasMore() {
        return !finished;
    }

    public interface OnRepositoryCommitListener {
        void onRepositoryCommitClick(RepositoryCommit RepositoryCommit);
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = (int) v.getTag(R.id.list_position);
            if (listener != null) listener.onRepositoryCommitClick(RepositoryCommits.get(pos));
        }
    };

    public void setOnItemClickListener(OnRepositoryCommitListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RepositoryCommitViewHolder holder = RepositoryCommitViewHolder.inflate(parent);
        holder.itemView.setOnClickListener(onClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RepositoryCommit RepositoryCommit = RepositoryCommits.get(position);
        if (holder instanceof RepositoryCommitViewHolder) {
            ((RepositoryCommitViewHolder) holder).bind(RepositoryCommit);
            holder.itemView.setTag(R.id.list_position, position);
        }
    }

    @Override
    public int getItemCount() {
        return RepositoryCommits.size();
    }

}

class RepositoryCommitViewHolder extends RecyclerView.ViewHolder {

    public static RepositoryCommitViewHolder inflate(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project_commit, parent, false);
        return new RepositoryCommitViewHolder(view);
    }

    @Bind(R.id.project_commit_title)
    TextView titleView;
    @Bind(R.id.project_commit_info)
    TextView infoView;
    @Bind(R.id.project_commit_image)
    ImageView imageView;

    public RepositoryCommitViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(RepositoryCommit repositoryCommit) {
        GitlabClient.getPicasso()
                .load(ImageUtil.getAvatarUrl(repositoryCommit.getAuthorEmail(),itemView.getResources().getDimensionPixelSize(R.dimen.image_size)))
                .config(Bitmap.Config.RGB_565)
                .resize(ViewUtil.dp2px(40), ViewUtil.dp2px(40))
                .centerCrop()
                .transform(new CircleTransformation())
                .into(imageView);
        titleView.setText(repositoryCommit.getMessage());
        String date = ConversionUtil.fromDate(repositoryCommit.getCreatedAt());
        infoView.setText(repositoryCommit.getAuthorName() + " authored at " + date);
    }
}
