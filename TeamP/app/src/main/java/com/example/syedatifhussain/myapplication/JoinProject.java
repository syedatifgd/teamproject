package com.example.syedatifhussain.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JoinProject extends AppCompatActivity {
    private String orgz;
    private EditText projectKey;
    private EditText projectidjoin;
    private Button btnSubmit;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_project);
        db = FirebaseDatabase.getInstance().getReference();
        orgz = getIntent().getExtras().get("orgz").toString();
        projectKey = (EditText)findViewById(R.id.editTextProjectKey);
        projectidjoin = (EditText) findViewById(R.id.editTextProjectIDJoin);
        btnSubmit = (Button) findViewById(R.id.btnSubmitJoin);
        projectidjoin.requestFocus();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String pkey = projectKey.getText().toString();
                final String pid = projectidjoin.getText().toString();
                if(pkey !=null && pid !=null){
                    db.child("Organizations").child(orgz).child("Projects").child(pid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String test = dataSnapshot.child("code").getValue(String.class);
                            if(test!=null && pkey!=null){
                                if(test.equals(projectKey.getText().toString())){
                                    if(dataSnapshot.child("Members").hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        db.child("Organizations").child(orgz).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("User Projects").child(pid).child("code").setValue(pkey);
                                        db.child("Organizations").child(orgz).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("User Projects").child(pid).child("description").setValue(dataSnapshot.child("description").getValue(String.class));



                                        db.child("Organizations/"+orgz+"/Projects").child(pid+"/Meeting/Status").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                final String val  = dataSnapshot.getValue(String.class);
                                                if(val == null){
                                                    db.child("Organizations/"+orgz+"/Projects").child(pid+"/uid").addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            final String val1 = dataSnapshot.getValue(String.class);
                                                            db.child("Organizations/"+orgz+"/Projects").child(pid+"/upass").addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    String val2 = dataSnapshot.getValue(String.class);
                                                                    Intent intent = new Intent(JoinProject.this, ProjectActivity.class);
                                                                    intent.putExtra("userid",val1);
                                                                    intent.putExtra("userpass",val2);
                                                                    intent.putExtra("pname",pid);
                                                                    startActivity(intent);
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {

                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                                }

                                                else if (val.equals("false")) {
                                                    Intent intent = new Intent(JoinProject.this, ProjectMeetingCheck.class);
                                                    intent.putExtra("orgid",orgz);
                                                    intent.putExtra("projectid",pid);
                                                    intent.putExtra("userid",dataSnapshot.child("uid").getValue(String.class));
                                                    intent.putExtra("userpass",dataSnapshot.child("upass").getValue(String.class));
                                                    startActivity(intent);

                                                }
                                                else {
                                                    Intent intent = new Intent(JoinProject.this, MeetingPanel.class);
                                                    intent.putExtra("orgid",orgz);
                                                    intent.putExtra("projectid",pid);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }
                            else {
                                Toast.makeText(JoinProject.this,"Could not join project, either you are not a part of it or input was incorrect",Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    Toast.makeText(JoinProject.this,"Empty Project ID or Key",Toast.LENGTH_SHORT).show();
                }
            }
        });





    }
}
