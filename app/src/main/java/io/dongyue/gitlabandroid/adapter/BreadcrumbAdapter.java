package io.dongyue.gitlabandroid.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.dongyue.gitlabandroid.R;

/**
 * Shows the current file path
 * Created by Jawnnypoo on 11/22/2015.
 */
public class BreadcrumbAdapter extends RecyclerView.Adapter<BreadcrumbViewHolder> {
    private List<Breadcrumb> mValues;

    public BreadcrumbAdapter() {
        mValues = new ArrayList<>();
        notifyDataSetChanged();
    }

    private final View.OnClickListener onProjectClickListener = v -> {
        int position = (int) v.getTag(R.id.list_position);
        Breadcrumb breadcrumb = getValueAt(position);
        if (breadcrumb != null && breadcrumb.getListener() != null) {
            breadcrumb.getListener().onClick();
        }
    };

    @Override
    public BreadcrumbViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BreadcrumbViewHolder holder = BreadcrumbViewHolder.inflate(parent);
        holder.itemView.setOnClickListener(onProjectClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(final BreadcrumbViewHolder holder, int position) {
        String title = "";
        boolean showArrow = position != mValues.size() - 1;

        Breadcrumb breadcrumb = getValueAt(position);
        if (breadcrumb != null) {
            title = breadcrumb.getTitle();
        }

        holder.bind(title, showArrow);
        holder.itemView.setTag(R.id.list_position, position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setData(Collection<Breadcrumb> breadcrumbs) {
        mValues.clear();
        if (breadcrumbs != null) {
            mValues.addAll(breadcrumbs);
            notifyItemRangeInserted(0, breadcrumbs.size());
        }
        notifyDataSetChanged();
    }

    public Breadcrumb getValueAt(int position) {
        if (position < 0 || position >= mValues.size()) {
            return null;
        }

        return mValues.get(position);
    }

    public static class Breadcrumb {
        private final String mTitle;
        private final Listener mListener;

        public Breadcrumb(String title, Listener listener) {
            mTitle = title;
            mListener = listener;
        }

        public String getTitle() {
            return mTitle;
        }

        public Listener getListener() {
            return mListener;
        }
    }

    public interface Listener {
        void onClick();
    }

}

class BreadcrumbViewHolder extends RecyclerView.ViewHolder {

    public static BreadcrumbViewHolder inflate(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_breadcrumb, parent, false);
        return new BreadcrumbViewHolder(view);
    }

    @Bind(R.id.breadcrumb_text)
    TextView mTextView;
    @Bind(R.id.breadcrumb_arrow)
    ImageView mArrowView;

    public BreadcrumbViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bind(String breadcrumb, boolean showArrow) {
        mTextView.setText(breadcrumb);
        if (showArrow) {
            mTextView.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.darker_gray));
            mArrowView.setVisibility(View.VISIBLE);
        } else {
            mTextView.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.white));
            mArrowView.setVisibility(View.GONE);
        }
    }
}