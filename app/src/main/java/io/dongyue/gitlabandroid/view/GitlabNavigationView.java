package io.dongyue.gitlabandroid.view;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.NavigationView;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;

import butterknife.ButterKnife;
import io.dongyue.gitlabandroid.R;
import io.dongyue.gitlabandroid.activity.HomeActivity;
import io.dongyue.gitlabandroid.activity.IssueActivity;
import io.dongyue.gitlabandroid.model.api.Issue;
import io.dongyue.gitlabandroid.utils.NavigationManager;
import io.dongyue.gitlabandroid.utils.eventbus.RxBus;
import io.dongyue.gitlabandroid.utils.eventbus.events.CloseDrawerEvent;

/**
 * Created by Brotherjing on 2016/3/6.
 */
public class GitlabNavigationView extends NavigationView {

    public GitlabNavigationView(Context context) {
        super(context);
        init();
    }

    public GitlabNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GitlabNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(){
        inflateMenu(R.menu.activity_home_drawer);
        View header = inflateHeaderView(R.layout.nav_header_home);
        ButterKnife.bind(this, header);
        header.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationManager.toUserInfo(getContext());
            }
        });

        setNavigationItemSelectedListener(onNavigationItemSelectedListener);
        //setSelectedItem();
    }

    private OnNavigationItemSelectedListener onNavigationItemSelectedListener = new OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()){
                case R.id.nav_activity:
                    break;
                case R.id.nav_issue:
                    NavigationManager.toIssueList(getContext());break;
                case R.id.nav_settings:
                    NavigationManager.toSettings(getContext());break;
                case R.id.nav_quit:
                    ((Activity)getContext()).finish();break;
                default:break;
            }
            RxBus.getBus().post(new CloseDrawerEvent());
            return true;
        }
    };
}
