package com.example.syedatifhussain.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.PeriodFormat;

import static java.lang.String.format;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ProjectMeetingCheck extends AppCompatActivity {

    private TextView txtMeetingTime;
    private DatabaseReference mdb;
    private DatabaseReference sdb;
    private Button btnViewUserStories;
    private RecyclerView storiesList;
    private List<String> listRecIDs = new ArrayList<String>();
    private List<String> listTokens = new ArrayList<String>();
    private String enduserid;
    private String enduserpass;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;


    FrameLayout progressBarHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_meeting_check);
        txtMeetingTime = (TextView)findViewById(R.id.txtMeetingTime);
        btnViewUserStories = (Button) findViewById(R.id.btnViewUserStories);
        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            enduserid = getIntent().getExtras().get("enduserid").toString();
            enduserpass = getIntent().getExtras().get("enduserpass").toString();
        }
        mdb = FirebaseDatabase.getInstance().getReference();
        sdb = FirebaseDatabase.getInstance().getReference().child("Organizations/"+getIntent().getExtras().getString("orgid")+"/Projects").child(getIntent().getExtras().getString("projectid")+"/User Stories");

        mdb.child("Organizations/"+getIntent().getExtras().getString("orgid")+"/Projects").child(getIntent().getExtras().getString("projectid")+"/Members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot issue : dataSnapshot.getChildren()) {
                    final String test = issue.getKey();
                    if(issue.child("role").getValue().toString().equals("End User")){

                    }
                    else {
                        listRecIDs.add(test);
                        callToken(test);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        mdb.child("Organizations/"+getIntent().getExtras().getString("orgid")+"/Projects").child(getIntent().getExtras().getString("projectid")+"/Meeting/Status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String val  = dataSnapshot.getValue(String.class);

                if(val == null){
                    outAnimation = new AlphaAnimation(1f, 0f);
                    outAnimation.setDuration(200);
                    progressBarHolder.setAnimation(outAnimation);
                    progressBarHolder.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Intent intent = new Intent(ProjectMeetingCheck.this, ProjectActivity.class);
                    intent.putExtra("userid",getIntent().getExtras().getString("userid"));
                    intent.putExtra("userpass",getIntent().getExtras().getString("userpass"));
                    intent.putExtra("pname",getIntent().getExtras().getString("projectid"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }

                else if(val.equals("false")){

                    mdb.child("Organizations/"+getIntent().getExtras().getString("orgid")+"/Projects").child(getIntent().getExtras().getString("projectid")+"/Meeting/Time").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String val1  = dataSnapshot.getValue(String.class);
                            if(!val1.isEmpty()){
                                outAnimation = new AlphaAnimation(1f, 0f);
                                outAnimation.setDuration(200);
                                progressBarHolder.setAnimation(outAnimation);
                                progressBarHolder.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                mdb.child("Organizations/"+getIntent().getExtras().getString("orgid")+"/Projects").child(getIntent().getExtras().getString("projectid")+"/Meeting/Date").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String val2  = dataSnapshot.getValue(String.class);
                                        final long timeInterval = 1000;
                                        final boolean[] test = {true};
                                        final Runnable runnable = new Runnable() {
                                            public void run() {
                                                while (test[0]) {
                                                    runOnUiThread(new Runnable() {

                                                        @Override
                                                        public void run() {
                                                            // convert these to a DateTime object
                                                            DateTime targetDateTime = DateTime.parse(format("%s %s", val2, val1), DateTimeFormat.forPattern("dd/MM/yyyy HH:mm"));

                                                            // grab a timestamp
                                                            DateTime now = DateTime.now();

                                                            // create a period object between the two
                                                            Period period = new Period(now, targetDateTime);

                                                            // convert the period to a printable String
                                                            String prettyPeriod = PeriodFormat.getDefault().print(period);
                                                            String two = period.getDays() + " Days, " + period.getHours() + " Hours, " + period.getMinutes()+" Minute(s) Left";


                                                            if(period.getMillis() <=0 && period.getDays() <=0 && period.getMinutes() <=0 && period.getHours()<=0){


                                                                mdb.child("Organizations/"+getIntent().getExtras().getString("orgid")+"/Projects").child(getIntent().getExtras().getString("projectid")+"/Meeting/Status").setValue("true");
                                                                Intent intent = new Intent(ProjectMeetingCheck.this, MeetingPanel.class);
                                                                intent.putExtra("orgid",getIntent().getExtras().getString("orgid"));
                                                                intent.putExtra("projectid",getIntent().getExtras().getString("projectid"));
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(intent);
                                                                test[0] = false;
                                                            }
                                                            else{
                                                                txtMeetingTime.setText(two);
                                                            }

                                                        }
                                                    });

                                                    try {
                                                        Thread.sleep(timeInterval);
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        };

                                        Thread thread = new Thread(runnable);

                                        thread.start();

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    if(FirebaseAuth.getInstance().getCurrentUser() == null){
                        outAnimation = new AlphaAnimation(1f, 0f);
                        outAnimation.setDuration(200);
                        progressBarHolder.setAnimation(outAnimation);
                        progressBarHolder.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


                        mdb.child("Organizations/"+getIntent().getExtras().getString("orgid")+"/Projects").child(getIntent().getExtras().getString("projectid")+"/Meeting/client").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String val = dataSnapshot.getValue(String.class);
                                if(val !=null){
                                    if(val.equals("true")){
                                        Intent intent = new Intent(ProjectMeetingCheck.this, MeetingPanel.class);
                                        intent.putExtra("orgid",getIntent().getExtras().getString("orgid"));
                                        intent.putExtra("projectid",getIntent().getExtras().getString("projectid"));
                                        intent.putExtra("enduserid",enduserid);
                                        intent.putExtra("enduserpass",enduserpass);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);

                                    }
                                    else {
                                        mdb.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Projects").child(getIntent().getExtras().getString("projectid")).child("Meeting").child("client").setValue("true");
                                        HashMap<String,String> notiData = new HashMap<String, String>();

                                        for(int i=0;i<listRecIDs.size();i++){
                                            notiData.put("text","User has joined the meeting panel for project "+getIntent().getExtras().getString("projectid"));
                                            notiData.put("project",getIntent().getExtras().getString("projectid"));
                                            notiData.put("organization",getIntent().getExtras().getString("orgid"));
                                            notiData.put("device",listTokens.get(i));
                                            mdb.child("Notifications").child(getIntent().getExtras().getString("projectid")).push().setValue(notiData);
                                        }

                                        Intent intent = new Intent(ProjectMeetingCheck.this, MeetingPanel.class);
                                        intent.putExtra("orgid",getIntent().getExtras().getString("orgid"));
                                        intent.putExtra("projectid",getIntent().getExtras().getString("projectid"));
                                        intent.putExtra("enduserid",enduserid);
                                        intent.putExtra("enduserpass",enduserpass);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    else {
                        outAnimation = new AlphaAnimation(1f, 0f);
                        outAnimation.setDuration(200);
                        progressBarHolder.setAnimation(outAnimation);
                        progressBarHolder.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        Intent intent = new Intent(ProjectMeetingCheck.this, MeetingPanel.class);
                        intent.putExtra("orgid",getIntent().getExtras().getString("orgid"));
                        intent.putExtra("projectid",getIntent().getExtras().getString("projectid"));
                        intent.putExtra("enduserid",enduserid);
                        intent.putExtra("enduserpass",enduserpass);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnViewUserStories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getContext());
                LayoutInflater inflater = LayoutInflater
                        .from(getApplicationContext());
                final View mView = inflater.inflate(R.layout.dialog_viewuserstories, null);
                mBuilder.setView(mView);
                storiesList = mView.findViewById(R.id.viewuserstories_container);
                storiesList.setHasFixedSize(true);
                storiesList.setLayoutManager(new LinearLayoutManager(ProjectMeetingCheck.this));

                final FirebaseRecyclerAdapter<stories,ProjectMeetingCheck.StoriesViewHolder> mRecyclerAdapter = new FirebaseRecyclerAdapter<stories, ProjectMeetingCheck.StoriesViewHolder>(
                        stories.class,
                        R.layout.user_story_view_layout,
                        ProjectMeetingCheck.StoriesViewHolder.class,
                        sdb) {
                    @Override
                    protected void populateViewHolder(final ProjectMeetingCheck.StoriesViewHolder viewHolder, final stories model, final int position) {

                        viewHolder.setDescription(model.getDescription());
                        viewHolder.setPriority(model.getPriority());
                        viewHolder.setType(model.getType());
                        viewHolder.setStoryName(getRef(position).getKey());
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final String visit_story_id = getRef(position).getKey();

                            }
                        });
                    }
                };

                mRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        super.onItemRangeInserted(positionStart, itemCount);
                        int friendlyMessageCount = mRecyclerAdapter.getItemCount();
                        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(ProjectMeetingCheck.this);
                        int lastVisiblePosition =
                                mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                        // If the recycler view is initially being loaded or the
                        // user is at the bottom of the list, scroll to the bottom
                        // of the list to show the newly added message.
                        if (lastVisiblePosition == -1 ||
                                (positionStart >= (friendlyMessageCount - 1) &&
                                        lastVisiblePosition == (positionStart - 1))) {
                            storiesList.scrollToPosition(positionStart);
                        }
                    }

                });
                storiesList.setAdapter(mRecyclerAdapter);
                sdb.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




                AlertDialog ald = mBuilder.create();
                ald.show();

            }
        });
    }

    private void callToken(String userid) {
        mdb.child("Organizations/"+getIntent().getExtras().getString("orgid")+"/Users").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listTokens.add(dataSnapshot.child("deviceToken").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static class StoriesViewHolder extends RecyclerView.ViewHolder{
        View mView;
        Boolean test = false;

        public StoriesViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDescription(String description){
            TextView storyDescription = mView.findViewById(R.id.txtViewUserStoryDescription);
            storyDescription.setText(description);
        }

        public void setPriority(String priority){
            TextView storyPriority = mView.findViewById(R.id.txtViewUserStoryPriority);
            storyPriority.setText(priority);
        }

        public void setStoryName(String storyName){
            TextView sname = mView.findViewById(R.id.txtViewUserStoryName);
            sname.setText(storyName);
        }
        public void setType(String type){
            TextView storyType = mView.findViewById(R.id.txtViewUserStoryType);
            storyType.setText(type);
        }
    }
}
