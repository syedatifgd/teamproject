package com.example.syedatifhussain.myapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class UserStories_Wizard_Step_2 extends Fragment {

    private RadioGroup radioGroup;
    private int radioButtonID = 0;
    private RadioButton r1;
    private Button button5;



    public UserStories_Wizard_Step_2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user_stories__wizard__step_2, container, false);

        radioGroup = view.findViewById(R.id.radioGroupFragment);
        button5 = view.findViewById(R.id.btnNextStepUserStories2);

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioButtonID = radioGroup.getCheckedRadioButtonId();
                r1 = radioGroup.findViewById(radioButtonID);
                String selectedText = (String)r1.getText();

                getActivity().getIntent().putExtra("checked", selectedText);
                UserStories_Wizard.switchFragment(2);
            }
        });

        return view;
    }

}
