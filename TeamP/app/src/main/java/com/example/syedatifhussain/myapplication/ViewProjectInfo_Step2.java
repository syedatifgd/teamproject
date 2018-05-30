package com.example.syedatifhussain.myapplication;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * Created by SyedAtif on 5/29/2018.
 */

public class ViewProjectInfo_Step2 extends android.support.v4.app.Fragment {

    private String orgd;
    private String projectid;
    private DatabaseReference mDatabase;
    private DatabaseReference usersDatabase;
    private RecyclerView recyclerView;

    public ViewProjectInfo_Step2() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_view_project_info_team_lead_step_2, container, false);

        orgd =getActivity().getIntent().getExtras().getString("orgid");
        projectid = getActivity().getIntent().getExtras().getString("projectid");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerView = view.findViewById(R.id.recyclerViewMembersProjectInfo);
        usersDatabase = FirebaseDatabase.getInstance().getReference().child("Organizations").child(orgd).child("Projects").child(projectid).child("Members");

        final TextView txtProjectKey = view.findViewById(R.id.txtProjectInfoProjectKey);
        final TextView txtProjectUserID = view.findViewById(R.id.txtProjectInfoProjectUserID);
        final TextView txtProjectUserPass = view.findViewById(R.id.txtProjectInfoProjectUserPass);

        mDatabase.child("Organizations").child(orgd).child("Projects").child(projectid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                txtProjectKey.setText(dataSnapshot.child("code").getValue(String.class));
                txtProjectUserID.setText(dataSnapshot.child("uid").getValue(String.class));
                txtProjectUserPass.setText(dataSnapshot.child("upass").getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        LinearLayoutManager layoutManager2
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager2);
        recyclerView.scrollToPosition(0);

        final FirebaseRecyclerAdapter<users,ViewProjectInfo_Step2.MembersViewHolder> mRecyclerAdapter = new FirebaseRecyclerAdapter<users, ViewProjectInfo_Step2.MembersViewHolder>(
                users.class,
                R.layout.users_viewproject_info,
                ViewProjectInfo_Step2.MembersViewHolder.class,
                usersDatabase) {
            @Override
            protected void populateViewHolder(final ViewProjectInfo_Step2.MembersViewHolder viewHolder, final users model, final int position) {

                if(model.getRole().equals("End User")){
                    RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)viewHolder.mView.getLayoutParams();
                    param.height = 0;
                    param.width = 0;
                    //viewHolder.mView.setMinimumHeight(0);
                    viewHolder.mView.setVisibility(View.GONE);
                    return;

                }
                else {

                    mDatabase.child("Organizations").child(orgd).child("Projects").child(projectid).child("head").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String head = dataSnapshot.getValue(String.class);

                            if(head.equals(getRef(position).getKey())){
                                RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)viewHolder.mView.getLayoutParams();
                                param.height = 0;
                                param.width = 0;
                                //viewHolder.mView.setMinimumHeight(0);
                                viewHolder.mView.setVisibility(View.GONE);
                                return;
                            }
                            else {
                                viewHolder.setName(model.getName());
                                mDatabase.child("Organizations").child(orgd).child("Users").child(getRef(position).getKey()+"/image").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        String val = dataSnapshot.getValue(String.class);
                                        viewHolder.setImage(getActivity(),val);
                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                final String visit_user_id = getRef(position).getKey();

                                                CharSequence options[] = new CharSequence[]{
                                                        model.getName() + "'s Profile",
                                                        "Remove from member list"
                                                };

                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle("Choose action");

                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int position) {
                                                        if(position == 0){
                                                            Intent profileIntent = new Intent(getActivity(),ProfileActivity.class);
                                                            profileIntent.putExtra("visit_user_id",visit_user_id);
                                                            profileIntent.putExtra("orgglobalid",orgd);
                                                            startActivity(profileIntent);
                                                        }
                                                        if(position == 1){
                                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                                                            builder1.setMessage("Are you sure you want to remove this user?");
                                                            builder1.setCancelable(true);

                                                            builder1.setPositiveButton(
                                                                    "Yes",
                                                                    new DialogInterface.OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int id) {
                                                                            usersDatabase.child(visit_user_id).removeValue();
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
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });

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

            }
        };

        mRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mRecyclerAdapter.getItemCount();
                LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }
            }

        });
        recyclerView.setAdapter(mRecyclerAdapter);

        usersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return view;
    }
    public static class MembersViewHolder extends RecyclerView.ViewHolder{
        View mView;
        Boolean test = false;

        public MembersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String contact_Name){
            TextView mContactName = mView.findViewById(R.id.txtUserProfileName);
            mContactName.setText(contact_Name);
        }

        public void setImage(Context ctx, String image){
            ImageView img = mView.findViewById(R.id.uImageContacts2);
            Picasso.with(ctx).load(image).placeholder(R.drawable.baseline_person_white_24dp).resize(220,220).into(img);
        }

    }

}
