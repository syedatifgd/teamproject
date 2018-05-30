package com.example.syedatifhussain.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
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
import com.rilixtech.CountryCodePicker;

public class SignIn extends AppCompatActivity {

    CountryCodePicker ccp;
    Button btnReadNumberFromSim;
    Button submitSignIn;
    EditText editTextPhoneNumberSignInStep1;
    private DatabaseReference dbs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ccp = (CountryCodePicker) findViewById(R.id.countryCodePicker);
        btnReadNumberFromSim = (Button) findViewById(R.id.btnReadNumberFromSim);
        editTextPhoneNumberSignInStep1 = (EditText) findViewById(R.id.editTextPhoneNumberSignInStep1);
        submitSignIn = (Button) findViewById(R.id.btnSubmitPhoneSignIn);
        dbs = FirebaseDatabase.getInstance().getReference();


        btnReadNumberFromSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    final String mPhoneNumber = tMgr.getLine1Number();
                    editTextPhoneNumberSignInStep1.setText(mPhoneNumber);

                }catch (Exception e){
                    Toast.makeText(SignIn.this,"Could not retrieve phone number from sim, please enter manually.",Toast.LENGTH_SHORT).show();
                }



            }
        });

        submitSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editTextPhoneNumberSignInStep1.getText().toString().isEmpty()){
                    String ccode = ccp.getFullNumberWithPlus();
                    final String fullNumber = ccode+editTextPhoneNumberSignInStep1.getText().toString();

                    final Query filterQuery;
                    filterQuery = dbs.child("App Data");

                    filterQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(editTextPhoneNumberSignInStep1.getText().toString()).exists()) {
                                Intent intent = new Intent(SignIn.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            }
                            else if(dataSnapshot.child(fullNumber).exists()) {
                                Intent intent = new Intent(SignIn.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(SignIn.this,"Phone validation failed. Please check your input and try again.",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    Toast.makeText(SignIn.this,"Phone validation failed. Please check your input and try again.",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
