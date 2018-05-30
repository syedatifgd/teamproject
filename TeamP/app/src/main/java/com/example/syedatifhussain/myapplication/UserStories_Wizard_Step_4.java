package com.example.syedatifhussain.myapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Calendar;
import java.util.Date;

import static java.lang.String.format;

public class UserStories_Wizard_Step_4 extends Fragment {
    private Button btnSetDate;
    private DatePicker datePicker;
    private DatabaseReference mdb;


    public UserStories_Wizard_Step_4() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user_stories__wizard__step_4, container, false);
        btnSetDate = view.findViewById(R.id.btnSetDate);
        datePicker = view.findViewById(R.id.datePicker);
        mdb = FirebaseDatabase.getInstance().getReference();

        btnSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();
                //Toast.makeText(getActivity(),day+" / "+month+" / "+year,Toast.LENGTH_SHORT).show();

                mdb.child("Organizations").child(getActivity().getIntent().getExtras().getString("orgid")).child("Projects").child(getActivity().getIntent().getExtras().getString("projectid")).child("Meeting").child("Date").setValue(day+"/"+month+"/"+year);


                //DateTime targetDateTime = DateTime.parse(format("%s", day+"/"+month+"/"+year), DateTimeFormat.forPattern("dd/MM/yyyy"));
                String test = day+"/"+month+"/"+year;
                getActivity().getIntent().putExtra("date", test);



                UserStories_Wizard.switchFragment(4);
            }
        });

        return view;
    }
    public Date getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }

}
