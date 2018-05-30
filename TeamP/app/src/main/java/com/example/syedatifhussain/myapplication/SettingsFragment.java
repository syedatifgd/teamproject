package com.example.syedatifhussain.myapplication;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by SyedAtif on 11/14/2017.
 */

public class SettingsFragment extends Fragment {

    private DatabaseReference db;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view  = inflater.inflate(R.layout.settings_fragment,container,false);
        db = FirebaseDatabase.getInstance().getReference();


        return view;
    }
}
