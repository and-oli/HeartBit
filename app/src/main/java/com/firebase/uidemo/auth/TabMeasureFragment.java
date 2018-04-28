package com.firebase.uidemo.auth;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.uidemo.R;

import readerocr.RecognizeTextActivity;

public class TabMeasureFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "TabMeasureFragment";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_measure_fragment, container, false);
        Button measure = view.findViewById(R.id.button_measure);
        measure.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_measure:
                Intent measureActivity = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                startActivity(measureActivity);
                break;
        }
    }
}
