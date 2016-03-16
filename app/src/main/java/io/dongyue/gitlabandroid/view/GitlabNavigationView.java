package io.dongyue.gitlabandroid.view;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.NavigationView;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.dongyue.gitlabandroid.R;
import io.dongyue.gitlabandroid.model.api.UserFull;
import io.dongyue.gitlabandroid.network.GitlabClient;
import io.dongyue.gitlabandroid.network.GitlabSubscriber;
import io.dongyue.gitlabandroid.utils.NavigationManager;
import io.dongyue.gitlabandroid.utils.eventbus.RxBus;
import io.dongyue.gitlabandroid.utils.eventbus.events.CloseDrawerEvent;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Brotherjing on 2016/3/6.
 */
public class GitlabNavigationView extends NavigationView {

    @Bind(R.id.imageView)
    ImageView imageView;
    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.email)
    TextView email;

    public GitlabNavigationView(Context context) {
        super(context);
        init(context);
    }

    public GitlabNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GitlabNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(Context context){
        inflateMenu(R.menu.activity_home_drawer);
        View header = inflateHeaderView(R.layout.nav_header_home);
        ButterKnife.bind(this, header);
        Observable<UserFull> observable = GitlabClient.getInstance().getThisUser();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new GitlabSubscriber<UserFull>() {
                    @Override
                    public void onNext(UserFull userFull) {
                        name.setText(userFull.getName());
                        email.setText(userFull.getEmail());
                        GitlabClient.getPicasso().with(context)
                                .load(userFull.getAvatarUrl())
                                .resize(100, 100)  //图片大小还未确定
                                .centerCrop()
                                .into(imageView);
                    }
                });
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
                    NavigationManager.toMyActivities(getContext());break;
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
