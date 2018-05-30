package com.example.syedatifhussain.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CreateProject extends AppCompatActivity {

    public ImageButton addMembers;
    private DatabaseReference mContactsDatabase;
    private DatabaseReference addProjectDB;
    private RecyclerView mUsersList;
    private List<String> list = new ArrayList<String>();
    private Button createProjet;
    private EditText pname;
    private EditText pdesc;
    private EditText pcode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        addProjectDB = FirebaseDatabase.getInstance().getReference();

        mContactsDatabase = FirebaseDatabase.getInstance().getReference().child("Organizations").child("BUKC").child("Users");
        //mContactsDatabase = FirebaseDatabase.getInstance().getReference().child("Organizations").child("BUKC").child("Users").child("07cxsh312czsDsz");

        mContactsDatabase.keepSynced(true);

        addMembers = (ImageButton)findViewById(R.id.btnAddMembers);
        createProjet = (Button)findViewById(R.id.btnCreateProject);
        pname = (EditText)findViewById(R.id.editTextProjectName);
        pdesc = (EditText)findViewById(R.id.editTextProjectDescription);
        pcode = (EditText)findViewById(R.id.editTextProjectCode);
        list.add(FirebaseAuth.getInstance().getCurrentUser().getUid());

        pcode.setEnabled(false);


        final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(5);

        for(int i=0;i<5;++i){
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        }
        pcode.setText(sb.toString());



        addMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getContext());
                LayoutInflater inflater = LayoutInflater
                        .from(getApplicationContext());
                final View mView = inflater.inflate(R.layout.dialog_addmembers, null);
                mBuilder.setView(mView);
                mUsersList = mView.findViewById(R.id.users_list);
                mUsersList.setHasFixedSize(true);
                mUsersList.setLayoutManager(new LinearLayoutManager(CreateProject.this));

                final FirebaseRecyclerAdapter<users,CreateProject.ContactsViewHolder> mRecyclerAdapter = new FirebaseRecyclerAdapter<users, ContactsViewHolder>(
                        users.class,
                        R.layout.users_contacts_layout,
                        ContactsViewHolder.class,
                        mContactsDatabase) {
                    @Override
                    protected void populateViewHolder(final ContactsViewHolder viewHolder, final users model, final int position) {
                        addProjectDB.child("Organizations/BUKC/Users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/phone").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String val  = dataSnapshot.getValue(String.class);

                                if(model.getPhone() !=null && model.getName()!=null && model.getRole()!=null){
                                    if(model.getPhone().equals(val)){

                                        RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)viewHolder.mView.getLayoutParams();
                                        param.height = 0;
                                        param.width = 0;
                                        //viewHolder.mView.setMinimumHeight(0);
                                        viewHolder.mView.setVisibility(View.GONE);
                                        return;
                                    }
                                    if(model.getName().equals("Client")){
                                        Toast.makeText(CreateProject.this,"Client",Toast.LENGTH_SHORT).show();
                                        RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)viewHolder.mView.getLayoutParams();
                                        param.height = 0;
                                        param.width = 0;
                                        //viewHolder.mView.setMinimumHeight(0);
                                        viewHolder.mView.setVisibility(View.GONE);
                                        return;

                                    }
                                    else{
                                        if(model.getName().equals("Client")){
                                            Toast.makeText(CreateProject.this,"Client",Toast.LENGTH_SHORT).show();
                                            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)viewHolder.mView.getLayoutParams();
                                            param.height = 0;
                                            param.width = 0;
                                            //viewHolder.mView.setMinimumHeight(0);
                                            viewHolder.mView.setVisibility(View.GONE);
                                            return;

                                        }
                                        else {
                                            viewHolder.setName(model.getName());
                                            viewHolder.setRole(model.getRole());
                                            viewHolder.setImage(CreateProject.this,model.getImage());
                                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    final String visit_user_id = getRef(position).getKey();

                                                    CharSequence options[] = new CharSequence[]{
                                                            model.getName() + "'s Profile",
                                                            "Add to project"
                                                    };

                                                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateProject.this);
                                                    builder.setTitle("Choose action");

                                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int position) {
                                                            if(position == 0){
                                                                Intent profileIntent = new Intent(CreateProject.this,ProfileActivity.class);
                                                                profileIntent.putExtra("visit_user_id",visit_user_id);
                                                                profileIntent.putExtra("orgglobalid","BUKC");

                                                                startActivity(profileIntent);
                                                            }
                                                            if(position == 1){
                                                                if(list.size()<=5){
                                                                    list.add(visit_user_id);
                                                                    Toast.makeText(CreateProject.this, "Member added.", Toast.LENGTH_SHORT).show();
                                                                    RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)viewHolder.mView.getLayoutParams();
                                                                    param.height = 0;
                                                                    param.width = 0;

                                                                }
                                                                else {
                                                                    Toast.makeText(CreateProject.this, "Cannot add more than 5 members.", Toast.LENGTH_SHORT).show();
                                                                }

                                                            }
                                                        }
                                                    });
                                                    builder.show();
                                                }
                                            });
                                        }

                                    }
                                }
                                else {
                                    RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)viewHolder.mView.getLayoutParams();
                                    param.height = 0;
                                    param.width = 0;
                                    //viewHolder.mView.setMinimumHeight(0);
                                    viewHolder.mView.setVisibility(View.GONE);
                                    return;

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                };
                mUsersList.scrollToPosition(0);

                mRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        super.onItemRangeInserted(positionStart, itemCount);
                        int friendlyMessageCount = mRecyclerAdapter.getItemCount();
                        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(CreateProject.this);
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




                AlertDialog ald = mBuilder.create();
                ald.show();
            }
        });

       createProjet.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               final Query filterQuery;
               if(pname.getText().toString().isEmpty() || pdesc.getText().toString().isEmpty() || pcode.getText().toString().isEmpty()){
                   Toast.makeText(CreateProject.this, "Project name,description or code not valid", Toast.LENGTH_SHORT).show();
               }
               else{
                   filterQuery = addProjectDB.child("Organizations/BUKC/Projects");
                   filterQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(DataSnapshot dataSnapshot) {

                           if (dataSnapshot.child(pname.getText().toString()).exists()) {
                               Toast.makeText(CreateProject.this,"Project already exists! Please rename or recreate the project.",Toast.LENGTH_SHORT).show();
                           }
                           else {
                               if(list.size() == 0){
                                   Toast.makeText(CreateProject.this, "Invalid number of project members", Toast.LENGTH_SHORT).show();
                               }
                               else{
                                   for (int i=0;i<list.size();i++){
                                       final String currentuid=list.get(i);

                                       addProjectDB.child("Organizations/BUKC/Users/"+list.get(i)+"/name").addValueEventListener(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(DataSnapshot dataSnapshot) {
                                               String val  = dataSnapshot.getValue(String.class);
                                               addProjectDB.child("Organizations").child("BUKC").child("Projects").child(pname.getText().toString()).child("Members").child(currentuid).child("name").setValue(val);
                                           }

                                           @Override
                                           public void onCancelled(DatabaseError databaseError) {

                                           }
                                       });
                                       addProjectDB.child("Organizations/BUKC/Users/"+list.get(i)+"/role").addValueEventListener(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(DataSnapshot dataSnapshot) {
                                               String val  = dataSnapshot.getValue(String.class);
                                               addProjectDB.child("Organizations").child("BUKC").child("Projects").child(pname.getText().toString()).child("Members").child(currentuid).child("role").setValue(val);

                                           }

                                           @Override
                                           public void onCancelled(DatabaseError databaseError) {

                                           }
                                       });
                                   }
                                   addProjectDB.child("Organizations").child("BUKC").child("Projects").child(pname.getText().toString()).child("code").setValue(pcode.getText().toString());
                                   addProjectDB.child("Organizations").child("BUKC").child("Projects").child(pname.getText().toString()).child("description").setValue(pdesc.getText().toString());

                                   final String ALLOWED_CHARACTERS ="012389qwertyuidfghjzxvbnm";
                                   final Random random=new Random();
                                   final StringBuilder sb=new StringBuilder(4);

                                   for(int i=0;i<4;++i){
                                       sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
                                   }
                                   String uidproject = sb.toString();

                                   final String ALLOWED_CHARACTERS2 ="qwertyuidfghjzxvbnmxyzs";
                                   final Random random2=new Random();
                                   final StringBuilder sb2=new StringBuilder(4);

                                   for(int i=0;i<4;++i){
                                       sb2.append(ALLOWED_CHARACTERS2.charAt(random.nextInt(ALLOWED_CHARACTERS2.length())));
                                   }
                                   String passproject = sb2.toString();


                                   addProjectDB.child("Organizations").child("BUKC").child("Projects").child(pname.getText().toString()).child("uid").setValue(uidproject);
                                   addProjectDB.child("Organizations").child("BUKC").child("Projects").child(pname.getText().toString()).child("upass").setValue(passproject);
                                   addProjectDB.child("Organizations").child("BUKC").child("Projects").child(pname.getText().toString()).child("status").setValue("false");
                                   addProjectDB.child("Organizations").child("BUKC").child("Projects").child(pname.getText().toString()).child("head").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());


                                   Toast.makeText(CreateProject.this, "Project Created", Toast.LENGTH_SHORT).show();
                                   Intent intent = new Intent(CreateProject.this, ProjectActivity.class);
                                   intent.putExtra("userid",uidproject);
                                   intent.putExtra("userpass",passproject);
                                   intent.putExtra("pname",pname.getText().toString());
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

           }
       });
    }

    public CreateProject(){

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
            Picasso.with(ctx).load(image).resize(220,220).into(img);
        }
        public boolean done(){
            test = true;
            return test;
        }
    }
}
