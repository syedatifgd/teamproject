package com.example.syedatifhussain.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.print.PrintHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import static java.lang.String.format;

public class UserStories_Wizard_Step_5 extends Fragment {
    private Button btnSetTime;
    private TimePicker timePicker;
    private DatabaseReference mdb;
    public int hour=0;
    public int minute=0;
    private String enduserid;
    private String enduserpass;

    public UserStories_Wizard_Step_5() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_stories__wizard__step_5, container, false);
        btnSetTime = view.findViewById(R.id.btnSetTime);
        timePicker = view.findViewById(R.id.timePicker2);
        timePicker.setIs24HourView(true);
        hour = timePicker.getCurrentHour();
        minute = timePicker.getCurrentMinute();
        mdb = FirebaseDatabase.getInstance().getReference();

        Toast.makeText(getActivity(),getActivity().getIntent().getExtras().getString("date"),Toast.LENGTH_SHORT);

        btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enduserid = getActivity().getIntent().getExtras().get("enduserid").toString();
                enduserpass = getActivity().getIntent().getExtras().get("enduserpass").toString();
                mdb.child("Organizations").child(getActivity().getIntent().getExtras().getString("orgid")).child("Projects").child(getActivity().getIntent().getExtras().getString("projectid")).child("Meeting").child("Time").setValue(hour+":"+minute);
                mdb.child("Organizations").child(getActivity().getIntent().getExtras().getString("orgid")).child("Projects").child(getActivity().getIntent().getExtras().getString("projectid")).child("Meeting").child("Status").setValue("false");
                mdb.child("Organizations").child(getActivity().getIntent().getExtras().getString("orgid")).child("Projects").child(getActivity().getIntent().getExtras().getString("projectid")).child("Meeting").child("client").setValue("false");
                mdb.child("Organizations").child(getActivity().getIntent().getExtras().getString("orgid")).child("Projects").child(getActivity().getIntent().getExtras().getString("projectid")).child("Meeting").child("cstatus").setValue("true");
                mdb.child("Organizations").child(getActivity().getIntent().getExtras().getString("orgid")).child("Projects").child(getActivity().getIntent().getExtras().getString("projectid")).child("Meeting").child("type").setValue("Sprint");
                mdb.child("Organizations").child(getActivity().getIntent().getExtras().getString("orgid")).child("Projects").child(getActivity().getIntent().getExtras().getString("projectid")).child("status").setValue("true");

                mdb.child("Organizations").child(getActivity().getIntent().getExtras().getString("orgid")).child("Projects").child(getActivity().getIntent().getExtras().getString("projectid")).child("Sprint Backlog").child("created").setValue("false");



                DateTime targetDateTime = DateTime.parse(format("%s %s", getActivity().getIntent().getExtras().getString("date"), hour+":"+minute), DateTimeFormat.forPattern("dd/MM/yyyy HH:mm"));
                DateTime dd = targetDateTime.plusHours(8);
                String endDate = dd.getDayOfMonth()+"/"+dd.getMonthOfYear()+"/"+dd.getYear()+" "+dd.getHourOfDay()+":"+dd.getMinuteOfHour();
                mdb.child("Organizations").child(getActivity().getIntent().getExtras().getString("orgid")).child("Projects").child(getActivity().getIntent().getExtras().getString("projectid")).child("Meeting").child("end").setValue(endDate);


                Intent intent = new Intent(getActivity(), ProjectMeetingCheck.class);
                intent.putExtra("orgid",getActivity().getIntent().getExtras().getString("orgid"));
                intent.putExtra("projectid",getActivity().getIntent().getExtras().getString("projectid"));
                intent.putExtra("enduserid",enduserid);
                intent.putExtra("enduserpass",enduserpass);

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        return view;
    }

}
