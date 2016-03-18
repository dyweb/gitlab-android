package io.dongyue.gitlabandroid.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.dongyue.gitlabandroid.App;
import io.dongyue.gitlabandroid.R;
import io.dongyue.gitlabandroid.model.rss.Entry;
import io.dongyue.gitlabandroid.network.GitlabClient;
import io.dongyue.gitlabandroid.utils.ViewUtil;
import io.dongyue.gitlabandroid.view.CircleTransformation;

/**
 * Adapts the feeds
 */
public class FeedAdapter extends RecyclerView.Adapter<FeedEntryViewHolder> {

    public static final int LIMIT = 20;

    public interface Listener {
        void onFeedEntryClicked(Entry entry);
    }
    private Listener mListener;

    private ArrayList<Entry> mValues;
    private int offset=0;

    public FeedAdapter(Listener listener) {
        mListener = listener;
        mValues = new ArrayList<>();
    }

    private Object tag = new Object();

    public RecyclerView.OnScrollListener stopLoadingWhenScrollListener = new RecyclerView.OnScrollListener(){
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE)
            {
                Picasso.with(App.getInstance()).resumeTag(tag);
            }
            else
            {
                Picasso.with(App.getInstance()).pauseTag(tag);
            }
        }
    };

    private final View.OnClickListener mOnItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag(R.id.list_position);
            mListener.onFeedEntryClicked(getEntry(position));
        }
    };

    public void addEntries(Collection<Entry> entries){
        mValues.addAll(entries);
        offset = mValues.size();
        notifyDataSetChanged();
    }

    public void addEntry(Entry entry){
        mValues.add(entry);
        offset = mValues.size();
        notifyDataSetChanged();
    }

    public void setEntries(Collection<Entry> entries) {
        mValues.clear();
        if (entries != null) {
            mValues.addAll(entries);
        }
        offset = mValues.size();
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
        holder.bind(tag,getEntry(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private Entry getEntry(int position) {
        return mValues.get(position);
    }

    public int getOffset() {
        return offset;
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

    public void bind(Object tag,Entry entry) {
        GitlabClient.getPicasso()
                .load(entry.getThumbnail().getUrl())
                .config(Bitmap.Config.RGB_565)
                .resize(ViewUtil.dp2px(40), ViewUtil.dp2px(40))
                .centerCrop()
                .transform(new CircleTransformation())
                .tag(tag)
                .into(mImageView);

        mTitleView.setText(Html.fromHtml(entry.getTitle()));
        mSummaryView.setText(Html.fromHtml(entry.getSummary()));
    }
}
