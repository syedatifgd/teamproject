package com.example.syedatifhussain.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

public class ProjectsFragment extends Fragment {
    private DatabaseReference db;
    private RecyclerView projectscontainer;
    private ProgressDialog pr;
    private DatabaseReference mProjectsDatabase;
    private DatabaseReference mJoinedProjectsDatabase;
    private Query filterQuery;
    private Button joinedProjects;
    private Button createdProjects;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view  = inflater.inflate(R.layout.projects_fragment,container,false);

        db = FirebaseDatabase.getInstance().getReference();
        mJoinedProjectsDatabase = FirebaseDatabase.getInstance().getReference();
        joinedProjects = view.findViewById(R.id.btnJoinedProjects);
        createdProjects = view.findViewById(R.id.btnCreatedProjects);
        projectscontainer = view.findViewById(R.id.projectscontainer);
        projectscontainer.setHasFixedSize(true);


        mProjectsDatabase = FirebaseDatabase.getInstance().getReference();

        mProjectsDatabase.keepSynced(true);
        projectscontainer.setLayoutManager(new LinearLayoutManager(getContext()));

        createdProjects.setEnabled(false);
        joinedProjects.setEnabled(true);

        joinedProjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createdProjects.setEnabled(true);
                joinedProjects.setEnabled(false);
                showJoinedProjects();
            }
        });
        createdProjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createdProjects.setEnabled(false);
                joinedProjects.setEnabled(true);
                showCreatedProjects();
            }
        });

        return view;
    }

    private void showJoinedProjects() {
        DatabaseReference dbs = FirebaseDatabase.getInstance().getReference();
        dbs.child("App Data").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot val = dataSnapshot.child("Organization");
                String orgglobalid = val.getValue().toString();
                mJoinedProjectsDatabase = FirebaseDatabase.getInstance().getReference().child("Organizations").child(orgglobalid).child("Users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/User Projects");

                mJoinedProjectsDatabase.keepSynced(true);
                projectscontainer.setLayoutManager(new LinearLayoutManager(getContext()));
                projectscontainer.scrollToPosition(0);


                final FirebaseRecyclerAdapter<projects,ProjectsFragment.ProjectsViewHolder> mRecyclerAdapter = new FirebaseRecyclerAdapter<projects, ProjectsFragment.ProjectsViewHolder>(
                        projects.class,
                        R.layout.projects_layout_show,
                        ProjectsFragment.ProjectsViewHolder.class,
                        mJoinedProjectsDatabase) {

                    @Override
                    protected void populateViewHolder(ProjectsFragment.ProjectsViewHolder viewHolder, final projects model, final int position) {

                        viewHolder.setDescription(model.getDescription());
                        viewHolder.setProjectName(getRef(position).getKey());
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final String projectid = getRef(position).getKey();

                                mProjectsDatabase.child("App Data").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        DataSnapshot val = dataSnapshot.child("Organization");
                                        //Toast.makeText(getActivity(),val.getValue().toString(),Toast.LENGTH_SHORT).show();
                                        final String test = val.getValue().toString();


                                        mProjectsDatabase.child("Organizations/"+test+"/Projects").child(projectid+"/Meeting/Status").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                final String val  = dataSnapshot.getValue(String.class);
                                                if(val == null){
                                                    mProjectsDatabase.child("Organizations/"+test+"/Projects").child(projectid+"/uid").addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            final String val1 = dataSnapshot.getValue(String.class);
                                                            mProjectsDatabase.child("Organizations/"+test+"/Projects").child(projectid+"/upass").addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    String val2 = dataSnapshot.getValue(String.class);
                                                                    Intent intent = new Intent(getActivity(), ProjectActivity.class);
                                                                    intent.putExtra("userid",val1);
                                                                    intent.putExtra("userpass",val2);
                                                                    intent.putExtra("pname",projectid);
                                                                    startActivity(intent);
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

                                                else if (val.equals("false")) {
                                                    Intent intent = new Intent(getActivity(), ProjectMeetingCheck.class);
                                                    intent.putExtra("orgid",test);
                                                    intent.putExtra("projectid",projectid);
                                                    intent.putExtra("userid",model.getUid());
                                                    intent.putExtra("userpass",model.getUpass());
                                                    startActivity(intent);

                                                }
                                                else {
                                                    Intent intent = new Intent(getActivity(), MeetingPanel.class);
                                                    intent.putExtra("orgid",test);
                                                    intent.putExtra("projectid",projectid);
                                                    startActivity(intent);
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
                            projectscontainer.scrollToPosition(positionStart);
                        }
                    }

                });

                projectscontainer.setAdapter(mRecyclerAdapter);
                mJoinedProjectsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
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


    @Override
    public void onStart() {
        super.onStart();
        filterQuery = mProjectsDatabase.child("Organizations/BUKC/Projects").orderByChild("head").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());


        final FirebaseRecyclerAdapter<projects,ProjectsFragment.ProjectsViewHolder> mRecyclerAdapter = new FirebaseRecyclerAdapter<projects, ProjectsFragment.ProjectsViewHolder>(
                projects.class,
                R.layout.projects_layout_show,
                ProjectsFragment.ProjectsViewHolder.class,
                filterQuery) {

            @Override
            protected void populateViewHolder(ProjectsFragment.ProjectsViewHolder viewHolder, final projects model, final int position) {

                viewHolder.setDescription(model.getDescription());
                viewHolder.setProjectName(getRef(position).getKey());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String projectid = getRef(position).getKey();

                        mProjectsDatabase.child("App Data").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                DataSnapshot val = dataSnapshot.child("Organization");
                                //Toast.makeText(getActivity(),val.getValue().toString(),Toast.LENGTH_SHORT).show();
                                 final String test = val.getValue().toString();


                                mProjectsDatabase.child("Organizations/"+test+"/Projects").child(projectid+"/Meeting/Status").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String val  = dataSnapshot.getValue(String.class);
                                        if(val == null){
                                            mProjectsDatabase.child("Organizations/"+test+"/Projects").child(projectid+"/uid").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    final String val1 = dataSnapshot.getValue(String.class);
                                                    mProjectsDatabase.child("Organizations/"+test+"/Projects").child(projectid+"/upass").addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            String val2 = dataSnapshot.getValue(String.class);
                                                            Intent intent = new Intent(getActivity(), ProjectActivity.class);
                                                            intent.putExtra("userid",val1);
                                                            intent.putExtra("userpass",val2);
                                                            intent.putExtra("pname",projectid);
                                                            startActivity(intent);
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

                                        else if (val.equals("false")) {
                                            Intent intent = new Intent(getActivity(), ProjectMeetingCheck.class);
                                            intent.putExtra("orgid",test);
                                            intent.putExtra("projectid",projectid);
                                            intent.putExtra("userid",model.getUid());
                                            intent.putExtra("userpass",model.getUpass());
                                            startActivity(intent);

                                        }
                                        else {
                                            Intent intent = new Intent(getActivity(), MeetingPanel.class);
                                            intent.putExtra("orgid",test);
                                            intent.putExtra("projectid",projectid);
                                            startActivity(intent);
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
                    projectscontainer.scrollToPosition(positionStart);
                }
            }

        });
        projectscontainer.setAdapter(mRecyclerAdapter);
        filterQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    public void showCreatedProjects(){
        filterQuery = mProjectsDatabase.child("Organizations/BUKC/Projects").orderByChild("head").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());


        final FirebaseRecyclerAdapter<projects,ProjectsFragment.ProjectsViewHolder> mRecyclerAdapter = new FirebaseRecyclerAdapter<projects, ProjectsFragment.ProjectsViewHolder>(
                projects.class,
                R.layout.projects_layout_show,
                ProjectsFragment.ProjectsViewHolder.class,
                filterQuery) {

            @Override
            protected void populateViewHolder(ProjectsFragment.ProjectsViewHolder viewHolder, final projects model, final int position) {

                viewHolder.setDescription(model.getDescription());
                viewHolder.setProjectName(getRef(position).getKey());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String projectid = getRef(position).getKey();

                        mProjectsDatabase.child("App Data").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                DataSnapshot val = dataSnapshot.child("Organization");
                                //Toast.makeText(getActivity(),val.getValue().toString(),Toast.LENGTH_SHORT).show();
                                final String test = val.getValue().toString();


                                mProjectsDatabase.child("Organizations/"+test+"/Projects").child(projectid+"/Meeting/Status").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String val  = dataSnapshot.getValue(String.class);
                                        if(val == null){
                                            mProjectsDatabase.child("Organizations/"+test+"/Projects").child(projectid+"/uid").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    final String val1 = dataSnapshot.getValue(String.class);
                                                    mProjectsDatabase.child("Organizations/"+test+"/Projects").child(projectid+"/upass").addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            String val2 = dataSnapshot.getValue(String.class);
                                                            Intent intent = new Intent(getActivity(), ProjectActivity.class);
                                                            intent.putExtra("userid",val1);
                                                            intent.putExtra("userpass",val2);
                                                            intent.putExtra("pname",projectid);
                                                            startActivity(intent);
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

                                        else if (val.equals("false")) {
                                            Intent intent = new Intent(getActivity(), ProjectMeetingCheck.class);
                                            intent.putExtra("orgid",test);
                                            intent.putExtra("projectid",projectid);
                                            intent.putExtra("userid",model.getUid());
                                            intent.putExtra("userpass",model.getUpass());
                                            startActivity(intent);

                                        }
                                        else {
                                            Intent intent = new Intent(getActivity(), MeetingPanel.class);
                                            intent.putExtra("orgid",test);
                                            intent.putExtra("projectid",projectid);
                                            startActivity(intent);
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
                    projectscontainer.scrollToPosition(positionStart);
                }
            }

        });
        projectscontainer.setAdapter(mRecyclerAdapter);
        filterQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public String getOrgID(String number){
        DatabaseReference db;
        final String[] orgid = new String[1];
        final String test;
        db=FirebaseDatabase.getInstance().getReference();


        db.child("App Data").child(number).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot val = dataSnapshot.child("Organization");
                //Toast.makeText(getActivity(),val.getValue().toString(),Toast.LENGTH_SHORT).show();

                orgid[0] = val.getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return orgid[0];
    }

    public static class ProjectsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        Boolean test = false;

        public ProjectsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDescription(String description){
            TextView projectDescription = mView.findViewById(R.id.projectDescription);
            projectDescription.setText(description);
        }
        public void setProjectName(String pname){
            TextView projectName = mView.findViewById(R.id.projectName);
            projectName.setText(pname);

        }
    }
}
