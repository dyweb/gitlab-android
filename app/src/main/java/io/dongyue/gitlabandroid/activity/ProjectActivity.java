package io.dongyue.gitlabandroid.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.dongyue.gitlabandroid.R;
import io.dongyue.gitlabandroid.activity.base.BaseActivity;
import io.dongyue.gitlabandroid.fragment.FilesFragment;
import io.dongyue.gitlabandroid.fragment.OverviewFragment;
import io.dongyue.gitlabandroid.fragment.RepositoryCommitsFragment;
import io.dongyue.gitlabandroid.model.api.Branch;
import io.dongyue.gitlabandroid.model.api.Project;
import io.dongyue.gitlabandroid.network.GitlabClient;
import io.dongyue.gitlabandroid.network.GitlabSubscriber;
import io.dongyue.gitlabandroid.utils.NavigationManager;
import io.dongyue.gitlabandroid.utils.ToastUtils;
import io.dongyue.gitlabandroid.utils.eventbus.RxBus;
import io.dongyue.gitlabandroid.utils.eventbus.events.APIErrorEvent;
import io.dongyue.gitlabandroid.utils.eventbus.events.SwitchBranchEvent;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ProjectActivity extends BaseActivity {

    private static final String EXTRA_PROJECT = "project";

    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Bind(R.id.container) ViewPager mViewPager;
    @Bind(R.id.branch_spinner) AppCompatSpinner spinner;

    private Project project;
    private String mBranchName;

    public static Intent viewProject(Context context,Project project){
        Intent intent = new Intent(context,ProjectActivity.class);
        intent.putExtra(EXTRA_PROJECT, Parcels.wrap(project));
        return intent;
    }

    public Project getProject() {
        return project;
    }

    public String getBranchName() {
        return mBranchName;
    }

    private void broadcastLoad(String branchName){
        RxBus.getBus().post(new SwitchBranchEvent(branchName));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        project = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_PROJECT));
        mBranchName = project.getDefaultBranch();

        addSubscription(RxBus.getBus().observeEvents(APIErrorEvent.class)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(apiErrorEvent -> {
                    if (apiErrorEvent.getCode() == 401) {
                        NavigationManager.toLogin(ProjectActivity.this);
                        finish();
                    }
                    //other cases
                }));
        addSubscription(GitlabClient.getInstance().getBranches(project.getId())
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(branches -> {
                if (branches!=null&&!branches.isEmpty()) {
                    spinner.setVisibility(View.VISIBLE);
                    spinner.setAdapter(new ArrayAdapter<>(ProjectActivity.this, R.layout.simple_list_item_1_dark, android.R.id.text1, branches));
                    for (int i = 0; i < branches.size(); ++i) {
                        if (branches.get(i).getName().equals(project.getDefaultBranch())) {
                            spinner.setSelection(i);
                        }
                    }
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            mBranchName = ((TextView) view).getText().toString();
                            broadcastLoad(mBranchName);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }else{
                    spinner.setVisibility(View.GONE);
                }
            }));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_project, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            if (!super.onOptionsItemSelected(item)) {
                //NavUtils.navigateUpFromSameTask(this);
                finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return OverviewFragment.newInstance();
                case 2:
                    return FilesFragment.newInstance();
                case 3:
                    //Project Commit Fragment
                    return RepositoryCommitsFragment.newInstance(project.getId());
                default:
                    return PlaceholderFragment.newInstance(position + 1);
            }
        }

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "OVERVIEW";
                case 1:
                    return "ACTIVITY";
                case 2:
                    return "FILES";
                case 3:
                    return "COMMIT";
                case 4:
                    return "ISSUE";
                case 5:
                    return "MEMBERS";
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_project, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }
}
