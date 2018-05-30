package com.example.syedatifhussain.myapplication;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.maps.model.Circle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    private List<Messages> userMessagesList;
    private FirebaseAuth mauth;

    public MessageAdapter(List<Messages> userMessagesList){
        this.userMessagesList = userMessagesList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_layout_of_user,parent,false);
        mauth =FirebaseAuth.getInstance();

        return new MessageViewHolder(V);
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {
        String currentUid = mauth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(position);

        String fromUserId = messages.getFrom();
        String message_type = messages.getType();

        DatabaseReference mUserDatabase;
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Organizations/BUKC/Users").child(fromUserId);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                Picasso.with(holder.userProfileImage.getContext()).load(image).placeholder(R.drawable.baseline_person_white_24dp).into(holder.userProfileImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(message_type.equals("text")){
            if(fromUserId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                holder.message_text_owntext.setVisibility(View.VISIBLE);
                holder.ownTime.setVisibility(View.VISIBLE);
                holder.messageText.setVisibility(View.INVISIBLE);
                holder.userProfileImage.setVisibility(View.INVISIBLE);
                holder.time.setVisibility(View.INVISIBLE);
                holder.messageImage.setVisibility(View.GONE);
                holder.message_own_image.setVisibility(View.GONE);

                holder.message_text_owntext.setText(messages.getMessage());
                holder.message_text_owntext.setBackgroundResource(R.drawable.ownmessage);
                holder.message_text_owntext.setTextColor(Color.WHITE);
                long timestamp = messages.getTime();
                SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yy hh:mm a");

                holder.ownTime.setText(sfd.format(new Date(timestamp)).toString());
            }
            else{
                holder.userProfileImage.setVisibility(View.VISIBLE);
                holder.ownTime.setVisibility(View.INVISIBLE);
                holder.messageText.setVisibility(View.VISIBLE);
                holder.message_text_owntext.setVisibility(View.INVISIBLE);
                holder.messageText.setBackgroundResource(R.drawable.othermessage);
                holder.messageText.setTextColor(Color.WHITE);
                holder.time.setVisibility(View.VISIBLE);
                holder.messageText.setText(messages.getMessage());

                long timestamp = messages.getTime();
                SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yy hh:mm a");

                holder.time.setText(sfd.format(new Date(timestamp)).toString());

            }
        }
        else {
            if(fromUserId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                holder.ownTime.setVisibility(View.VISIBLE);
                holder.messageText.setVisibility(View.INVISIBLE);
                holder.message_own_image.setVisibility(View.VISIBLE);
                holder.messageImage.setVisibility(View.GONE);
                holder.userProfileImage.setVisibility(View.INVISIBLE);
                holder.time.setVisibility(View.INVISIBLE);
                holder.message_text_owntext.setVisibility(View.INVISIBLE);
                Picasso.with(holder.messageText.getContext()).load(messages.getMessage()).placeholder(R.drawable.baseline_image_black_36dp).into(holder.message_own_image);
                long timestamp = messages.getTime();
                holder.message_own_image.setBackgroundResource(R.drawable.ownmessage);
                SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yy hh:mm a");

                holder.ownTime.setText(sfd.format(new Date(timestamp)).toString());
            }
            else{
                holder.userProfileImage.setVisibility(View.VISIBLE);
                holder.message_own_image.setVisibility(View.GONE);
                holder.messageImage.setVisibility(View.VISIBLE);
                Picasso.with(holder.messageText.getContext()).load(messages.getMessage()).placeholder(R.drawable.baseline_image_black_36dp).into(holder.messageImage);
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
        public ImageView message_own_image;
        public TextView time;

        public MessageViewHolder(final View view){
            super(view);
            messageText = view.findViewById(R.id.message_text);
            message_text_owntext = view.findViewById(R.id.message_text_owntext);
            userProfileImage = view.findViewById(R.id.messages_profile_image);
            messageImage = view.findViewById(R.id.message_image_layout);
            message_own_image = view.findViewById(R.id.message_own_image);
            //messageName = view.findViewById(R.id.name_text_layout);
            time = view.findViewById(R.id.time_text_layout);
            ownTime = view.findViewById(R.id.time_own_text);

            message_own_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getContext());
                    LayoutInflater inflater = LayoutInflater
                            .from(view.getContext());
                    final View mView = inflater.inflate(R.layout.displaymessageimagefull, null);
                    mBuilder.setView(mView);
                    PhotoView imgFull = mView.findViewById(R.id.imgFull);
                    imgFull.setImageDrawable(message_own_image.getDrawable());
                    AlertDialog ald = mBuilder.create();
                    ald.show();
                }
            });

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
