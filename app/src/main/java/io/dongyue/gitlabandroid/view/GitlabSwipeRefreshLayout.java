package io.dongyue.gitlabandroid.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import io.dongyue.gitlabandroid.R;

/**
 * Created by Brotherjing on 2016/3/5.
 */
public class GitlabSwipeRefreshLayout extends SwipeRefreshLayout {

    public GitlabSwipeRefreshLayout(Context context) {
        super(context);
        init();
    }

    public GitlabSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
    }

}
