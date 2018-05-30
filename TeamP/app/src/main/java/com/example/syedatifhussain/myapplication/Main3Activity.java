package com.example.syedatifhussain.myapplication;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.lang.reflect.Field;

public class Main3Activity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SectionsPageAdapter mSectionsPageAdapter;

    private static ViewPager mViewPager;
    private TextView t1;
    private TextView t2;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        Log.d(TAG, "onCreate: Starting.");

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.baseline_cast_white_36dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.baseline_view_list_white_36dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.baseline_person_white_36dp);
        tabLayout.getTabAt(3).setIcon(R.drawable.baseline_chat_white_36dp);
        tabLayout.getTabAt(4).setIcon(R.drawable.baseline_settings_white_36dp);


        t1 = (TextView)findViewById(R.id.txtUserDetails);
        t2 = (TextView) findViewById(R.id.txtUserDetails2);

        try{
            String fid = getIntent().getExtras().get("fid").toString();
            if(fid!=null){
                if(fid.equals("home")){
                    switchFragment(0);
                }
                else if(fid.equals("projects")){
                    switchFragment(1);
                }
                else if(fid.equals("contacts")){
                    switchFragment(2);
                }
                else if(fid.equals("chats")){
                    switchFragment(3);
                }
                else if(fid.equals("settings")){
                    switchFragment(4);
                }
            }

        }catch (Exception ex){

        }



    }

    public static void switchFragment(int target){
        mViewPager.setCurrentItem(target);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());


        adapter.addFragment(new HomeFragment(), "");
        adapter.addFragment(new ProjectsFragment(), "");
        adapter.addFragment(new ContactsFragment(), "");
        adapter.addFragment(new Chats(),"");
        adapter.addFragment(new SettingsFragment(), "");

        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        loadEveryTime();
    }

    private void loadEveryTime() {

        db = FirebaseDatabase.getInstance().getReference();


        db.child("Organizations").child("BUKC").child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                t1.setText(name+" BUKC");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        db.child("Organizations").child("BUKC").child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/role").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Button b1 = (Button)findViewById(R.id.btnCreateProject);
                String role = dataSnapshot.getValue(String.class);
                t2.setText(role);
                if(role.equals("Team Lead")){
                    if(b1 !=null){
                        b1.setVisibility(View.VISIBLE);
                    }

                }
                else {
                    if(b1 !=null){
                        b1.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
