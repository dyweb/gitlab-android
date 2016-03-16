package io.dongyue.gitlabandroid.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.dongyue.gitlabandroid.R;
import io.dongyue.gitlabandroid.model.api.UserFull;
import io.dongyue.gitlabandroid.network.GitlabClient;
import io.dongyue.gitlabandroid.network.GitlabSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserInfoActivity extends AppCompatActivity {

    @Bind(R.id.text)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        loadData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadData(){
        Observable<UserFull> observable = GitlabClient.getInstance().getThisUser();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new GitlabSubscriber<UserFull>() {
                    @Override
                    public void onNext(UserFull userFull) {
                        StringBuilder st = new StringBuilder();
                        st.append("User name: ").append(userFull.getUsername())
                                .append("\nEmail: ").append(userFull.getEmail())
                                .append("\nId: ").append(userFull.getId())
                                .append("\nCreatedAt: ").append(userFull.getCreatedAt())
                                .append("\nProjectsLimit: ").append(userFull.getProjectsLimit())
                                .append("\nCurrentSignInAt: ").append(userFull.getCurrentSignInAt())
                                .append("\nName: ").append(userFull.getName())
                                .append("\nState: ").append(userFull.getState())
                                .append("\nColorSchemeId: ").append(userFull.getColorSchemeId())
                                .append("\nTwitter: ").append(userFull.getTwitter())
                                .append("\nSkype: ").append(userFull.getSkype())
                                .append("\nLinkedin: ").append(userFull.getLinkedin())
                                .append("\nThemeId: ").append(userFull.getThemeId())
                                .append("\nIdentities: ").append(userFull.getIdentities());
                        textView.setText(st);
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onOptionsItemSelected(item)) {
                //NavUtils.navigateUpFromSameTask(this);
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
