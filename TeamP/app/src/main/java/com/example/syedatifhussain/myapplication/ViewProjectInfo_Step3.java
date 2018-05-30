package com.example.syedatifhussain.myapplication;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by SyedAtif on 5/29/2018.
 */

public class ViewProjectInfo_Step3 extends android.support.v4.app.Fragment {

    public ViewProjectInfo_Step3() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_project_info_team_lead_step_3, container, false);

        return view;
    }
}
