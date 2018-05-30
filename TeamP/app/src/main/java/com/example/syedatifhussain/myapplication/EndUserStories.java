package com.example.syedatifhussain.myapplication;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EndUserStories extends AppCompatActivity {
    private String projectid;
    private String orgid;
    private DatabaseReference mdb;
    private Button btnAddStories;
    private RadioGroup radioGroup;
    private int radioButtonID = 0;
    private RadioButton r1;
    private Button btnTestOnboard;
    private String enduserid;
    private String enduserpass;
    private final int[] storeVal = new int[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_user_stories);

        projectid = getIntent().getExtras().get("projectid").toString();
        orgid = getIntent().getExtras().get("orgid").toString();
        enduserid = getIntent().getExtras().get("enduserid").toString();
        enduserpass = getIntent().getExtras().get("enduserpass").toString();

        btnTestOnboard = (Button)findViewById(R.id.btnTestOnboard);

        btnTestOnboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EndUserStories.this, UserStories_Wizard.class);
                intent.putExtra("orgid",orgid);
                intent.putExtra("projectid",projectid);
                intent.putExtra("enduserid",enduserid);
                intent.putExtra("enduserpass",enduserpass);
                startActivity(intent);
            }
        });

//
//        mdb = FirebaseDatabase.getInstance().getReference();
//        btnAddStories.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getContext());
//                LayoutInflater inflater = LayoutInflater.from(EndUserStories.this);
//                View mView= inflater.inflate(R.layout.users_add_stories, null, false);
//                final EditText storyName = mView.findViewById(R.id.editTextStoryName);
//                final EditText storyDescription = mView.findViewById(R.id.editTextStoryDescription);
//                final SeekBar seekBarPriority = mView.findViewById(R.id.seekBarSetStoryPriority);
//                final TextView txtPriority = mView.findViewById(R.id.txtPriority);
//                final List<userStory> storyList = new ArrayList<userStory>();
//                radioGroup = mView.findViewById(R.id.radioGroup1);
//
//
//
//                seekBarPriority.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//                    @Override
//                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                        storeVal[0] = i;
//                        if(i==0){
//                            txtPriority.setText("Priority: Low");
//                        }
//                        else if(i==1){
//                            txtPriority.setText("Priority: Medium");
//                        }
//                        else if(i==2){
//                            txtPriority.setText("Priority: High");
//                        }
//
//                    }
//
//                    @Override
//                    public void onStartTrackingTouch(SeekBar seekBar) {
//
//                    }
//
//                    @Override
//                    public void onStopTrackingTouch(SeekBar seekBar) {
//                        if(storeVal[0] ==0){
//                            txtPriority.setText("Priority: Low");
//                        }
//                        else if(storeVal[0] ==1){
//                            txtPriority.setText("Priority: Medium");
//                        }
//                        else if(storeVal[0] ==2){
//                            txtPriority.setText("Priority: High");
//                        }
//                    }
//                });
//                Button finalAdd= mView.findViewById(R.id.btnFinalAdd);
//                Button saveAdd= mView.findViewById(R.id.btnSaveAdd);
//                ImageButton btnAttachments = mView.findViewById(R.id.btnAddAttachments);
//                btnAttachments.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent();
//                        intent.setAction(Intent.ACTION_GET_CONTENT);
//                        intent.setType("file/*");
//                        startActivity(intent);
//                    }
//                });
//
//                saveAdd.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if(!storyName.getText().toString().isEmpty() && !storyDescription.getText().toString().isEmpty()){
//                            if(storeVal[0] ==0){
//                                radioButtonID = radioGroup.getCheckedRadioButtonId();
//                                r1 = radioGroup.findViewById(radioButtonID);
//                                String selectedText = (String)r1.getText();
//                                userStory us = new userStory(storyName.getText().toString(),storyDescription.getText().toString(),selectedText,"Low");
//                                storyList.add(us);
//                                Toast.makeText(EndUserStories.this,"Story Saved",Toast.LENGTH_SHORT).show();
//                                storyName.setText("");
//                                storyDescription.setText("");
//                            }
//                            else if(storeVal[0] ==1){
//                                radioButtonID = radioGroup.getCheckedRadioButtonId();
//                                r1 = radioGroup.findViewById(radioButtonID);
//                                String selectedText = (String)r1.getText();
//                                userStory us = new userStory(storyName.getText().toString(),storyDescription.getText().toString(),selectedText,"Medium");
//                                storyList.add(us);
//                                Toast.makeText(EndUserStories.this,"Story Saved",Toast.LENGTH_SHORT).show();
//                                storyName.setText("");
//                                storyDescription.setText("");
//                            }
//                            else if(storeVal[0] ==2){
//                                radioButtonID = radioGroup.getCheckedRadioButtonId();
//                                r1 = radioGroup.findViewById(radioButtonID);
//                                String selectedText = (String)r1.getText();
//                                userStory us = new userStory(storyName.getText().toString(),storyDescription.getText().toString(),selectedText,"High");
//                                storyList.add(us);
//                                Toast.makeText(EndUserStories.this,"Story Saved",Toast.LENGTH_SHORT).show();
//                                storyName.setText("");
//                                storyDescription.setText("");
//                            }
//                        }
//                        else{
//                            Toast.makeText(EndUserStories.this,"Make sure story details were entered correctly",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//                finalAdd.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if(!storyName.getText().toString().isEmpty() && !storyDescription.getText().toString().isEmpty()){
//                            if(storeVal[0] ==0){
//                                radioButtonID = radioGroup.getCheckedRadioButtonId();
//                                r1 = radioGroup.findViewById(radioButtonID);
//                                String selectedText = (String)r1.getText();
//                                userStory us = new userStory(storyName.getText().toString(),storyDescription.getText().toString(),selectedText,"Low");
//                                storyList.add(us);
//                                Toast.makeText(EndUserStories.this,"Story Saved",Toast.LENGTH_SHORT).show();
//                                storyName.setText("");
//                                storyDescription.setText("");
//                            }
//                            else if(storeVal[0] ==1){
//                                radioButtonID = radioGroup.getCheckedRadioButtonId();
//                                r1 = radioGroup.findViewById(radioButtonID);
//                                String selectedText = (String)r1.getText();
//                                userStory us = new userStory(storyName.getText().toString(),storyDescription.getText().toString(),selectedText,"Medium");
//                                storyList.add(us);
//                                Toast.makeText(EndUserStories.this,"Story Saved",Toast.LENGTH_SHORT).show();
//                                storyName.setText("");
//                                storyDescription.setText("");
//                            }
//                            else if(storeVal[0] ==2){
//                                radioButtonID = radioGroup.getCheckedRadioButtonId();
//                                r1 = radioGroup.findViewById(radioButtonID);
//                                String selectedText = (String)r1.getText();
//                                userStory us = new userStory(storyName.getText().toString(),storyDescription.getText().toString(),selectedText,"High");
//                                storyList.add(us);
//                                Toast.makeText(EndUserStories.this,"Story Saved",Toast.LENGTH_SHORT).show();
//                                storyName.setText("");
//                                storyDescription.setText("");
//                            }
//                            for(int i=0;i<storyList.size();i++){
//                                mdb.child("Organizations").child(orgid).child("Projects").child(projectid).child("User Stories").child(storyList.get(i).getStoryName()).child("Description").setValue(storyList.get(i).getStoryDescription());
//                                mdb.child("Organizations").child(orgid).child("Projects").child(projectid).child("User Stories").child(storyList.get(i).getStoryName()).child("Story Type").setValue(storyList.get(i).getStoryType());
//                                mdb.child("Organizations").child(orgid).child("Projects").child(projectid).child("User Stories").child(storyList.get(i).getStoryName()).child("Priority").setValue(storyList.get(i).getStoryPriority());
//
//                                Toast.makeText(EndUserStories.this,"Story(s) Created Successfully!",Toast.LENGTH_SHORT).show();
//                                storyName.setText("");
//                                storyDescription.setText("");
//
//                            }
//                        }
//                        else if(!storyList.isEmpty()){
//                            for(int i=0;i<storyList.size();i++){
//                                mdb.child("Organizations").child(orgid).child("Projects").child(projectid).child("User Stories").child(storyList.get(i).getStoryName()).child("Description").setValue(storyList.get(i).getStoryDescription());
//                                mdb.child("Organizations").child(orgid).child("Projects").child(projectid).child("User Stories").child(storyList.get(i).getStoryName()).child("Story Type").setValue(storyList.get(i).getStoryType());
//                                mdb.child("Organizations").child(orgid).child("Projects").child(projectid).child("User Stories").child(storyList.get(i).getStoryName()).child("Priority").setValue(storyList.get(i).getStoryPriority());
//
//                                Toast.makeText(EndUserStories.this,"Story(s) Created Successfully!",Toast.LENGTH_SHORT).show();
//                                storyName.setText("");
//                                storyDescription.setText("");
//
//                            }
//                        }
//                        else {
//                            Toast.makeText(EndUserStories.this,"Make sure story details were entered correctly",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//                mBuilder.setView(mView);
//                AlertDialog ald = mBuilder.create();
//                ald.show();
//
//            }
//        });

    }
    private class userStory{
        String storyName;
        String storyDescription;
        String storyType;
        String storyPriority;

        userStory(){

        }
        userStory(String sn, String sd, String st, String sp){
            this.storyName=sn;
            this.storyDescription=sd;
            this.storyType=st;
            this.storyPriority=sp;
        }
        String getStoryName(){
            return storyName;
        }
        String getStoryDescription(){
            return storyDescription;
        }
        String getStoryType(){
            return storyType;
        }
        String getStoryPriority(){
            return storyPriority;
        }


    }
}
