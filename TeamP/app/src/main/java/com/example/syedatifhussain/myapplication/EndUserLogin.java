package com.example.syedatifhussain.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class EndUserLogin extends AppCompatActivity {

    private EditText uidcredentials;
    private EditText upasscredentials;
    private EditText orgidcredentials;
    DatabaseReference mDatabase;
    private boolean t1 = false;
    private boolean t2 = false;
    private Button proceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_user_login);



        uidcredentials = (EditText)findViewById(R.id.editTextUserNameCredentials);
        upasscredentials = (EditText)findViewById(R.id.editTextUserPasswordCredentials);
        orgidcredentials = (EditText) findViewById(R.id.editTextOrgIDCredentials);
        proceed = (Button) findViewById(R.id.btnProceedCredentials);


        mDatabase = FirebaseDatabase.getInstance().getReference();


        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUserCredentials(orgidcredentials.getText().toString(),uidcredentials.getText().toString(),upasscredentials.getText().toString());
            }
        });


    }
    public void checkUserCredentials(final String og, final String uu, final String pp){
        final Query filterQuery;
        filterQuery = mDatabase.child("Organizations/"+og+"/Projects").orderByChild("uid").equalTo(uu);

        final Query filterQuery2;
        filterQuery2 = mDatabase.child("Organizations/"+og+"/Projects").orderByChild("upass").equalTo(pp);

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
                                        Intent intent = new Intent(EndUserLogin.this, EndUserDashboard.class);
                                        intent.putExtra("projectid",clubkey);
                                        intent.putExtra("orgid",og);
                                        intent.putExtra("enduserid",uu);
                                        intent.putExtra("enduserpass",pp);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(EndUserLogin.this);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("uid", uu);
                                        editor.putString("upass", pp);
                                        editor.putString("orgcredentials", og);
                                        editor.commit();

                                        mDatabase.child("Organizations").child(og).child("Users").child(uu).child("image").setValue("https://firebasestorage.googleapis.com/v0/b/myapplication2-c7258.appspot.com/o/User%20Images%2FEndUser%2Fbaseline_account_circle_black_36dp.png?alt=media&token=48bb31c1-6515-45d0-be29-32a303df2e38");
                                        mDatabase.child("Organizations").child(og).child("Users").child(uu).child("name").setValue("Client");
                                        mDatabase.child("Organizations").child(og).child("Users").child(uu).child("project").setValue(clubkey);
                                        mDatabase.child("Organizations").child(og).child("Users").child(uu).child("role").setValue("End User");

                                        startActivity(intent);
                                    }
                                }




                            }
                            else {
                                Toast.makeText(EndUserLogin.this,"User ID,Password or Organization ID Invalid!",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    Toast.makeText(EndUserLogin.this,"User ID,Password or Organization ID Invalid!",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
