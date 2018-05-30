package com.example.syedatifhussain.myapplication;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserStories_Wizard extends AppCompatActivity {
    private static final String TAG = "UserStoriesWizard";

    private SectionsPageAdapter mSectionsPageAdapter;

    public static ViewPager mViewPager;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_stories__wizard);

        Log.d(TAG, "onCreate: Starting.");

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.containerUserStories);
        setupViewPager(mViewPager);

    }
    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());


        adapter.addFragment(new UserStories_Wizard_Step_1(), "");
        adapter.addFragment(new UserStories_Wizard_Step_2(), "");
        adapter.addFragment(new UserStories_Wizard_Step_3(), "");
        adapter.addFragment(new UserStories_Wizard_Step_4(), "");
        adapter.addFragment(new UserStories_Wizard_Step_5(), "");

        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //loadEveryTime();
    }
    public static void switchFragment(int target){
        mViewPager.setCurrentItem(target);
    }
}
