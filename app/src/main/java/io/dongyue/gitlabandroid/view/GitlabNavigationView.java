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
        ButterKnife.bind(this,header);

        setNavigationItemSelectedListener(onNavigationItemSelectedListener);
        setSelectedItem();
    }

    private void setSelectedItem(){
        if(getContext() instanceof HomeActivity)
            getMenu().findItem(R.id.nav_project).setChecked(true);
        else if(getContext() instanceof IssueActivity)
            getMenu().findItem(R.id.nav_issue).setChecked(true);
    }

    private OnNavigationItemSelectedListener onNavigationItemSelectedListener = new OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()){
                case R.id.nav_project:
                    if(getContext() instanceof HomeActivity){

                    }else{
                        NavigationManager.toProjectList(getContext());
                        ((Activity)getContext()).finish();
                    }
                    break;
                case R.id.nav_issue:
                    if(getContext() instanceof IssueActivity){

                    }else{
                        NavigationManager.toIssueList(getContext());
                        ((Activity)getContext()).finish();
                    }
                default:break;
            }
            return true;
        }
    };
}
