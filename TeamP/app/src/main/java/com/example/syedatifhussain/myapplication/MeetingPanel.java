package com.example.syedatifhussain.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.PeriodFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class MeetingPanel extends AppCompatActivity {
    private String orgid;
    private String projectid;
    private ImageButton drawDoodle;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView nv;
    private DatabaseReference mdb;

    /*
    * Messaging Variables
    * */
    private static final int GALLERY_PICK = 1;
    private String recieverID;
    private DatabaseReference user_message_key;
    private DatabaseReference mDatabase;
    public String ph;
    private ImageButton sendMessage;
    private EditText chatMessage;
    private RecyclerView userMessagesList;
    private SwipeRefreshLayout mRefreshLayout;
    private final List<Messages> messageList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapterMeetingPanel messageAdapter;
    private Toolbar mChatToolbar;
    private ImageButton select_image;
    private RecyclerView storiesList;
    private RecyclerView storiesListSprint;
    private RecyclerView backlogstories_meeting_panel;

    private ImageButton optionsMeetingPanelChat;
    private StorageReference mImageStorage;
    public Uri imageUri=null;
    public Uri imageBoldUri = null;
    private String authUser;
    private DatabaseReference sdb;
    private DatabaseReference sdb1;
    private List<String> listMeetingType = new ArrayList<String>();
    private List<String> listRatingForMembers = new ArrayList<String>();




    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;

    private int itemPosition = 0;
    private String mLastKey = "";
    private String mPrevKey = "";

    private FirebaseAuth mauth;
    final int CAMERA_REQUEST = 1888;


    /*
    * Client id and pass intent receivers
    * */
    private String enduserid;
    private String enduserpass;
    private ImageButton projectInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_panel);

        /**
         * Meeting Panel Message Variables
         */
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mauth=FirebaseAuth.getInstance();
        sendMessage = (ImageButton)findViewById(R.id.btnSendMessageMeeting);
        chatMessage = (EditText) findViewById(R.id.input_message_meeting);
        userMessagesList = (RecyclerView)findViewById(R.id.recyclerviewpanelmessages);
        mImageStorage = FirebaseStorage.getInstance().getReference();
        messageAdapter = new MessageAdapterMeetingPanel(messageList,MeetingPanel.this);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setHasFixedSize(true);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);
        mRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeMeetingPanel);
        optionsMeetingPanelChat = (ImageButton)findViewById(R.id.optionsMeetingPanelChat);
        mImageStorage = FirebaseStorage.getInstance().getReference();
        mChatToolbar = (Toolbar)findViewById(R.id.chat_app_bar);
        projectInfo = (ImageButton)findViewById(R.id.btnProjectInfo);
        backlogstories_meeting_panel =  (RecyclerView) findViewById(R.id.backlogstories_meeting_panel);
        sdb = FirebaseDatabase.getInstance().getReference().child("Organizations/"+getIntent().getExtras().getString("orgid")+"/Projects").child(getIntent().getExtras().getString("projectid")+"/User Stories");
        sdb1 = FirebaseDatabase.getInstance().getReference().child("Organizations/"+getIntent().getExtras().getString("orgid")+"/Projects").child(getIntent().getExtras().getString("projectid")+"/Sprint Backlog/Sprint 1");





        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessageMethod();
            }
        });



        orgid = getIntent().getExtras().getString("orgid");
        projectid = getIntent().getExtras().getString("projectid");
