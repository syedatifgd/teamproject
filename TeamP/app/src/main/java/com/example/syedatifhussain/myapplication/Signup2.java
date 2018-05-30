package com.example.syedatifhussain.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class Signup2 extends AppCompatActivity {

    //fields declaration
    EditText userName;
    EditText orgCode;
    EditText orgID;
    ImageView userImage;
    Button submitData;
    ImageButton editProfileImageButton;
    //final int SELECT_FILE = 6384;
    final int CAMERA_REQUEST = 1888;
    Uri imageBoldUri = null;
    Bitmap thumb_bitmap = null;
    //firebase variables declarations
    DatabaseReference mDatabase;
    StorageReference nStorageRef;
    StorageReference thumbImageRef;
    private ProgressDialog mProgress;
    String downloadUrl;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        userName = (EditText) findViewById(R.id.editTextUserNameSignUp2);
        orgCode = (EditText)findViewById(R.id.editTextOrganizationCodeSignUp2New);
        orgID = (EditText)findViewById(R.id.editTextOrganizationIDSignUp2);
        userImage = (ImageView) findViewById(R.id.uImageContacts);
        editProfileImageButton = (ImageButton) findViewById(R.id.btnEditProfileImage);
        submitData = (Button) findViewById(R.id.btnCompleteSignUp2);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        nStorageRef = FirebaseStorage.getInstance().getReference();
        mProgress = new ProgressDialog(this);
        thumbImageRef = FirebaseStorage.getInstance().getReference().child("Thumb_Images");
        editProfileImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                selectImage();
            }
        });
        submitData.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                if(!userName.getText().toString().isEmpty() && !orgCode.getText().toString().isEmpty() && !orgID.getText().toString().isEmpty()){
                    checkOrg(orgID.getText().toString(),orgCode.getText().toString());
                    mProgress.setMessage("Finishing up..");
                    mProgress.show();
                }
                else{
                    Toast.makeText(Signup2.this,"Invalid Input or Organization ID and Code Mismatch", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mDatabase.child("Image URIs/"+FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()+"/Profile Image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //HashMap<String, Object> hashmap = (HashMap) dataSnapshot.getValue();
                //String val = (String) hashmap.get(String.class);
                String val = dataSnapshot.getValue(String.class);
                if(val !=null){
                    Picasso.with(getApplicationContext()).load(val).resize(300,300).into(userImage);
                }
                else{
                    userImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_contacts_black_36dp));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                userImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_contacts_black_36dp));
            }
        });


    }
    private void checkOrg(final String orgID, final String orgCode){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final Query filterQuery;
        filterQuery = mDatabase.orderByChild(orgID+"/Code").equalTo(orgCode);
        //final Query filterQuery = mDatabase.orderByChild('_searchLastName').startAt(queryText).endAt(queryText+"\uf8ff").once("value");
        filterQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        mDatabase.child("Organizations").child(orgID).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("phone").setValue(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                        //mDatabase.child("Organizations").child(orgID).child("Users").child("O70MoZ7CHhg86usnWtVMAdl9P0P2").child("Phone").setValue("+923452998780");
                        mDatabase.child("Organizations").child(orgID).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").setValue(userName.getText().toString());

                        mDatabase.child("Organizations").child(orgID).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("role").setValue("Developer");
                        String deviceToken = FirebaseInstanceId.getInstance().getToken();
                        mDatabase.child("Organizations").child("BUKC").child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("deviceToken").setValue(deviceToken);

                        mDatabase.child("Organizations/"+orgID+"/Users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/account").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //HashMap<String, Object> hashmap = (HashMap) dataSnapshot.getValue();
                                //String val = (String) hashmap.get(String.class);
                                String val = dataSnapshot.getValue(String.class);
                                if(val !=null){
                                    if(val.equals("true")){
                                        mDatabase.child("Organizations").child(orgID).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("account").setValue("true");
                                    }
                                    else if(val.equals("false")){
                                        mDatabase.child("Organizations").child(orgID).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("account").setValue("false");
                                    }
                                }
                                else{
                                    mDatabase.child("Organizations").child(orgID).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("account").setValue("true");
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                userImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_contacts_black_36dp));
                            }
                        });


                        if(imageBoldUri !=null){
                            StorageReference nChildStorage = nStorageRef.child("User Images").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("Profile Image").child(imageBoldUri.getLastPathSegment());
                            String profilePicUri = imageBoldUri.getLastPathSegment();

                            nChildStorage.putFile(imageBoldUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful()){
                                        downloadUrl = task.getResult().getDownloadUrl().toString();
                                        mDatabase.child("Image URIs").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("Profile Image").setValue(downloadUrl);
                                        mDatabase.child("Organizations").child(orgID).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("image").setValue(downloadUrl);
                                    }
                                }
                            });
                        }
                        else{
                            mDatabase.child("Image URIs/"+FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()+"/Profile Image").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String val = dataSnapshot.getValue(String.class);
                                    if(val !=null){
                                        mDatabase.child("Organizations").child(orgID).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("image").setValue(val);
                                    }
                                    else{
                                        //userImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_contacts_black_36dp));
                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }

                        mDatabase.child("App Data").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("Organization").setValue(orgID);
                        mDatabase.child("App Data").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("User ID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        mDatabase.child("App Data").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("Role").setValue("Developer");
                        mProgress.dismiss();
                        Intent myIntent = new Intent(Signup2.this, Main3Activity.class);
                        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Signup2.this.startActivity(myIntent);
                    }
                }
                else {
                    mProgress.dismiss();
                    Toast.makeText(Signup2.this,"Invalid Organization Code or ID",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Signup2.this);
        builder.setTitle("Add Photo");

        builder.setItems(items, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                if(items[item].equals("Take Photo")){
                    cameraIntent();
                }
                else if(items[item].equals("Choose from gallery")){
                    galleryIntent();
                }
                else if(items[item].equals("Cancel")){
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();

    }

    private void galleryIntent() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,20);
    }

    private void cameraIntent() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == 20 && resultCode == RESULT_OK){
            Uri imageUri = data.getData();

            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(this);
        }
        else if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
            Uri imageUri = data.getData();

            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(this);
        }


        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                imageBoldUri = result.getUri();



                userImage.setImageURI(imageBoldUri);
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }
    }
}
