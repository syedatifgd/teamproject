package com.example.syedatifhussain.myapplication;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatActivity extends AppCompatActivity implements Session.SessionListener,PublisherKit.PublisherListener  {
    Intent startAgain;

    /*
    * API Keys and Variables for Video Call Feature
    * */
    private static int checkStream = 0;
    private static String API_KEY = "falseee";
    private static String SESSION_ID = "falseee";
    private static String TOKEN = "falseee";
    private static String LOG_TAG = ChatActivity.class.getSimpleName();
    private static final int RC_SETTINGS = 123;
    private Session session;
    private ImageButton btnVideoCallChatActivity;
    private FrameLayout subscriberContainer;
    private FrameLayout publisherContainer;
    private Publisher publisher;
    private Subscriber subscriber;
    private ImageButton closeVideoChat;
    private Stream stream;

    /*
    * Chat Messages Variables
    * */

    private static final int GALLERY_PICK = 1;
    private String recieverID;
    private DatabaseReference mDatabase;
    public String ph;
    private ImageButton sendMessage;
    private ImageButton selectImage;
    private EditText chatMessage;
    private RecyclerView userMessagesList;
    private SwipeRefreshLayout mRefreshLayout;
    private final List<Messages> messageList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private Toolbar mChatToolbar;
    private ImageButton select_image;
    private StorageReference mImageStorage;
    public Uri imageUri=null;
    public Uri imageBoldUri = null;
    private ImageButton btnDrawDoodleChat;

    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;

    private int itemPosition = 0;
    private String mLastKey = "";
    private String mPrevKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        startAgain = getIntent();

        subscriberContainer = (FrameLayout)findViewById(R.id.subscriberContainer);
        publisherContainer = (FrameLayout)findViewById(R.id.publishercontainer);
        btnVideoCallChatActivity = (ImageButton)findViewById(R.id.btnVideoCallChatActivity);
        closeVideoChat = (ImageButton)findViewById(R.id.closeVideoChat);
        closeVideoChat.setVisibility(View.INVISIBLE);
        btnDrawDoodleChat = (ImageButton)findViewById(R.id.btnDrawDoodleChat);

        mChatToolbar = (Toolbar)findViewById(R.id.chat_app_bar);
        select_image = (ImageButton)findViewById(R.id.select_image);
        mImageStorage = FirebaseStorage.getInstance().getReference();


        setSupportActionBar(mChatToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar,null);
        actionBar.setCustomView(action_bar_view);


        recieverID = getIntent().getExtras().get("visit_user_id").toString();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("App Data/"+FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()+"/Organization").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String orgz = dataSnapshot.getValue(String.class);


                mDatabase.child("Organizations").child(orgz).child("Users").child(recieverID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        String name = dataSnapshot.child("name").getValue(String.class);
                        String videoto = dataSnapshot.child("videoto").getValue(String.class);
                        String videoactive = dataSnapshot.child("videoactive").getValue(String.class);
                        checkStream++;
                        if(videoto == null && videoactive == null){
                            if(session == null){
//                                closeVideoChat.setVisibility(View.INVISIBLE);
//                                publisherContainer.setVisibility(View.GONE);
//                                subscriberContainer.setVisibility(View.GONE);
//                                subscriberContainer.setBackgroundColor(Color.TRANSPARENT);
//                                Toast.makeText(ChatActivity.this,"Video disconnected",Toast.LENGTH_SHORT).show();
//                                closeVideoChat.setVisibility(View.INVISIBLE);
//                                btnVideoCallChatActivity.setVisibility(View.VISIBLE);
//                                finish();
//                                startActivity(startAgain);
                                closeVideoChat.setVisibility(View.INVISIBLE);
                                //subscriberContainer.setBackgroundColor(Color.TRANSPARENT);
                            }
                            if(session!=null){
                                session.disconnect();
                                closeVideoChat.setVisibility(View.INVISIBLE);
                                subscriberContainer.setBackgroundColor(Color.TRANSPARENT);
                                closeVideoChat.setVisibility(View.INVISIBLE);
                                btnVideoCallChatActivity.setVisibility(View.VISIBLE);
                                finish();
                                startActivity(startAgain);
                            }
                        }
                        else if(videoto !=null && videoactive !=null){
                            if(videoto.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && videoactive.equals("true")){

                                if(!((Activity)ChatActivity.this).isFinishing())
                                {
                                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                                    final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                    r.play();

                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ChatActivity.this);
                                    builder1.setMessage(name+" wants to connect on video chat");
                                    builder1.setCancelable(true);

                                    builder1.setPositiveButton(
                                            "Accept",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    r.stop();
                                                    String apikey =  dataSnapshot.child("video").child("apikey").getValue(String.class);
                                                    String sessionid = dataSnapshot.child("video").child("sessionid").getValue(String.class);
                                                    String token = dataSnapshot.child("video").child("token").getValue(String.class);

                                                    API_KEY = apikey;
                                                    SESSION_ID = sessionid;
                                                    TOKEN = token;
                                                    requestPermissions();
                                                    closeVideoChat.setVisibility(View.VISIBLE);
                                                    btnVideoCallChatActivity.setVisibility(View.INVISIBLE);
                                                    subscriberContainer.setBackgroundColor(Color.WHITE);
                                                }
                                            });

                                    builder1.setNegativeButton(
                                            "Decline",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    r.stop();
                                                    dialog.cancel();
                                                }
                                            });

                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mDatabase.child("Organizations").child(orgz).child("Users").child(recieverID+"/name").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String val;
                        val = dataSnapshot.getValue(String.class);
                        TextView name = (TextView)findViewById(R.id.uNameChat);
                        name.setText(val);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mDatabase.child("Organizations").child(orgz).child("Users").child(recieverID+"/role").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String val;
                        val = dataSnapshot.getValue(String.class);
                        TextView role = (TextView)findViewById(R.id.uRoleChat);
                        role.setText(val);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                mDatabase.child("Organizations").child(orgz).child("Users").child(recieverID+"/phone").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ph = dataSnapshot.getValue(String.class);
                        loadImageData(ph);
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
        messageAdapter = new MessageAdapter(messageList);

        sendMessage = (ImageButton)findViewById(R.id.btnSendMessage);
        selectImage = (ImageButton)findViewById(R.id.select_image);
        chatMessage = (EditText)findViewById(R.id.input_message);
        userMessagesList = (RecyclerView)findViewById(R.id.messages_list);
        mRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.message_swipe_layout);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setHasFixedSize(true);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);

        FetchMessages();

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessageMethod();

            }
        });
        //mRefreshLayout.setEnabled(false);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
                itemPosition = 0;
                FetchMoreMessages();
            }
        });
        select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),GALLERY_PICK);
            }
        });


        btnVideoCallChatActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscriberContainer.setBackgroundColor(Color.WHITE);
                btnVideoCallChatActivity.setVisibility(View.INVISIBLE);
                makeCall();
            }
        });
        closeVideoChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(ChatActivity.this);
                builder1.setMessage("Do you want to close video chat?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                endCall();
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

        btnDrawDoodleChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, doodleActivitySingle.class);
                intent.putExtra("recieverID",recieverID);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase.child("App Data/"+FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()+"/Organization").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String org = dataSnapshot.getValue(String.class);
                mDatabase.child("Organizations").child(org).child("Users").child(recieverID).child("Contacts").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            //Toast.makeText(ChatActivity.this,"You are allowed to chat",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(ChatActivity.this);
                            LayoutInflater inflater = LayoutInflater
                                    .from(ChatActivity.this);
                            final View mView = inflater.inflate(R.layout.chat_disable, null);
                            mBuilder.setView(mView);
                            final AlertDialog ald = mBuilder.create();

                            Button btnConfirm = mView.findViewById(R.id.btnConfirmDisableChat);
                            btnConfirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ald.dismiss();
                                    Intent intent2 = new Intent(ChatActivity.this, Main3Activity.class);
                                    intent2.putExtra("fid","contacts");
                                    startActivity(intent2);
                                }
                            });
                            ald.setCancelable(false);
                            ald.setCanceledOnTouchOutside(false);
                            ald.show();
                        }
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

    private void endCall() {
        session.disconnect();
        closeVideoChat.setVisibility(View.INVISIBLE);
        publisherContainer.removeAllViews();
        subscriberContainer.removeAllViews();
        subscriberContainer.setBackgroundColor(Color.TRANSPARENT);
        Toast.makeText(ChatActivity.this,"Video disconnected",Toast.LENGTH_SHORT).show();
        mDatabase.child("App Data/"+FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()+"/Organization").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String orgz = dataSnapshot.getValue(String.class);
                mDatabase.child("Organizations").child(orgz).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("video").child("apikey").removeValue();
                mDatabase.child("Organizations").child(orgz).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("video").child("sessionid").removeValue();
                mDatabase.child("Organizations").child(orgz).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("video").child("token").removeValue();
                mDatabase.child("Organizations").child(orgz).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("videoactive").removeValue();
                mDatabase.child("Organizations").child(orgz).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("videoto").removeValue();

                //important because when subsriber cancels the video it should remove the alert
                mDatabase.child("Organizations").child(orgz).child("Users").child(recieverID).child("video").child("apikey").removeValue();
                mDatabase.child("Organizations").child(orgz).child("Users").child(recieverID).child("video").child("sessionid").removeValue();
                mDatabase.child("Organizations").child(orgz).child("Users").child(recieverID).child("video").child("token").removeValue();
                mDatabase.child("Organizations").child(orgz).child("Users").child(recieverID).child("videoactive").removeValue();
                mDatabase.child("Organizations").child(orgz).child("Users").child(recieverID).child("videoto").removeValue();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        subscriber = null;
        publisher = null;
        finish();
        startActivity(startAgain);
        btnVideoCallChatActivity.setVisibility(View.VISIBLE);

    }

    private void makeCall() {
        fetchSessionConnectionData();
    }

    public void fetchSessionConnectionData() {
        RequestQueue reqQueue = Volley.newRequestQueue(this);
        reqQueue.add(new JsonObjectRequest(Request.Method.GET,
                "https://teamlyvideochatserver.herokuapp.com" + "/session",
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    API_KEY = response.getString("apiKey");
                    SESSION_ID = response.getString("sessionId");
                    TOKEN = response.getString("token");

                    Log.i(LOG_TAG, "API_KEY: " + API_KEY);
                    Log.i(LOG_TAG, "SESSION_ID: " + SESSION_ID);
                    Log.i(LOG_TAG, "TOKEN: " + TOKEN);

                    mDatabase.child("App Data/"+FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()+"/Organization").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String orgz = dataSnapshot.getValue(String.class);
                            mDatabase.child("Organizations").child(orgz).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("video").child("apikey").setValue(API_KEY);
                            mDatabase.child("Organizations").child(orgz).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("video").child("sessionid").setValue(SESSION_ID);
                            mDatabase.child("Organizations").child(orgz).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("video").child("token").setValue(TOKEN);
                            mDatabase.child("Organizations").child(orgz).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("videoto").setValue(recieverID);
                            mDatabase.child("Organizations").child(orgz).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("videoactive").setValue("true");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    requestPermissions();
                } catch (JSONException error) {
                    Log.e(LOG_TAG, "Web Service error: " + error.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "Web Service error: " + error.getMessage());
            }
        }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            imageUri = data.getData();

            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(this);
        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                imageBoldUri = result.getUri();

                final String current_user_ref = "Organizations/BUKC/Messages/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/"+recieverID;
                final String chat_user_ref = "Organizations/BUKC/Messages/"+ recieverID +"/"+FirebaseAuth.getInstance().getCurrentUser().getUid();

                DatabaseReference user_message_push = mDatabase.child("Organizations").child("BUKC").child("Messages").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(recieverID).push();

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
                            messageMap.put("from",FirebaseAuth.getInstance().getCurrentUser().getUid());

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

                    }
                });

            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }
    }

    private void FetchMoreMessages() {
        DatabaseReference messageRef = mDatabase.child("Organizations/BUKC").child("Messages").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(recieverID);
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

    private void loadImageData(String phone) {
        mDatabase.child("Image URIs").child(phone+"/Profile Image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String val = dataSnapshot.getValue(String.class);
                CircleImageView v1 = (CircleImageView)findViewById(R.id.uImageChat);
                Picasso.with(ChatActivity.this).load(val).resize(220,220).into(v1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void FetchMessages() {
        DatabaseReference messageRef = mDatabase.child("Organizations/BUKC").child("Messages").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(recieverID);
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

    private void SendMessageMethod() {
        String messageText = chatMessage.getText().toString();
        if(TextUtils.isEmpty(messageText)){
            Toast.makeText(ChatActivity.this,"Cannot send empty message",Toast.LENGTH_SHORT).show();
        }
        else{
            String message_sender_ref = "Organizations/BUKC/Messages/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/"+recieverID;
            String message_reciever_ref = "Organizations/BUKC/Messages/"+ recieverID +"/"+FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference user_message_key = mDatabase.child("Organizations").child("BUKC").child("Messages").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(recieverID).push();
            String message_push_id = user_message_key.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message",messageText);
            messageTextBody.put("seen",false);
            messageTextBody.put("type","text");
            messageTextBody.put("time", ServerValue.TIMESTAMP);
            messageTextBody.put("from",FirebaseAuth.getInstance().getCurrentUser().getUid());

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(message_sender_ref+"/"+message_push_id,messageTextBody);
            messageBodyDetails.put(message_reciever_ref+"/"+message_push_id,messageTextBody);
            chatMessage.setText("");

            mDatabase.child("Organizations/BUKC/Chat").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(recieverID).child("seen").setValue(true);
            mDatabase.child("Organizations/BUKC/Chat").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(recieverID).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mDatabase.child("Organizations/BUKC/Chat").child(recieverID).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("seen").setValue(false);
            mDatabase.child("Organizations/BUKC/Chat").child(recieverID).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("timestamp").setValue(ServerValue.TIMESTAMP);

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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }
    @AfterPermissionGranted(RC_SETTINGS)
    private void requestPermissions(){
        String[] permissions  = {Manifest.permission.INTERNET,Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO};
        if(EasyPermissions.hasPermissions(this,permissions)){
            if(!API_KEY.equals("") && !SESSION_ID.equals("") && !TOKEN.equals("")){
                session = new Session.Builder(ChatActivity.this, API_KEY, SESSION_ID).build();
                session.setSessionListener(ChatActivity.this);
                session.connect(TOKEN);
                closeVideoChat.setVisibility(View.VISIBLE);

            }

        }
        else{
            EasyPermissions.requestPermissions(this,"Teamly requires access to Camera and Microphone",RC_SETTINGS,permissions);
        }
    }

    @Override
    public void onConnected(Session session) {
        publisher = new Publisher.Builder(this).build();
        publisher.setPublisherListener(this);
        publisherContainer.addView(publisher.getView());
        session.publish(publisher);
    }

    @Override
    public void onDisconnected(Session session) {


    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        if(subscriber == null){
            subscriber = new Subscriber.Builder(this,stream).build();
            session.subscribe(subscriber);
            subscriberContainer.addView(subscriber.getView());
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        if(subscriber!=null){
            subscriber =null;
            publisher = null;
            publisherContainer.removeAllViews();
            subscriberContainer.removeAllViews();
        }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {

    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
