package com.example.syedatifhussain.myapplication;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by SyedAtif on 5/29/2018.
 */

public class ViewProjectInfo_Step1 extends android.support.v4.app.Fragment {

    private String orgd;
    private String projectid;
    private DatabaseReference mDatabase;

    public ViewProjectInfo_Step1() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_project_info_team_lead_step_1, container, false);

        orgd =getActivity().getIntent().getExtras().getString("orgid");
        projectid = getActivity().getIntent().getExtras().getString("projectid");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        TextView txtProjectName = view.findViewById(R.id.txtProjectInfoProjectName);
        final TextView txtProjectDate = view.findViewById(R.id.txtProjectInfoStartDate);
        final TextView txtProjectCurrentMeeting = view.findViewById(R.id.txtProjectInfoCurrentMeeting);
        final TextView txtProjectSprint = view.findViewById(R.id.txtProjectInfoCurrentSprint);
        final TextView txtProjectDesc = view.findViewById(R.id.txtProjectInfoProjectDescription);
        txtProjectName.setText(projectid);

        mDatabase.child("Organizations").child(orgd).child("Projects").child(projectid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                txtProjectDate.setText(dataSnapshot.child("Meeting").child("Date").getValue(String.class));
                txtProjectCurrentMeeting.setText(dataSnapshot.child("Meeting").child("type").getValue(String.class));
                txtProjectSprint.setText(dataSnapshot.child("Meeting").child("sprint").getValue(String.class));
                txtProjectDesc.setText(dataSnapshot.child("description").getValue(String.class));


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }
}
