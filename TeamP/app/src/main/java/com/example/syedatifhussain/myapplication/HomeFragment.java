package com.example.syedatifhussain.myapplication;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SyedAtif on 11/14/2017.
 */

public class HomeFragment extends Fragment {
    private DatabaseReference mContactsDatabase;
    public Button cProject;
    public Button jProject;
    private List<String> listRecIDs = new ArrayList<String>();

    private DatabaseReference db;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;


    FrameLayout progressBarHolder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view  = inflater.inflate(R.layout.home_fragment,container,false);
        cProject = view.findViewById(R.id.btnCreateProject);
        progressBarHolder = view.findViewById(R.id.progressBarHolder);
        jProject = view.findViewById(R.id.btnJoinProject);

        cProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateProject.class);
                startActivity(intent);
            }
        });
        jProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = FirebaseDatabase.getInstance().getReference();
                db.child("App Data/"+FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()+"/Organization").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String orgid = dataSnapshot.getValue(String.class);
                        Intent intent = new Intent(getActivity(), JoinProject.class);
                        intent.putExtra("orgz",orgid);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        db = FirebaseDatabase.getInstance().getReference();
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);
        cProject.setVisibility(View.GONE);
        jProject.setVisibility(View.GONE);

//        db.child("Organizations/BUKC/Projects").child("whatsapp/Members").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot issue : dataSnapshot.getChildren()) {
//                    String test = issue.getKey();
//                    listRecIDs.add(test);
//                    Toast.makeText(getActivity(),test,Toast.LENGTH_SHORT).show();
//
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        for (int i =0;i<listRecIDs.size();i++){
//            Toast.makeText(getActivity(),listRecIDs.get(i),Toast.LENGTH_SHORT).show();
//        }

        db.child("Organizations").child("BUKC").child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/role").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Button b1 = view.findViewById(R.id.btnCreateProject);
                String role = dataSnapshot.getValue(String.class);
                if(role.equals("Team Lead")){
                    b1.setVisibility(View.VISIBLE);
                    outAnimation = new AlphaAnimation(1f, 0f);
                    outAnimation.setDuration(200);
                    progressBarHolder.setAnimation(outAnimation);
                    progressBarHolder.setVisibility(View.GONE);
                    jProject.setVisibility(View.VISIBLE);
                }
                else {
                    b1.setVisibility(View.GONE);
                    outAnimation = new AlphaAnimation(1f, 0f);
                    outAnimation.setDuration(200);
                    progressBarHolder.setAnimation(outAnimation);
                    progressBarHolder.setVisibility(View.GONE);
                    jProject.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        db.child("Organizations").child("BUKC").child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/account").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String acc = dataSnapshot.getValue(String.class);
                if(!acc.equals(null)){
                    if(acc.equals("false")){
                        Toast.makeText(getActivity(),"Your account has been disabled or deleted, contact system administrator.", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        getActivity().finish();
                        outAnimation = new AlphaAnimation(1f, 0f);
                        outAnimation.setDuration(200);
                        progressBarHolder.setAnimation(outAnimation);
                        progressBarHolder.setVisibility(View.GONE);
                        jProject.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }
}
