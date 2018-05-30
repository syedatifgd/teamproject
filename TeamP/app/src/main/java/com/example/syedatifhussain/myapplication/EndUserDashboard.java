package com.example.syedatifhussain.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EndUserDashboard extends AppCompatActivity {

    private String projectid;
    private String orgid;
    private String enduserid;
    private String enduserpass;
    private DatabaseReference mdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_user_dashboard);

        projectid = getIntent().getExtras().get("projectid").toString();
        orgid = getIntent().getExtras().get("orgid").toString();
        enduserid = getIntent().getExtras().get("enduserid").toString();
        enduserpass = getIntent().getExtras().get("enduserpass").toString();

        mdb = FirebaseDatabase.getInstance().getReference();

        mdb.child("Organizations/"+orgid+"/Projects").child(projectid+"/status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String val  = dataSnapshot.getValue(String.class);
                if(val.equals("false")){
                    Intent userstoriesintent = new Intent(EndUserDashboard.this,EndUserStories.class);
                    userstoriesintent.putExtra("projectid",projectid);
                    userstoriesintent.putExtra("orgid",orgid);
                    userstoriesintent.putExtra("enduserid",enduserid);
                    userstoriesintent.putExtra("enduserpass",enduserpass);
                    startActivity(userstoriesintent);
                }
                else{

                    mdb.child("Organizations/"+getIntent().getExtras().getString("orgid")+"/Projects").child(getIntent().getExtras().getString("projectid")+"/Meeting/Status").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String meetStatus = dataSnapshot.getValue(String.class);
                            if(meetStatus.equals("true")){
                                Intent userstoriesintent = new Intent(EndUserDashboard.this,MeetingPanel.class);
                                userstoriesintent.putExtra("projectid",projectid);
                                userstoriesintent.putExtra("orgid",orgid);
                                userstoriesintent.putExtra("enduserid",enduserid);
                                userstoriesintent.putExtra("enduserpass",enduserpass);
                                startActivity(userstoriesintent);
                            }
                            else {
                                Intent userstoriesintent = new Intent(EndUserDashboard.this,ProjectMeetingCheck.class);
                                userstoriesintent.putExtra("projectid",projectid);
                                userstoriesintent.putExtra("orgid",orgid);
                                userstoriesintent.putExtra("enduserid",enduserid);
                                userstoriesintent.putExtra("enduserpass",enduserpass);
                                startActivity(userstoriesintent);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
