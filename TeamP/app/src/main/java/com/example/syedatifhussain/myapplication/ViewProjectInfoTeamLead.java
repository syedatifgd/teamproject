package com.example.syedatifhussain.myapplication;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by SyedAtif on 5/29/2018.
 */

public class ViewProjectInfoTeamLead extends AppCompatActivity {

    private SectionsPageAdapter mSectionsPageAdapter;

    public static ViewPager mViewPager;
    private DatabaseReference db;
    private String orgd;
    private String projectid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_viewprojectinfoteamlead);
        orgd =getIntent().getExtras().getString("orgid");
        projectid = getIntent().getExtras().getString("projectid");

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.containerProjectInfo);
        setupViewPager(mViewPager);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.baseline_check_box_outline_blank_white_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.baseline_check_box_outline_blank_white_24dp);

    }
    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());


        adapter.addFragment(new ViewProjectInfo_Step1(), "");
        adapter.addFragment(new ViewProjectInfo_Step2(), "");

        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

    }
    public static void switchFragment(int target){
        mViewPager.setCurrentItem(target);
    }
}
