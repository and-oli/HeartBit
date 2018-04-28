package com.firebase.uidemo.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.firebase.uidemo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GraphsFragment extends Fragment implements View.OnClickListener{

    private FirebaseUser user = null;
    private View view = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_graphs, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();

        FloatingActionButton newRecord = view.findViewById(R.id.fab);
        newRecord.setOnClickListener(this);

        return view;
    }

    public void onResume(){
        super.onResume();
        ((BottomNavActivity)getActivity()).setActionBarTitle("Blood Pressure Graph");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                startActivity(new Intent(view.getContext(), NewRecordActivity.class));
                break;
        }
    }
}
