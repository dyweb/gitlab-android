package io.dongyue.gitlabandroid.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
import io.dongyue.gitlabandroid.model.rss.Entry;
import io.dongyue.gitlabandroid.network.GitlabClient;
import io.dongyue.gitlabandroid.view.CircleTransformation;

/**
 * Adapts the feeds
 */
public class FeedAdapter extends RecyclerView.Adapter<FeedEntryViewHolder> {

    public interface Listener {
        void onFeedEntryClicked(Entry entry);
    }
    private Listener mListener;

    private ArrayList<Entry> mValues;

    public FeedAdapter(Listener listener) {
        mListener = listener;
        mValues = new ArrayList<>();
    }

    private final View.OnClickListener mOnItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag(R.id.list_position);
            mListener.onFeedEntryClicked(getEntry(position));
        }
    };

    public void setEntries(Collection<Entry> entries) {
        mValues.clear();
        if (entries != null) {
            mValues.addAll(entries);
        }
        notifyDataSetChanged();
    }

    @Override
    public FeedEntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FeedEntryViewHolder holder = FeedEntryViewHolder.inflate(parent);
        holder.itemView.setOnClickListener(mOnItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(final FeedEntryViewHolder holder, int position) {
        holder.itemView.setTag(R.id.list_position, position);
        holder.bind(getEntry(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private Entry getEntry(int position) {
        return mValues.get(position);
    }
}

class FeedEntryViewHolder extends RecyclerView.ViewHolder {

    public static FeedEntryViewHolder inflate(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_entry, parent, false);
        return new FeedEntryViewHolder(view);
    }

    @Bind(R.id.image)
    ImageView mImageView;
    @Bind(R.id.title)
    TextView mTitleView;
    @Bind(R.id.description) TextView mSummaryView;

    public FeedEntryViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bind(Entry entry) {
        GitlabClient.getPicasso()
                .load(entry.getThumbnail().getUrl())
                .transform(new CircleTransformation())
                .into(mImageView);

        mTitleView.setText(Html.fromHtml(entry.getTitle()));
        mSummaryView.setText(Html.fromHtml(entry.getSummary()));
    }
}
