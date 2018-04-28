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

import readerocr.AndroidCamera;
import readerocr.RecognizeTextActivity;
import readerocr.utils.CommonUtils;

import static readerocr.utils.CommonUtils.info;

public class TabReadFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "TabReadFragment";
    private View view = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_read_fragment, container, false);

        Button scan = view.findViewById(R.id.button_scan);
        scan.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_scan:
                Intent recognizeActivity = new Intent(getActivity().getApplicationContext(), RecognizeTextActivity.class);
                getActivity().finish();
                recognizeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(recognizeActivity);
                break;
        }
    }
}