//        drawDoodle = (ImageButton)findViewById(R.id.drawDoodle);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        nv = (NavigationView)findViewById(R.id.navigationViewMeetingPanel);
        mdb = FirebaseDatabase.getInstance().getReference();

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MeetingPanel.this);
        LayoutInflater inflater = LayoutInflater
                .from(MeetingPanel.this);
        final View mView = inflater.inflate(R.layout.layout_meeting_end_input, null);
        mBuilder.setView(mView);
        final AlertDialog ald = mBuilder.create();


        AlertDialog.Builder mBuilder2 = new AlertDialog.Builder(MeetingPanel.this);
        LayoutInflater inflater2 = LayoutInflater
                .from(MeetingPanel.this);
        final View mView2 = inflater2.inflate(R.layout.create_project_backlog, null);
        mBuilder2.setView(mView2);
        final AlertDialog ald2 = mBuilder2.create();


        AlertDialog.Builder mBuilderCstatus = new AlertDialog.Builder(MeetingPanel.this);
        LayoutInflater inflaterCstatus = LayoutInflater
                .from(MeetingPanel.this);
        final View mViewCstatus = inflaterCstatus.inflate(R.layout.layout_meeting_end, null);
        mBuilderCstatus.setView(mViewCstatus);

        final AlertDialog aldCstatus = mBuilderCstatus.create();


        mDatabase.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Projects").child(getIntent().getExtras().getString("projectid")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                String backLogStatus = dataSnapshot.child("Sprint Backlog").child("created").getValue(String.class);
                String cStatus = dataSnapshot.child("Meeting").child("cstatus").getValue(String.class);

                if(backLogStatus.equals("false")){
                    if(mauth.getCurrentUser() !=null){

                        if(dataSnapshot.child("head").getValue(String.class).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            projectInfo.setVisibility(View.VISIBLE);

                            Button temp = mView2.findViewById(R.id.btnSaveBackLog);
                            temp.setVisibility(View.INVISIBLE);
                            Button saveBackLog = mView2.findViewById(R.id.btnSaveBackLogFinal);

                            storiesList = mView2.findViewById(R.id.recyclerViewShowStoriesToAddBacklog);
                            storiesList.setHasFixedSize(true);
                            storiesList.setLayoutManager(new LinearLayoutManager(MeetingPanel.this));
                            storiesList.scrollToPosition(0);

                            LinearLayoutManager layoutManager2
                                    = new LinearLayoutManager(MeetingPanel.this, LinearLayoutManager.HORIZONTAL, false);
                            storiesListSprint = mView2.findViewById(R.id.recyclerViewProjectBacklogStories);
                            storiesListSprint.setHasFixedSize(true);
                            storiesListSprint.setLayoutManager(layoutManager2);
                            storiesListSprint.scrollToPosition(0);

                            final FirebaseRecyclerAdapter<stories,MeetingPanel.StoriesViewHolder> mRecyclerAdapter = new FirebaseRecyclerAdapter<stories, MeetingPanel.StoriesViewHolder>(
                                    stories.class,
                                    R.layout.user_story_view_layout,
                                    MeetingPanel.StoriesViewHolder.class,
                                    sdb) {
                                @Override
                                protected void populateViewHolder(final MeetingPanel.StoriesViewHolder viewHolder, final stories model, final int position) {

                                    viewHolder.setDescription(model.getDescription());
                                    viewHolder.setPriority(model.getPriority());
                                    viewHolder.setType(model.getType());
                                    viewHolder.setStoryName(getRef(position).getKey());
                                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            final String visit_story_id = getRef(position).getKey();
                                            HashMap<String,String> sprintData = new HashMap<String, String>();

                                            sprintData.put("description",model.getDescription());
                                            sprintData.put("priority",model.getPriority());
                                            sprintData.put("type",model.getType());
                                            mDatabase.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Projects").child(getIntent().getExtras().getString("projectid")).child("Sprint Backlog").child("Sprint 1").child(visit_story_id).setValue(sprintData);

                                        }
                                    });
                                }
                            };

                            final FirebaseRecyclerAdapter<stories,MeetingPanel.StoriesViewHolder> mRecyclerAdapter2 = new FirebaseRecyclerAdapter<stories, MeetingPanel.StoriesViewHolder>(
                                    stories.class,
                                    R.layout.user_story_view_layout_horizontal,
                                    MeetingPanel.StoriesViewHolder.class,
                                    sdb1) {
                                @Override
                                protected void populateViewHolder(final MeetingPanel.StoriesViewHolder viewHolder, final stories model, final int position) {
                                    viewHolder.setStoryNameAgain(getRef(position).getKey());
                                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            final String visit_story_id = getRef(position).getKey();
                                            Toast.makeText(MeetingPanel.this,visit_story_id,Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            };

                            mRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                                @Override
                                public void onItemRangeInserted(int positionStart, int itemCount) {
                                    super.onItemRangeInserted(positionStart, itemCount);
                                    int friendlyMessageCount = mRecyclerAdapter.getItemCount();
                                    LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(MeetingPanel.this);
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
                            mRecyclerAdapter2.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                                @Override
                                public void onItemRangeInserted(int positionStart, int itemCount) {
                                    super.onItemRangeInserted(positionStart, itemCount);
                                    int friendlyMessageCount = mRecyclerAdapter2.getItemCount();
                                    LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(MeetingPanel.this);
                                    int lastVisiblePosition =
                                            mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                                    // If the recycler view is initially being loaded or the
                                    // user is at the bottom of the list, scroll to the bottom
                                    // of the list to show the newly added message.
                                    if (lastVisiblePosition == -1 ||
                                            (positionStart >= (friendlyMessageCount - 1) &&
                                                    lastVisiblePosition == (positionStart - 1))) {
                                        storiesListSprint.scrollToPosition(positionStart);
                                    }
                                }

                            });
                            storiesList.setAdapter(mRecyclerAdapter);
                            storiesListSprint.setAdapter(mRecyclerAdapter2);
                            saveBackLog.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(mRecyclerAdapter2.getItemCount() == 0){
                                        Toast.makeText(MeetingPanel.this,"Cannot create empty back log.",Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        mDatabase.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Projects").child(getIntent().getExtras().getString("projectid")).child("Sprint Backlog").child("created").setValue("true");
                                        mDatabase.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Projects").child(getIntent().getExtras().getString("projectid")).child("Meeting").child("sprint").setValue("Sprint 1");
                                        Toast.makeText(MeetingPanel.this,"Backlog Created.",Toast.LENGTH_SHORT).show();
                                        ald2.dismiss();

                                    }


                                }
                            });
                            sdb.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            sdb1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            ald2.setCancelable(false);
                            ald2.setCanceledOnTouchOutside(false);
                            ald2.show();
                        }
                        else {
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MeetingPanel.this);
                            LayoutInflater inflater = LayoutInflater
                                    .from(MeetingPanel.this);
                            final View mView = inflater.inflate(R.layout.project_backlog_not_created_display, null);
                            mBuilder.setView(mView);
                            AlertDialog ald = mBuilder.create();
                            ald.setCancelable(false);
                            ald.setCanceledOnTouchOutside(false);
                            ald.show();

                        }
                    }
                    else {
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MeetingPanel.this);
                            LayoutInflater inflater = LayoutInflater
                                    .from(MeetingPanel.this);
                            final View mView = inflater.inflate(R.layout.project_backlog_not_created_display, null);
                            mBuilder.setView(mView);
                            AlertDialog ald = mBuilder.create();
                            ald.setCancelable(false);
                            ald.setCanceledOnTouchOutside(false);
                            ald.show();

                    }

                }
                else if(cStatus.equals("false")){
                    if(mauth.getCurrentUser() !=null){
                        aldCstatus.setCancelable(false);
                        aldCstatus.setCanceledOnTouchOutside(false);
                        aldCstatus.show();
                    }
                    else {

                        String checktype = dataSnapshot.child("Meeting").child("type").getValue(String.class);


                        ImageButton sprint = mView.findViewById(R.id.btnSprintMeeting);
                        ImageButton review = mView.findViewById(R.id.btnReviewMeeting);
                        ImageButton retro = mView.findViewById(R.id.btnRetrospectiveMeeting);

                        if(checktype.equals("Sprint")){
                            sprint.setEnabled(false);
                            retro.setEnabled(false);
                            review.setEnabled(true);
                        }
                        else if(checktype.equals("Review")){
                            sprint.setEnabled(false);
                            review.setEnabled(false);
                            retro.setEnabled(true);
                        }
                        else if(checktype.equals("Retrospective") && cStatus.equals("false") ) {
                            sprint.setEnabled(false);
                            review.setEnabled(false);
                            retro.setEnabled(false);

                            sprint.setVisibility(View.INVISIBLE);
                            review.setVisibility(View.INVISIBLE);
                            retro.setVisibility(View.INVISIBLE);

                            verifyProjectEnd();

                        }

                        review.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ald.dismiss();
                                changeCStatus("Review");

                            }
                        });
                        retro.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ald.dismiss();
                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MeetingPanel.this);
                                LayoutInflater inflater = LayoutInflater
                                        .from(MeetingPanel.this);
                                final View mView = inflater.inflate(R.layout.layout_display_notice_final_meeting, null);
                                mBuilder.setView(mView);

                                final AlertDialog ald = mBuilder.create();

                                Button btnConfirmNotice = mView.findViewById(R.id.btnConfirmNoticeFinalMeeting);
                                btnConfirmNotice.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ald.dismiss();
                                        changeCStatus("Retrospective");
                                    }
                                });


                                ald.setCancelable(true);
                                ald.setCanceledOnTouchOutside(true);
                                ald.show();

                            }
                        });

                        mBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                ald.dismiss();

                            }
                        });

                        Button btnLearnMore = mView.findViewById(R.id.btnLearnMoreMeetings);
                        btnLearnMore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MeetingPanel.this);
                                LayoutInflater inflater = LayoutInflater
                                        .from(MeetingPanel.this);
                                final View mView = inflater.inflate(R.layout.learn_more_meetings, null);
                                mBuilder.setView(mView);
                                final AlertDialog ald = mBuilder.create();

                                ald.setCancelable(true);
                                ald.setCanceledOnTouchOutside(true);
                                ald.show();

                            }
                        });

                        String test2 = dataSnapshot.child("Meeting").child("type").getValue(String.class);
                        if(test2.equals("Review")){
                            review.setEnabled(false);

                        }
                        else if(test2.equals("Retrospective")){
                            retro.setEnabled(false);
                        }

                        if(!checktype.equals("Retrospective")){

                            ald.setCancelable(false);
                            ald.setCanceledOnTouchOutside(false);
                            ald.show();
                        }

                    }
                }
                else if (cStatus.equals("true")){
                    aldCstatus.dismiss();

                    //populateBackLog();
                    ald.dismiss();
                    projectInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(FirebaseAuth.getInstance().getCurrentUser() !=null){

                                if(dataSnapshot.child("head").getValue(String.class).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    Intent intent = new Intent(MeetingPanel.this, ViewProjectInfoTeamLead.class);
                                    intent.putExtra("orgid",getIntent().getExtras().getString("orgid"));
                                    intent.putExtra("projectid",getIntent().getExtras().getString("projectid"));
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(MeetingPanel.this,"Insufficient Rights",Toast.LENGTH_SHORT).show();

                                }
                            }
                            else {
                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MeetingPanel.this);
                                LayoutInflater inflater = LayoutInflater
                                        .from(MeetingPanel.this);
                                final View mView = inflater.inflate(R.layout.project_info, null);
                                mBuilder.setView(mView);
                                TextView projectName = mView.findViewById(R.id.txtProjectInfoProjectName);
                                projectName.setText(getIntent().getExtras().getString("projectid"));
                                final AlertDialog ald = mBuilder.create();
                                final TextView projectStartDate = mView.findViewById(R.id.txtProjectInfoStartDate);
                                final TextView projectMeetingType = mView.findViewById(R.id.txtProjectInfoCurrentMeeting);
                                final TextView projectSprint = mView.findViewById(R.id.txtProjectInfoCurrentSprint);
                                Button endMeeting = mView.findViewById(R.id.btnEndCurrentMeetingProjectInfo);
                                mDatabase.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Projects").child(getIntent().getExtras().getString("projectid")).child("Meeting").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        projectStartDate.setText(dataSnapshot.child("Date").getValue().toString());
                                        projectMeetingType.setText(dataSnapshot.child("type").getValue().toString()+" Meeting");
                                        projectSprint.setText(dataSnapshot.child("sprint").getValue().toString());
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                endMeeting.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MeetingPanel.this);
                                        builder1.setMessage("Are you sure you want to end current meeting session?");
                                        builder1.setCancelable(true);

                                        builder1.setPositiveButton(
                                                "Yes",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        ald.dismiss();
                                                        mDatabase.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Projects").child(getIntent().getExtras().getString("projectid")).child("Meeting").child("cstatus").setValue("false");
                                                        mDatabase.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Projects").child(getIntent().getExtras().getString("projectid")).child("Sprint Backlog").child("Sprint 1").child("completed").setValue("true");
                                                    }
                                                });

                                        builder1.setNegativeButton(
                                                "No",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                });

                                        AlertDialog alert11 = builder1.create();
                                        alert11.show();

                                    }
                                });
                                ald.show();
                            }

                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(mauth.getCurrentUser() !=null){
            authUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
            setSupportActionBar(mChatToolbar);
            mDrawerLayout.addDrawerListener(mToggle);
            mToggle.syncState();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
        else {
            setSupportActionBar(mChatToolbar);
            try{
                if(getIntent().getExtras().get("enduserid").toString() !=null && getIntent().getExtras().get("enduserpass").toString()!=null ){
                    enduserid = getIntent().getExtras().get("enduserid").toString();
                    enduserpass = getIntent().getExtras().get("enduserpass").toString();
                    authUser = enduserid;
                }
                else {

                }
            }catch (Exception ex){
                finish();
                startActivity(getIntent());
            }

        }

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if(menuItem.getItemId() == R.id.menu_home){
                    Intent intent = new Intent(MeetingPanel.this, Main3Activity.class);
                    intent.putExtra("fid","home");
                    startActivity(intent);
                }
                else if(menuItem.getItemId() == R.id.menu_projects){
                    Intent intent2 = new Intent(MeetingPanel.this, Main3Activity.class);
                    intent2.putExtra("fid","projects");
                    startActivity(intent2);

                }
                else if(menuItem.getItemId() == R.id.menu_contacts){
                    Intent intent3 = new Intent(MeetingPanel.this, Main3Activity.class);
                    intent3.putExtra("fid","contacts");
                    startActivity(intent3);
                }

                else if(menuItem.getItemId() == R.id.menu_chats){
                    Intent intent4 = new Intent(MeetingPanel.this, Main3Activity.class);
                    intent4.putExtra("fid","chats");
                    startActivity(intent4);
                }
                else if(menuItem.getItemId() == R.id.menu_settings){
                    Intent intent5 = new Intent(MeetingPanel.this, Main3Activity.class);
                    intent5.putExtra("fid","settings");
                    startActivity(intent5);
                }
                return true;
            }
        });

        optionsMeetingPanelChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]{
                        "Send Image",
                        "Take Image",
                        "Draw Doodle"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(MeetingPanel.this);
                builder.setTitle("Choose action");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        if(position == 0){
                            Intent galleryIntent = new Intent();
                            galleryIntent.setType("image/*");
                            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                            startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),GALLERY_PICK);
                        }
                        if(position == 1){
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        }
                        if(position == 2){
                            Intent intent = new Intent(MeetingPanel.this, doodleActivity.class);
                            intent.putExtra("orgid",getIntent().getExtras().getString("orgid"));
                            intent.putExtra("projectid",getIntent().getExtras().getString("projectid"));
                            intent.putExtra("authuser",authUser);
                            startActivity(intent);
                        }
                    }
                });
                builder.show();
            }
        });
        FetchMessages();

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
                itemPosition = 0;
                FetchMoreMessages();


            }
        });

    }

    private void verifyProjectEnd() {

        mDatabase.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Projects").child(getIntent().getExtras().getString("projectid")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String cstatus = dataSnapshot.child("Meeting").child("cstatus").getValue(String.class);
                String type = dataSnapshot.child("Meeting").child("type").getValue(String.class);

                if(dataSnapshot.child("Rating").exists()){
                    String rating = dataSnapshot.child("Rating").getValue(String.class);

                }
                else {
                    if(cstatus.equals("false") && type.equals("Retrospective")){


                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MeetingPanel.this);
                        LayoutInflater inflater = LayoutInflater
                                .from(MeetingPanel.this);
                        final View mView = inflater.inflate(R.layout.layout_get_client_rating, null);
                        mBuilder.setView(mView);

                        final AlertDialog ald = mBuilder.create();
                        final RatingBar rr = mView.findViewById(R.id.ratingBarGetClient);
                        Button submitRating = mView.findViewById(R.id.btnSubmitRatingClient);



                        ald.setCancelable(false);
                        ald.setCanceledOnTouchOutside(false);
                        ald.show();

                        submitRating.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ald.dismiss();
                                float rating = rr.getRating();
                                mDatabase.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Projects").child(getIntent().getExtras().getString("projectid")).child("Rating").setValue(String.valueOf(rating));
                                mDatabase.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Projects").child(getIntent().getExtras().getString("projectid")).child("Completed").setValue("true");
                                mDatabase.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Projects").child(getIntent().getExtras().getString("projectid")).child("Completion").setValue(ServerValue.TIMESTAMP);
                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void populateBackLog() {
//        //populate top horizontal recycler view to view backlog stories



    }

    @Override
    protected void onStart() {
        //sdb1 = FirebaseDatabase.getInstance().getReference().child("Organizations/"+getIntent().getExtras().getString("orgid")+"/Projects/"+getIntent().getExtras().getString("projectid")+"/Sprint Backlog/Sprint 1").child(getIntent().getExtras().getString("projectid")).child("Sprint Backlog");

        super.onStart();
        try{
            LinearLayoutManager layoutManager2
                    = new LinearLayoutManager(MeetingPanel.this, LinearLayoutManager.HORIZONTAL, false);
            backlogstories_meeting_panel.setHasFixedSize(true);
            backlogstories_meeting_panel.setLayoutManager(layoutManager2);
            backlogstories_meeting_panel.scrollToPosition(0);

            final FirebaseRecyclerAdapter<stories,MeetingPanel.StoriesViewHolder> mRecyclerAdapter = new FirebaseRecyclerAdapter<stories, MeetingPanel.StoriesViewHolder>(
                    stories.class,
                    R.layout.user_story_view_layout_horizontal,
                    MeetingPanel.StoriesViewHolder.class,
                    sdb) {

                @Override
                protected void populateViewHolder(MeetingPanel.StoriesViewHolder viewHolder, final stories model, final int position) {
                    String test = getRef(position).getKey();
                    viewHolder.setStoryNameAgain(test);
                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final String visit_story_id = getRef(position).getKey();

                            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MeetingPanel.this);
                            LayoutInflater inflater = LayoutInflater
                                    .from(MeetingPanel.this);
                            final View mView = inflater.inflate(R.layout.view_user_single_story, null);
                            mBuilder.setView(mView);
                            final AlertDialog ald = mBuilder.create();
                            final TextView txtStoryName = mView.findViewById(R.id.txtStoryNameSingle);
                            final TextView txtStoryDesc = mView.findViewById(R.id.txtStoryDescriptionSingle);
                            final TextView txtStoryType = mView.findViewById(R.id.txxtStoryTypeSingle);
                            final TextView txtStoryPriority = mView.findViewById(R.id.txtStoryPrioritySingle);



                            mDatabase.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Projects").child(getIntent().getExtras().getString("projectid")).child("User Stories").child(visit_story_id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    String desc = dataSnapshot.child("description").getValue().toString();
                                    String prio = dataSnapshot.child("priority").getValue().toString();
                                    String type =  dataSnapshot.child("type").getValue().toString();
                                    txtStoryName.setText(visit_story_id);
                                    txtStoryDesc.setText(desc);
                                    txtStoryPriority.setText(prio);
                                    txtStoryType.setText(type);


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    Toast.makeText(MeetingPanel.this,databaseError.getMessage().toString(),Toast.LENGTH_SHORT).show();

                                }
                            });

                            ald.setCancelable(true);
                            ald.setCanceledOnTouchOutside(true);
                            ald.show();

                        }
                    });
                }
            };

            backlogstories_meeting_panel.setAdapter(mRecyclerAdapter);
            sdb.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception ex){
            Toast.makeText(MeetingPanel.this,ex.toString(),Toast.LENGTH_SHORT).show();
        }

        mDatabase.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Projects").child(getIntent().getExtras().getString("projectid")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("Completed").exists() && dataSnapshot.child("Completion").exists()){
                    String completed = dataSnapshot.child("Completed").getValue(String.class);
                    if(completed.equals("true")){
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MeetingPanel.this);
                        LayoutInflater inflater = LayoutInflater
                                .from(MeetingPanel.this);
                        final View mView = inflater.inflate(R.layout.layout_project_done, null);
                        mBuilder.setView(mView);

                        final AlertDialog ald = mBuilder.create();

                        RatingBar ratingBarDisplayProjectRating = mView.findViewById(R.id.ratingBarDisplayProjectRating);
                        TextView txtDisplayProjectNameStats = mView.findViewById(R.id.txtDisplayProjectNameStats);
                        TextView txtCompletionDateDisplay = mView.findViewById(R.id.txtCompletionDateDisplay);
                        TextView txtRatingInStringDisplay = mView.findViewById(R.id.txtRatingInStringDisplay);


                        float ratingValue = Float.valueOf(dataSnapshot.child("Rating").getValue(String.class));
                        ratingBarDisplayProjectRating.setEnabled(false);
                        ratingBarDisplayProjectRating.setRating(ratingValue);

                        txtDisplayProjectNameStats.setText(getIntent().getExtras().getString("projectid"));
                        txtRatingInStringDisplay.setText(dataSnapshot.child("Rating").getValue(String.class));



                        long timestamp = dataSnapshot.child("Completion").getValue(Long.class);

                        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yy hh:mm a");

                        txtCompletionDateDisplay.setText(sfd.format(new Date(timestamp)).toString());

                        int i=0;

                        for (DataSnapshot issue : dataSnapshot.child("Members").getChildren()) {
                            //mDatabase.child("Organizations").child(val).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Contacts").child("Phone").setValue(phoneNum);
                            if(issue.child("name").equals("End User")){

                            }
                            else {
                                listRatingForMembers.add(issue.getKey());
                            }
                            mDatabase.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Users").child(listRatingForMembers.get(i)).child("rating").setValue(dataSnapshot.child("Rating").getValue(String.class));
                            i++;
                        }

                        ald.setCancelable(false);
                        ald.setCanceledOnTouchOutside(false);
                        ald.show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void changeCStatus(String meeting) {
        mDatabase.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Projects").child(getIntent().getExtras().getString("projectid")).child("Meeting").child("type").setValue(meeting);
        mDatabase.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Projects").child(getIntent().getExtras().getString("projectid")).child("Meeting").child("cstatus").setValue("true");
        onStart();
        messageList.clear();
        FetchMessages();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            imageUri = data.getData();

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

                mDatabase.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Projects").child(getIntent().getExtras().getString("projectid")).child("Meeting").child("type").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String val = dataSnapshot.getValue(String.class);

                        final String current_user_ref = "Organizations/"+getIntent().getExtras().getString("orgid")+"/Projects/"+getIntent().getExtras().getString("projectid")+"/Messages/"+val;
                        final String chat_user_ref = "Organizations/"+getIntent().getExtras().getString("orgid")+"/Projects/"+getIntent().getExtras().getString("projectid")+"/Messages/"+val;

                        DatabaseReference user_message_push = mDatabase.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Projects").child(getIntent().getExtras().getString("projectid")).child("Messages").child(val).push();

                        final String push_id = user_message_push.getKey();

                        StorageReference filepath = mImageStorage.child("message_images").child(push_id+".jpg");
                        filepath.putFile(imageBoldUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    String download_url = task.getResult().getDownloadUrl().toString();

                                    Map messageMap = new HashMap();
                                    messageMap.put("message",download_url);
                                    messageMap.put("type","image");
                                    messageMap.put("time", ServerValue.TIMESTAMP);
                                    messageMap.put("pid",getIntent().getExtras().getString("projectid"));
                                    messageMap.put("from",authUser);

                                    Map messageUserMap = new HashMap();
                                    messageUserMap.put(current_user_ref+"/"+push_id,messageMap);
                                    messageUserMap.put(chat_user_ref+"/"+push_id,messageMap);

                                    chatMessage.setText("");

                                    mDatabase.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            if(databaseError!=null){
                                                Log.d("Chat Log : ",databaseError.getMessage().toString());
                                            }
                                            chatMessage.setText("");
                                        }
                                    });

                                }
                                else {
                                    Log.d(task.getException().toString(),"Check this please ");
                                    Toast.makeText(MeetingPanel.this,task.getException().toString(),Toast.LENGTH_SHORT).show();


                                }

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }
    }


    private void FetchMoreMessages() {

        mDatabase.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Projects").child(getIntent().getExtras().getString("projectid")).child("Meeting").child("type").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String val = dataSnapshot.getValue(String.class);
                DatabaseReference messageRef = mDatabase.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Projects").child(getIntent().getExtras().getString("projectid")).child("Messages").child(val);
                Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

                messageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Messages message = dataSnapshot.getValue(Messages.class);
                        String messageKey = dataSnapshot.getKey();

                        if(!mPrevKey.equals(messageKey)){
                            messageList.add(itemPosition++,message);
                        }
                        else{
                            mPrevKey = mLastKey;
                        }

                        if(itemPosition == 1){
                            mLastKey = messageKey;
                        }

                        messageAdapter.notifyDataSetChanged();
                        mRefreshLayout.setRefreshing(false);
                        linearLayoutManager.scrollToPositionWithOffset(10,0);

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

//    mDatabase.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Projects").child(getIntent().getExtras().getString("projectid")).child("Meeting").child("type").addListenerForSingleValueEvent(new ValueEventListener() {
//        @Override
//        public void onDataChange(DataSnapshot dataSnapshot) {
//            String val = dataSnapshot.getValue(String.class);
//
//
//        }
//
//        @Override
//        public void onCancelled(DatabaseError databaseError) {
//
//        }
//    });

    private void FetchMessages() {
        mDatabase.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Projects").child(getIntent().getExtras().getString("projectid")).child("Meeting").child("type").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String val = dataSnapshot.getValue(String.class);

                DatabaseReference messageRef = mDatabase.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Projects").child(getIntent().getExtras().getString("projectid")+"/Messages/"+val);
                Query messageQuery = messageRef.limitToLast(mCurrentPage = TOTAL_ITEMS_TO_LOAD);

                messageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Messages messages = dataSnapshot.getValue(Messages.class);
                        itemPosition++;
                        if(itemPosition == 1){
                            String messageKey = dataSnapshot.getKey();

                            mLastKey = messageKey;
                            mPrevKey = messageKey;

                        }
                        messageList.add(messages);
                        messageAdapter.notifyDataSetChanged();
                        userMessagesList.scrollToPosition(messageList.size() - 1);

                        mRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void SendMessageMethod(){
        final String messageText = chatMessage.getText().toString();
        if(TextUtils.isEmpty(messageText)){
            Toast.makeText(MeetingPanel.this,"Cannot send empty message",Toast.LENGTH_SHORT).show();
        }
        else{

            mDatabase.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Projects").child(getIntent().getExtras().getString("projectid")).child("Meeting").child("type").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String val = dataSnapshot.getValue(String.class);
                    String message_sender_ref = "Organizations/"+getIntent().getExtras().getString("orgid")+"/Projects/"+getIntent().getExtras().getString("projectid")+"/Messages/"+val;
                    String message_reciever_ref = "Organizations/"+getIntent().getExtras().getString("orgid")+"/Projects/"+getIntent().getExtras().getString("projectid")+"/Messages/"+val;
                    user_message_key = mDatabase.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Projects/"+getIntent().getExtras().getString("projectid")+"/Messages/"+val).push();
                    String message_push_id = user_message_key.getKey();
                    Map messageTextBody = new HashMap();
                    messageTextBody.put("message",messageText);
                    messageTextBody.put("seen",false);
                    messageTextBody.put("type","text");
                    messageTextBody.put("time", ServerValue.TIMESTAMP);
                    messageTextBody.put("pid",getIntent().getExtras().getString("projectid"));
                    messageTextBody.put("from",authUser);

                    Map messageBodyDetails = new HashMap();
                    messageBodyDetails.put(message_sender_ref+"/"+message_push_id,messageTextBody);
                    messageBodyDetails.put(message_reciever_ref+"/"+message_push_id,messageTextBody);
                    chatMessage.setText("");

                    mDatabase.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError!=null){
                                Log.d("Chat Log : ",databaseError.getMessage().toString());
                            }
                            chatMessage.setText("");
                        }
                    });


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
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
        public void setStoryNameAgain(String storyName){
            TextView sname = mView.findViewById(R.id.holdStoryName);
            sname.setText(storyName);
        }
        public void setType(String type){
            TextView storyType = mView.findViewById(R.id.txtViewUserStoryType);
            storyType.setText(type);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
