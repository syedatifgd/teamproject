package com.example.syedatifhussain.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String ph;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String visit_user_id = getIntent().getExtras().get("visit_user_id").toString();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        String orgglobalid = getIntent().getExtras().get("orgglobalid").toString();

        mDatabase.child("Organizations").child(orgglobalid).child("Users").child(visit_user_id+"/name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String val;
                val = dataSnapshot.getValue(String.class);
                TextView name = (TextView)findViewById(R.id.profile_visit_user_name);
                name.setText(val);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("Organizations").child(orgglobalid).child("Users").child(visit_user_id+"/role").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String val;
                val = dataSnapshot.getValue(String.class);
                TextView role = (TextView)findViewById(R.id.profile_visit_user_role);
                role.setText(val);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("Organizations").child(orgglobalid).child("Users").child(visit_user_id+"/phone").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ph = dataSnapshot.getValue(String.class);
                mDatabase.child("Image URIs").child(ph+"/Profile Image").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String val = dataSnapshot.getValue(String.class);
                        CircleImageView  v1 = (CircleImageView )findViewById(R.id.profile_visit_user_image);
                        Picasso.with(ProfileActivity.this).load(val).resize(400,400).into(v1);
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
}
