package com.example.syedatifhussain.myapplication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;

public class Selection extends AppCompatActivity {

    private Button dev;
    private Button user;
    private final int REQUEST_LOGIN = 1000;
    private DatabaseReference mDatabase;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;


    FrameLayout progressBarHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null) {
            //if user is already logged in
            if (!FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().isEmpty()) {
                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                mDatabase.child("Organizations").child("BUKC").child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("deviceToken").setValue(deviceToken);

                startActivity(new Intent(this, Main3Activity.class)
                        .putExtra("phone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().isEmpty())
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                );
                outAnimation = new AlphaAnimation(1f, 0f);
                outAnimation.setDuration(200);
                progressBarHolder.setAnimation(outAnimation);
                progressBarHolder.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


            }
        }
        //checking if end user is already signed in
        else{
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            final String uid = preferences.getString("uid", "");
            final String upass = preferences.getString("upass", "");
            final String orgcredentials = preferences.getString("orgcredentials", "");

            if(!uid.isEmpty() && !upass.isEmpty() && !orgcredentials.isEmpty()){
                final Query filterQuery;
                filterQuery = mDatabase.child("Organizations/"+orgcredentials+"/Projects").orderByChild("uid").equalTo(uid);

                final Query filterQuery2;
                filterQuery2 = mDatabase.child("Organizations/"+orgcredentials+"/Projects").orderByChild("upass").equalTo(upass);

                filterQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            filterQuery2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){

                                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                            String clubkey = childSnapshot.getKey();
                                            if(clubkey!=null){

                                                outAnimation = new AlphaAnimation(1f, 0f);
                                                outAnimation.setDuration(200);
                                                progressBarHolder.setAnimation(outAnimation);
                                                progressBarHolder.setVisibility(View.GONE);
                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                Intent intent = new Intent(Selection.this, EndUserDashboard.class);
                                                intent.putExtra("projectid",clubkey);
                                                intent.putExtra("orgid",orgcredentials);
                                                intent.putExtra("enduserid",uid);
                                                intent.putExtra("enduserpass",upass);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            }
                                        }
                                    }
                                    else {
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        outAnimation = new AlphaAnimation(1f, 0f);
                                        outAnimation.setDuration(200);
                                        progressBarHolder.setAnimation(outAnimation);
                                        progressBarHolder.setVisibility(View.GONE);
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(Selection.this);
                                        builder1.setMessage("Your credentials have changed, " +
                                                "do you want to contact the Organization?");
                                        builder1.setCancelable(true);

                                        builder1.setPositiveButton(
                                                "Yes",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        mDatabase.child("Organizations").child(orgcredentials+"/Contact").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                String val  = dataSnapshot.getValue(String.class);
                                                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                                                intent.setData(Uri.parse("tel:"+val));
                                                                startActivity(intent);
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {
                                                                Toast.makeText(Selection.this,"Network error.",Toast.LENGTH_SHORT).show();

                                                            }
                                                        });
                                                    }
                                                });

                                        builder1.setNegativeButton(
                                                "No",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                });

                                        AlertDialog alert11 = builder1.create();
                                        alert11.show();

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(Selection.this,"Network error.",Toast.LENGTH_SHORT).show();
                                    outAnimation = new AlphaAnimation(1f, 0f);
                                    outAnimation.setDuration(200);
                                    progressBarHolder.setAnimation(outAnimation);
                                    progressBarHolder.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                }
                            });
                        }
                        else {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            outAnimation = new AlphaAnimation(1f, 0f);
                            outAnimation.setDuration(200);
                            progressBarHolder.setAnimation(outAnimation);
                            progressBarHolder.setVisibility(View.GONE);
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(Selection.this);
                            builder1.setMessage("Your credentials have changed, " +
                                                "do you want to contact the Organization?");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            mDatabase.child("Organizations").child(orgcredentials+"/Contact").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    String val  = dataSnapshot.getValue(String.class);
                                                    Intent intent = new Intent(Intent.ACTION_DIAL);
                                                    intent.setData(Uri.parse("tel:"+val));
                                                    startActivity(intent);
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    });

                            builder1.setNegativeButton(
                                    "No",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(Selection.this,"Network error.",Toast.LENGTH_SHORT).show();

                    }
                });
            }
            else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                outAnimation = new AlphaAnimation(1f, 0f);
                outAnimation.setDuration(200);
                progressBarHolder.setAnimation(outAnimation);
                progressBarHolder.setVisibility(View.GONE);
            }
        }
        dev = (Button)findViewById(R.id.btnDeveloper);
        user = (Button)findViewById(R.id.btnUser);

        dev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Selection.this,SignIn.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                finish();
            }
        });

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Selection.this, EndUserLogin.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
        });
    }
}
