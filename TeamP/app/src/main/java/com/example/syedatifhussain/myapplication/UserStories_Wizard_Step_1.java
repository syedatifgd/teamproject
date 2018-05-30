package com.example.syedatifhussain.myapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class UserStories_Wizard_Step_1 extends Fragment {

    private EditText ed1;
    private EditText ed2;
    private Button button5;
    private ViewPager pager;


    public UserStories_Wizard_Step_1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_stories__wizard__step_1, container, false);
        ed1 = view.findViewById(R.id.editTextStoryNameFragment);
        ed2 = view.findViewById(R.id.editTextStoryDescriptionFragment);
        button5 = view.findViewById(R.id.btnNextStepUserStories);
        pager = view.findViewById(R.id.containerUserStories);

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ed1.getText().toString().isEmpty() && !ed2.getText().toString().isEmpty()){
                    getActivity().getIntent().putExtra("storyname", ed1.getText().toString());
                    getActivity().getIntent().putExtra("storydescription", ed2.getText().toString());
                    UserStories_Wizard.switchFragment(1);

                }
                else{
                    Toast.makeText(getActivity(),"Invalid story name or description",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

}
