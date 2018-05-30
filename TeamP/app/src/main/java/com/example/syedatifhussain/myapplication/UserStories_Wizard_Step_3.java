package com.example.syedatifhussain.myapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class UserStories_Wizard_Step_3 extends Fragment {

    private SeekBar seekBarPriority;
    private final int[] storeVal = new int[1];
    private TextView txtPriority;
    private Button btnFinalAdd;
    private Button btnSaveAdd;
    private EditText storyname;
    private EditText storydescription;
    private DatabaseReference mdb;


    public UserStories_Wizard_Step_3() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user_stories__wizard__step_3, container, false);

        seekBarPriority = view.findViewById(R.id.seekBarSetStoryPriorityFragment);
        txtPriority = view.findViewById(R.id.txtPriorityFragment);
        btnFinalAdd = view.findViewById(R.id.btnFinalAddFragment);
        btnSaveAdd = view.findViewById(R.id.btnSaveAddFragment);
        storyname = view.findViewById(R.id.editTextStoryNameFragment);
        storydescription = view.findViewById(R.id.editTextStoryDescriptionFragment);
        mdb = FirebaseDatabase.getInstance().getReference();
        final List<userStory> storyList = new ArrayList<userStory>();

        seekBarPriority.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                storeVal[0] = i;
                if(i==0){
                    txtPriority.setText("Priority: Low");
                }
                else if(i==1){
                    txtPriority.setText("Priority: Medium");
                }
                else if(i==2){
                    txtPriority.setText("Priority: High");
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(storeVal[0] ==0){
                    txtPriority.setText("Priority: Low");
                }
                else if(storeVal[0] ==1){
                    txtPriority.setText("Priority: Medium");
                }
                else if(storeVal[0] ==2){
                    txtPriority.setText("Priority: High");
                }
            }
        });


        btnSaveAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!getActivity().getIntent().getExtras().getString("storyname").isEmpty() && !getActivity().getIntent().getExtras().getString("storydescription").isEmpty()){
                    if(storeVal[0] ==0){
                        userStory us = new userStory(getActivity().getIntent().getExtras().getString("storyname"),getActivity().getIntent().getExtras().getString("storydescription"),getActivity().getIntent().getExtras().getString("checked"),"Low");
                        storyList.add(us);
                        Toast.makeText(getActivity(),"Story Saved",Toast.LENGTH_SHORT).show();
                        UserStories_Wizard.switchFragment(0);
                    }
                    else if(storeVal[0] ==1){
                        userStory us = new userStory(getActivity().getIntent().getExtras().getString("storyname"),getActivity().getIntent().getExtras().getString("storydescription"),getActivity().getIntent().getExtras().getString("checked"),"Medium");
                        storyList.add(us);
                        Toast.makeText(getActivity(),"Story Saved",Toast.LENGTH_SHORT).show();
                        UserStories_Wizard.switchFragment(0);
                    }
                    else if(storeVal[0] ==2){
                        userStory us = new userStory(getActivity().getIntent().getExtras().getString("storyname"),getActivity().getIntent().getExtras().getString("storydescription"),getActivity().getIntent().getExtras().getString("checked"),"High");
                        storyList.add(us);
                        Toast.makeText(getActivity(),"Story Saved",Toast.LENGTH_SHORT).show();
                        UserStories_Wizard.switchFragment(0);
                    }
                    for(int i=0;i<storyList.size();i++){
                        mdb.child("Organizations").child(getActivity().getIntent().getExtras().getString("orgid")).child("Projects").child(getActivity().getIntent().getExtras().getString("projectid")).child("User Stories").child(storyList.get(i).getStoryName()).child("description").setValue(storyList.get(i).getStoryDescription());
                        mdb.child("Organizations").child(getActivity().getIntent().getExtras().getString("orgid")).child("Projects").child(getActivity().getIntent().getExtras().getString("projectid")).child("User Stories").child(storyList.get(i).getStoryName()).child("type").setValue(storyList.get(i).getStoryType());
                        mdb.child("Organizations").child(getActivity().getIntent().getExtras().getString("orgid")).child("Projects").child(getActivity().getIntent().getExtras().getString("projectid")).child("User Stories").child(storyList.get(i).getStoryName()).child("priority").setValue(storyList.get(i).getStoryPriority());
                    }
                }
                else{
                    Toast.makeText(getActivity(),"Make sure story details were entered correctly",Toast.LENGTH_SHORT).show();
                }

            }
        });


        btnFinalAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!getActivity().getIntent().getExtras().getString("storyname").isEmpty() && !getActivity().getIntent().getExtras().getString("storydescription").isEmpty()){
                    if(storeVal[0] ==0){
                        userStory us = new userStory(getActivity().getIntent().getExtras().getString("storyname"),getActivity().getIntent().getExtras().getString("storydescription"),getActivity().getIntent().getExtras().getString("checked"),"Low");
                        storyList.add(us);
                        Toast.makeText(getActivity(),"Story Saved",Toast.LENGTH_SHORT).show();
                    }
                    else if(storeVal[0] ==1){
                        userStory us = new userStory(getActivity().getIntent().getExtras().getString("storyname"),getActivity().getIntent().getExtras().getString("storydescription"),getActivity().getIntent().getExtras().getString("checked"),"Medium");
                        storyList.add(us);
                        Toast.makeText(getActivity(),"Story Saved",Toast.LENGTH_SHORT).show();
                    }
                    else if(storeVal[0] ==2){
                        userStory us = new userStory(getActivity().getIntent().getExtras().getString("storyname"),getActivity().getIntent().getExtras().getString("storydescription"),getActivity().getIntent().getExtras().getString("checked"),"High");
                        storyList.add(us);
                        Toast.makeText(getActivity(),"Story Saved",Toast.LENGTH_SHORT).show();
                    }
                    for(int i=0;i<storyList.size();i++){
                        mdb.child("Organizations").child(getActivity().getIntent().getExtras().getString("orgid")).child("Projects").child(getActivity().getIntent().getExtras().getString("projectid")).child("User Stories").child(storyList.get(i).getStoryName()).child("description").setValue(storyList.get(i).getStoryDescription());
                        mdb.child("Organizations").child(getActivity().getIntent().getExtras().getString("orgid")).child("Projects").child(getActivity().getIntent().getExtras().getString("projectid")).child("User Stories").child(storyList.get(i).getStoryName()).child("type").setValue(storyList.get(i).getStoryType());
                        mdb.child("Organizations").child(getActivity().getIntent().getExtras().getString("orgid")).child("Projects").child(getActivity().getIntent().getExtras().getString("projectid")).child("User Stories").child(storyList.get(i).getStoryName()).child("priority").setValue(storyList.get(i).getStoryPriority());
                    }
                    Toast.makeText(getActivity(),"Story(s) Created Successfully!",Toast.LENGTH_SHORT).show();
                    UserStories_Wizard.switchFragment(3);
                }
                else if(!storyList.isEmpty()){
                    for(int i=0;i<storyList.size();i++){
                        mdb.child("Organizations").child(getActivity().getIntent().getExtras().getString("orgid")).child("Projects").child(getActivity().getIntent().getExtras().getString("projectid")).child("User Stories").child(storyList.get(i).getStoryName()).child("description").setValue(storyList.get(i).getStoryDescription());
                        mdb.child("Organizations").child(getActivity().getIntent().getExtras().getString("orgid")).child("Projects").child(getActivity().getIntent().getExtras().getString("projectid")).child("User Stories").child(storyList.get(i).getStoryName()).child("type").setValue(storyList.get(i).getStoryType());
                        mdb.child("Organizations").child(getActivity().getIntent().getExtras().getString("orgid")).child("Projects").child(getActivity().getIntent().getExtras().getString("projectid")).child("User Stories").child(storyList.get(i).getStoryName()).child("priority").setValue(storyList.get(i).getStoryPriority());
                        Toast.makeText(getActivity(),"Story(s) Created Successfully!",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getActivity(),"Make sure story details were entered correctly",Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
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
