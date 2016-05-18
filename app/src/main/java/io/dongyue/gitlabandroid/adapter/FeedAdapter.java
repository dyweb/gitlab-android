package io.dongyue.gitlabandroid.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.dongyue.gitlabandroid.App;
import io.dongyue.gitlabandroid.R;
import io.dongyue.gitlabandroid.activity.ProjectActivity;
import io.dongyue.gitlabandroid.model.api.Project;
import io.dongyue.gitlabandroid.model.rss.Entry;
import io.dongyue.gitlabandroid.model.rss.Link;
import io.dongyue.gitlabandroid.network.GitlabClient;
import io.dongyue.gitlabandroid.utils.Logger;
import io.dongyue.gitlabandroid.utils.NavigationManager;
import io.dongyue.gitlabandroid.utils.ViewUtil;
import io.dongyue.gitlabandroid.view.CircleTransformation;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
    private int offset = 0;

    public FeedAdapter(Listener listener) {
        mListener = listener;
        mValues = new ArrayList<>();
    }

    private Object tag = new Object();

    public RecyclerView.OnScrollListener stopLoadingWhenScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                Picasso.with(App.getInstance()).resumeTag(tag);
            } else {
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

    public void addEntries(Collection<Entry> entries) {
        mValues.addAll(entries);
        offset = mValues.size();
        notifyDataSetChanged();
    }

    public void addEntry(Entry entry) {
        mValues.add(entry);
        offset = mValues.size();
        notifyDataSetChanged();
    }

    public void clearEntries() {
        mValues.clear();
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
        holder.bind(tag, getEntry(position));
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
    @Bind(R.id.description)
    TextView mSummaryView;

    public FeedEntryViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);

        mTitleView.setMovementMethod(LinkMovementMethod.getInstance());
        mSummaryView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void bind(Object tag, Entry entry) {
        GitlabClient.getPicasso()
                .load(entry.getThumbnail().getUrl())
                .config(Bitmap.Config.RGB_565)
                .resize(ViewUtil.dp2px(40), ViewUtil.dp2px(40))
                .centerCrop()
                .transform(new CircleTransformation())
                .tag(tag)
                .into(mImageView);

        mTitleView.setText(getLinkfiedTitle(entry));
        mSummaryView.setText(entry.getSummary());
    }

    private SpannableStringBuilder getLinkfiedTitle(Entry entry) {
        Link link = entry.getLink();
        List<String> segments = link.getHref().getPathSegments();
        if (segments.size() < 2) {
            return null;
        }
        final String projectFullName = segments.get(0) + "/" + segments.get(1);//NAMESPACE/PROJECT_NAME
        final String displayedProjectName = segments.get(0) + " / " + segments.get(1); // "NAMESPACE / PROJECT_NAME"

        String title = entry.getTitle();
        SpannableStringBuilder titleBuilder = new SpannableStringBuilder(title);

        linkifyMergeAndIssue(titleBuilder);
        linkifyProjectName(projectFullName, displayedProjectName, title, titleBuilder);
        linkifyUsername(title, titleBuilder);

        return titleBuilder;
    }

    private void linkifyProjectName(final String projectFullName, String displayedProjectName, String title, SpannableStringBuilder titleBuilder) {
        //add links in project name
        int start = 0;
        while ((start = title.indexOf(displayedProjectName, start)) != -1) {
            int end = start + displayedProjectName.length();
            titleBuilder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    startProjectActivity(widget.getContext(), projectFullName);
                }
            }, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            start = end;
        }
    }

    private void linkifyMergeAndIssue(SpannableStringBuilder titleBuilder) {
        //add links in merge requests and issues
        Pattern pattern = Pattern.compile("(merge\\srequest|issue)\\s*(#[\\d]+)");
        Matcher matcher = pattern.matcher(titleBuilder);
        while (matcher.find()) {
            int start = matcher.start(2); //index of '#'
            int end = matcher.end(2);
            final String type = matcher.group(1);
            titleBuilder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    if (type.startsWith("issue")) {
                        //TODO open IssueDetail
                    } else if (type.startsWith("merge request")) {
                        //TODO
                    }
                }
            }, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
    }

    private void linkifyUsername(final String title, SpannableStringBuilder titleBuilder) {
        //add links in username
        int end = title.indexOf(' ');
        titleBuilder.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                String username = title.substring(0, end);
                startUserInfoActivity(widget.getContext(), username);
            }
        }, 0, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    }

    private void startUserInfoActivity(Context context, String username) {
        GitlabClient.getInstance().getUser(username)
                .map(users -> users.get(0)) //username is unique
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(/* TODO: open UserInfo activity */);
    }

    private void startProjectActivity(Context context, String projectFullName) {
        GitlabClient.getInstance().getProject(projectFullName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(project -> NavigationManager.toProject(context, project),
                        error -> Logger.i(error.getMessage()));
    }
}
