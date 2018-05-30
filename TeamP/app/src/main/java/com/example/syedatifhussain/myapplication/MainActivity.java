package com.example.syedatifhussain.myapplication;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    private final int REQUEST_LOGIN = 1000;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null) {
            //if user is already logged in
            if (!FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().isEmpty()) {
                startActivity(new Intent(this, Main3Activity.class)
                        .putExtra("phone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().isEmpty())
                );
            }
        }
        else{
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build())).build(),REQUEST_LOGIN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_LOGIN){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            //mDatabase.child("Users").child("Phone").push().setValue();
            //Sucessfully signed in
            if(resultCode == RESULT_OK){
                Intent mainIntent = new Intent(this,Signup2.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                if(!FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().isEmpty()){
                    startActivity(new Intent(this,Signup2.class));
                    finish();
                    return;
                }
            }
            else{
                if(response == null){
                    Toast.makeText(this, "Cancelled",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, "Unknown Error", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Toast.makeText(this,"Unknown SignIn Error",Toast.LENGTH_SHORT).show();
        }
    }
}
