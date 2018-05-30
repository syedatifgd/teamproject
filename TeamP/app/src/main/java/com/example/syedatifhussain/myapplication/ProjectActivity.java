package com.example.syedatifhussain.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProjectActivity extends AppCompatActivity {

    private TextView uid;
    private TextView upass;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView nv;
    private FirebaseAuth mAuth;
    private DatabaseReference dbs;
    private ImageButton btnSendEmailToEndUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayoutProject);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        nv = (NavigationView)findViewById(R.id.navigationViewProject);
        uid = (TextView)findViewById(R.id.txtUID);
        upass = (TextView) findViewById(R.id.txtPass);
        mAuth = FirebaseAuth.getInstance();
        dbs = FirebaseDatabase.getInstance().getReference();
        btnSendEmailToEndUser = (ImageButton) findViewById(R.id.btnSendEmailToEndUser);
        btnSendEmailToEndUser.setVisibility(View.INVISIBLE);



        if(FirebaseAuth.getInstance().getCurrentUser() !=null){
            mDrawerLayout.addDrawerListener(mToggle);
            mToggle.syncState();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
        getSupportActionBar().setTitle(getIntent().getExtras().get("pname").toString());

        uid.setText(getIntent().getExtras().get("userid").toString());
        upass.setText(getIntent().getExtras().get("userpass").toString());


        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if(menuItem.getItemId() == R.id.menu_home){
                    Intent intent = new Intent(ProjectActivity.this, Main3Activity.class);
                    intent.putExtra("fid","home");
                    startActivity(intent);
                }
                else if(menuItem.getItemId() == R.id.menu_projects){
                    Intent intent2 = new Intent(ProjectActivity.this, Main3Activity.class);
                    intent2.putExtra("fid","projects");
                    startActivity(intent2);

                }
                else if(menuItem.getItemId() == R.id.menu_contacts){
                    Intent intent3 = new Intent(ProjectActivity.this, Main3Activity.class);
                    intent3.putExtra("fid","contacts");
                    startActivity(intent3);
                }

                else if(menuItem.getItemId() == R.id.menu_chats){
                    Intent intent4 = new Intent(ProjectActivity.this, Main3Activity.class);
                    intent4.putExtra("fid","chats");
                    startActivity(intent4);
                }
                else if(menuItem.getItemId() == R.id.menu_settings){
                    Intent intent5 = new Intent(ProjectActivity.this, Main3Activity.class);
                    intent5.putExtra("fid","settings");
                    startActivity(intent5);
                }
                return true;
            }
        });


        btnSendEmailToEndUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProjectActivity.this);
                LayoutInflater inflater = LayoutInflater
                        .from(ProjectActivity.this);
                final View mView = inflater.inflate(R.layout.layout_send_email_client, null);
                mBuilder.setView(mView);
                final AlertDialog ald = mBuilder.create();

                final EditText clientEmail = mView.findViewById(R.id.editTextClientEmailAddress);
                final String emailAdd = clientEmail.getText().toString();

                Button btnSendEmail = mView.findViewById(R.id.btnSendEmail);
                btnSendEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("plain/text");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { clientEmail.getText().toString() });
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Your Teamly Credentials");
                        intent.putExtra(Intent.EXTRA_TEXT, "UserID : "+getIntent().getExtras().get("userid").toString()+" UserPass : "+getIntent().getExtras().get("userpass").toString());
                        startActivity(Intent.createChooser(intent, ""));
                    }
                });


                ald.setCancelable(true);
                ald.setCanceledOnTouchOutside(true);
                ald.show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null){

            dbs.child("Organizations").child("BUKC").child("Projects").child(getIntent().getExtras().get("pname").toString()).child("head").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String head = dataSnapshot.getValue(String.class);
                    if(head.equals(mAuth.getCurrentUser().getUid())){
                        btnSendEmailToEndUser.setVisibility(View.VISIBLE);
                    }
                    else {
                        btnSendEmailToEndUser.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
