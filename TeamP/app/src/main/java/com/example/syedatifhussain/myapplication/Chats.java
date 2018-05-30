package com.example.syedatifhussain.myapplication;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;


public class Chats extends Fragment {
    Context c;

    private DatabaseReference db;
    private RecyclerView mConvList;

    private DatabaseReference mConvDatabase;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;


    public Chats() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_chats2, container, false);

        mConvList = (RecyclerView) mMainView.findViewById(R.id.conv_list);
        c = mConvList.getContext();
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Organizations/BUKC/Chat").child(mCurrent_user_id);

        mConvDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Organizations/BUKC/Users");
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("Organizations/BUKC/Messages/").child(mCurrent_user_id);
        mUsersDatabase.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mConvList.setHasFixedSize(true);
        mConvList.setLayoutManager(linearLayoutManager);


        // Inflate the layout for this fragment
        return mMainView;
    }
    @Override
    public void onStart() {
        super.onStart();

        Query conversationQuery = mConvDatabase.orderByChild("time");

        FirebaseRecyclerAdapter<Conv, ConvViewHolder> firebaseConvAdapter = new FirebaseRecyclerAdapter<Conv, ConvViewHolder>(
                Conv.class,
                R.layout.users_contacts_layout,
                ConvViewHolder.class,
                conversationQuery
        ) {
            @Override
            protected void populateViewHolder(final ConvViewHolder convViewHolder, final Conv conv, int i) {
                final String list_user_id = getRef(i).getKey();

                Query lastMessageQuery = mMessageDatabase.child(list_user_id).limitToLast(1);

                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                        String data = dataSnapshot.child("message").getValue().toString();
                        String type = dataSnapshot.child("type").getValue().toString();
                        String from = dataSnapshot.child("from").getValue().toString();
                        long timestamp = dataSnapshot.child("time").getValue(Long.class);
                        convViewHolder.setTimestamp(timestamp,c,from);

                        if(type.equals("image")) {
                            convViewHolder.setMessage("Image", conv.isSeen());
                        }
                        else {
                            convViewHolder.setMessage(data, conv.isSeen());
                        }

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



                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(list_user_id !=null){
                            final String userName = dataSnapshot.child("name").getValue().toString();
                            String userThumb = dataSnapshot.child("image").getValue().toString();
                            Context cc = getActivity();

                            convViewHolder.setName(userName);
                            convViewHolder.setUserImage(userThumb, cc);

                            convViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                    chatIntent.putExtra("visit_user_id", list_user_id);
                                    startActivity(chatIntent);

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mConvList.setAdapter(firebaseConvAdapter);

    }

    public static class ConvViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ConvViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setMessage(String message, boolean isSeen){

            TextView userStatusView = (TextView) mView.findViewById(R.id.txtUserProfileRole);
            userStatusView.setText(message);

            if(!isSeen){
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.BOLD);
            } else {
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);
            }

        }

        public void setName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.txtUserProfileName);
            userNameView.setText(name);

        }

        public void setTimestamp (long time,Context ctx,String from){
            TextView messageTime = mView.findViewById(R.id.txtShowMessageTimeChatLayout);
            //SimpleDateFormat sfd = new SimpleDateFormat("HH:mm");
            GetTimeAgo getTimeAgo = new GetTimeAgo();
            String lastSeenTime = getTimeAgo.getTimeAgo(time, ctx);
            messageTime.setText(lastSeenTime);
            messageTime.setBackgroundResource(R.drawable.ownmessage);
            messageTime.setTextColor(Color.WHITE);

        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.uImageContacts2);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.baseline_person_white_24dp).into(userImageView);

        }
    }

}
