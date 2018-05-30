package com.example.syedatifhussain.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by SyedAtif on 5/23/2018.
 */

public class MessageAdapterMeetingPanel extends RecyclerView.Adapter<MessageAdapterMeetingPanel.MessageViewHolder> {

    public MessageAdapterMeetingPanel() {
    }
    private List<Messages> userMessagesList;
    private FirebaseAuth mauth;
    private DatabaseReference db;
    private String userr;
    private Context context;

    public MessageAdapterMeetingPanel(List<Messages> userMessagesList,Context context){
        this.userMessagesList = userMessagesList;
        this.context = context;
    }

    @Override
    public MessageAdapterMeetingPanel.MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_layout_meeting_panel,parent,false);
        mauth =FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
        getUU();


        return new MessageAdapterMeetingPanel.MessageViewHolder(V);
    }

    private void getUU() {
        final Messages messages = userMessagesList.get(0);
        db.child("Organizations").child("BUKC").child("Projects").child(messages.getPid()).child("uid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String val = dataSnapshot.getValue(String.class);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("uid", val);
                editor.commit();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBindViewHolder(final MessageAdapterMeetingPanel.MessageViewHolder holder, int position) {
        final Messages messages = userMessagesList.get(position);
        if(mauth.getCurrentUser()!=null){
            userr = mauth.getCurrentUser().getUid();
        }
        else {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            final String uid = preferences.getString("uid", "");
            userr = uid;

        }

        final String fromUserId = messages.getFrom();
        final String message_type = messages.getType();
        DatabaseReference mUserDatabase;
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Organizations/BUKC/Users").child(fromUserId);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                holder.messageName.setText(name);
                String image = dataSnapshot.child("image").getValue().toString();
                Picasso.with(holder.userProfileImage.getContext()).load(image).placeholder(R.drawable.baseline_person_white_24dp).into(holder.userProfileImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(message_type.equals("text")){
            if(fromUserId.equals(userr)){

                holder.message_text_owntext.setVisibility(View.VISIBLE);
                holder.messageText.setVisibility(View.INVISIBLE);
                holder.messageName.setVisibility(View.INVISIBLE);
                holder.userProfileImage.setVisibility(View.INVISIBLE);
                holder.ownTime.setVisibility(View.VISIBLE);
                holder.time.setVisibility(View.INVISIBLE);
                holder.messageImage.setVisibility(View.GONE);
                holder.message_text_owntext.setText(messages.getMessage());
                holder.message_text_owntext.setBackgroundResource(R.drawable.ownmessage);
                holder.message_text_owntext.setTextColor(Color.WHITE);
                long timestamp = messages.getTime();
                SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yy hh:mm a");
                holder.ownTime.setText(sfd.format(new Date(timestamp)).toString());
            }
            else{
                holder.messageImage.setVisibility(View.GONE);
                holder.messageName.setVisibility(View.VISIBLE);
                holder.userProfileImage.setVisibility(View.VISIBLE);
                holder.ownTime.setVisibility(View.INVISIBLE);
                holder.messageText.setVisibility(View.VISIBLE);
                holder.message_text_owntext.setVisibility(View.INVISIBLE);
                holder.messageText.setBackgroundColor(Color.WHITE);
                holder.messageText.setTextColor(Color.BLACK);
                holder.time.setVisibility(View.VISIBLE);
                holder.messageText.setText(messages.getMessage());
                long timestamp = messages.getTime();
                SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yy hh:mm a");
                holder.time.setText(sfd.format(new Date(timestamp)).toString());
            }

        }
        else {
            if(fromUserId.equals(userr)){
                holder.messageName.setVisibility(View.INVISIBLE);
                holder.messageText.setVisibility(View.INVISIBLE);
                holder.messageImage.setVisibility(View.VISIBLE);
                holder.userProfileImage.setVisibility(View.INVISIBLE);
                holder.time.setVisibility(View.INVISIBLE);
                holder.ownTime.setVisibility(View.VISIBLE);
                holder.message_text_owntext.setVisibility(View.INVISIBLE);
                Picasso.with(holder.messageImage.getContext()).load(messages.getMessage()).placeholder(R.drawable.baseline_image_black_36dp).into(holder.messageImage);
                long timestamp = messages.getTime();
                SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yy hh:mm a");
                holder.ownTime.setText(sfd.format(new Date(timestamp)).toString());
            }
            else{
                holder.messageImage.setVisibility(View.VISIBLE);
                Picasso.with(holder.messageImage.getContext()).load(messages.getMessage()).placeholder(R.drawable.baseline_image_black_36dp).into(holder.messageImage);
                holder.ownTime.setVisibility(View.INVISIBLE);
                holder.messageText.setVisibility(View.INVISIBLE);
                holder.message_text_owntext.setVisibility(View.INVISIBLE);
                holder.time.setVisibility(View.VISIBLE);
                long timestamp = messages.getTime();
                SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yy hh:mm a");
                holder.time.setText(sfd.format(new Date(timestamp)).toString());
            }
        }
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView messageText;
        public TextView message_text_owntext;
        public TextView ownTime;
        public TextView messageName;
        public CircleImageView userProfileImage;
        public ImageView messageImage;
        public TextView time;

        public MessageViewHolder(final View view){
            super(view);
            messageText = view.findViewById(R.id.message_text_layout);
            message_text_owntext = view.findViewById(R.id.message_text_layout_own_text);
            userProfileImage = view.findViewById(R.id.message_profile_layout);
            messageImage = view.findViewById(R.id.message_image_layout_meeting_panel);
            messageName = view.findViewById(R.id.name_text_layout);
            time = view.findViewById(R.id.time_text_layout);
            ownTime = view.findViewById(R.id.time_own_meeting_panel);

            messageImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getContext());
                    LayoutInflater inflater = LayoutInflater
                            .from(view.getContext());
                    final View mView = inflater.inflate(R.layout.displaymessageimagefull, null);
                    mBuilder.setView(mView);
                    PhotoView imgFull = mView.findViewById(R.id.imgFull);
                    imgFull.setImageDrawable(messageImage.getDrawable());
                    AlertDialog ald = mBuilder.create();
                    ald.show();
                }
            });

        }
    }
}
