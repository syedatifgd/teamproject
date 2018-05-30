package com.example.syedatifhussain.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * Created by SyedAtif on 11/14/2017.
 */

public class ContactsFragment extends Fragment {
    private DatabaseReference mContactsDatabase;
    private RecyclerView mUsersList;

    private View rootView;
    private ProgressDialog pr;
    private boolean exist = false;
    private DatabaseReference db;
    private String orgglobalid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, final Bundle savedInstanceState) {
        rootView  = inflater.inflate(R.layout.contacts_fragment,container,false);
        mUsersList = rootView.findViewById(R.id.contacts_list);
        mUsersList.setHasFixedSize(true);
        db = FirebaseDatabase.getInstance().getReference();
        FloatingActionButton btnaddcontact = (FloatingActionButton)rootView.findViewById(R.id.btnAddContact);

        btnaddcontact.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getContext());
                final View mView = getLayoutInflater(savedInstanceState).inflate(R.layout.dialog_addcontact, null);
                final EditText mPhone = mView.findViewById(R.id.txtPhone);
                Button mSubmitContact= mView.findViewById(R.id.btnSubmit);
                final String abcd = mPhone.getText().toString();
                mSubmitContact.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                       if(!mPhone.getText().toString().isEmpty()){
                           if(mPhone.getText().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())){
                               Toast.makeText(getActivity(), "Cannot add self as contact",Toast.LENGTH_SHORT).show();
                           }
                           else{
                               checkPh(mPhone.getText().toString());
                               return;
                           }
                        }
                        else if(mPhone.getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), R.string.failed_add_contact_msg,Toast.LENGTH_SHORT).show();
                        }
                        else {
                           Toast.makeText(getActivity(), "Invalid contact info or the contact is not on Teamly",Toast.LENGTH_SHORT).show();
                       }
                    }
                });
                mBuilder.setView(mView);
                AlertDialog ald = mBuilder.create();
                ald.show();

            }
        });
        return rootView;
    }

    public ContactsFragment(){

    }

    @Override
    public void onStart() {
        super.onStart();

        DatabaseReference dbs = FirebaseDatabase.getInstance().getReference();
        dbs.child("App Data").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot val = dataSnapshot.child("Organization");
                orgglobalid = val.getValue().toString();
                mContactsDatabase = FirebaseDatabase.getInstance().getReference().child("Organizations").child(orgglobalid).child("Users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/Contacts");

                mContactsDatabase.keepSynced(true);
                mUsersList.setLayoutManager(new LinearLayoutManager(getContext()));
                mUsersList.scrollToPosition(0);


                final FirebaseRecyclerAdapter<users,ContactsViewHolder> mRecyclerAdapter = new FirebaseRecyclerAdapter<users, ContactsViewHolder>(
                        users.class,
                        R.layout.users_contacts_layout,
                        ContactsViewHolder.class,
                        mContactsDatabase) {

                    @Override
                    protected void populateViewHolder(ContactsViewHolder viewHolder, final users model, final int position) {

                        viewHolder.setName(model.getName());
                        viewHolder.setRole(model.getRole());
                        viewHolder.setImage(getContext(),model.getImage());
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final String visit_user_id = getRef(position).getKey();


                                CharSequence options[] = new CharSequence[]{
                                        model.getName() + "'s Profile",
                                        "Send a Message"
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Choose action");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int position) {
                                        if(position == 0){
                                            Intent profileIntent = new Intent(getActivity(),ProfileActivity.class);
                                            profileIntent.putExtra("visit_user_id",visit_user_id);
                                            profileIntent.putExtra("orgglobalid",orgglobalid);
                                            startActivity(profileIntent);
                                        }
                                        if(position == 1){
                                            Intent chatIntent = new Intent(getActivity(),ChatActivity.class);
                                            chatIntent.putExtra("visit_user_id",visit_user_id);
                                            startActivity(chatIntent);
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }
                };

                mRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        super.onItemRangeInserted(positionStart, itemCount);
                        int friendlyMessageCount = mRecyclerAdapter.getItemCount();
                        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
                        int lastVisiblePosition =
                                mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                        // If the recycler view is initially being loaded or the
                        // user is at the bottom of the list, scroll to the bottom
                        // of the list to show the newly added message.
                        if (lastVisiblePosition == -1 ||
                                (positionStart >= (friendlyMessageCount - 1) &&
                                        lastVisiblePosition == (positionStart - 1))) {
                            mUsersList.scrollToPosition(positionStart);
                        }
                    }

                });

                mUsersList.setAdapter(mRecyclerAdapter);
                mContactsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

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


    public static class ContactsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        Boolean test = false;

        public ContactsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String contact_Name){
            TextView mContactName = mView.findViewById(R.id.txtUserProfileName);
            mContactName.setText(contact_Name);
        }

        public void setRole(String role){
            TextView rrole = mView.findViewById(R.id.txtUserProfileRole);
            rrole.setText(role);
        }

        public void setImage(Context ctx, String image){
            ImageView img = mView.findViewById(R.id.uImageContacts2);
            Picasso.with(ctx).load(image).placeholder(R.drawable.baseline_person_white_24dp).resize(220,220).into(img);
        }
        public boolean done(){
            test = true;
            return test;
        }


    }

    public void checkPh(final String phoneNum){
        final DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("App Data/"+FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()+"/Organization").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //HashMap<String, Object> hashmap = (HashMap) dataSnapshot.getValue();
                //String val = (String) hashmap.get(String.class);
                final String val = dataSnapshot.getValue(String.class);
                if(val !=null){
                    final Query filterQuery;
                    filterQuery = mDatabase.child("Organizations/"+val+"/Users").orderByChild("phone").equalTo(phoneNum);
                    //final Query filterQuery = mDatabase.orderByChild('_searchLastName').startAt(queryText).endAt(queryText+"\uf8ff").once("value");
                    filterQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                    //mDatabase.child("Organizations").child(val).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Contacts").child("Phone").setValue(phoneNum);
                                    doSave(phoneNum,val);
                                }
                            }
                            else {
                                Toast.makeText(getActivity(),"The contact is not on Teamly.",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else{

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void doSave(final String number,final String orgID){
        final DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("App Data/"+number+"/User ID").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String val2 = dataSnapshot.getValue(String.class);

                mDatabase.child("Organizations").child(orgID).child("Users").child(val2+"/name").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String name = dataSnapshot.getValue(String.class);

                        mDatabase.child("App Data/"+number+"/Role").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final String val3 = dataSnapshot.getValue(String.class);

                                mDatabase.child("Image URIs/"+number+"/Profile Image").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String val4 = dataSnapshot.getValue(String.class);
                                        mDatabase.child("Organizations").child(orgID).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Contacts").child(val2).child("phone").setValue(number);
                                        mDatabase.child("Organizations").child(orgID).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Contacts").child(val2).child("image").setValue(val4);
                                        mDatabase.child("Organizations").child(orgID).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Contacts").child(val2).child("role").setValue(val3);
                                        mDatabase.child("Organizations").child(orgID).child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Contacts").child(val2).child("name").setValue(name);
                                        Toast.makeText(getActivity(),"Contact added successfully!",Toast.LENGTH_SHORT).show();
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


}
