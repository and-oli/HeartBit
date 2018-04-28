package com.firebase.uidemo.auth;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.uidemo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DoctorFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {

    private FirebaseUser user = null;
    DatabaseReference databaseDoctor;

    private View view = null;
    private LinearLayout noDoc;
    private ConstraintLayout editDoc;
    private ConstraintLayout docInfo;
    private TextView docName;
    private TextView docPhone;
    private EditText drName;
    private EditText drPhone;
    private FloatingActionButton callDoc;
    ValueEventListener eventListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_doctor, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseDoctor = FirebaseDatabase.getInstance().getReference("usersData")
                .child(user.getUid())
                .child("doctor");

        docName = view.findViewById(R.id.dr_name);
        docPhone = view.findViewById(R.id.dr_phone);
        drName = view.findViewById(R.id.doctors_name);
        drPhone = view.findViewById(R.id.doctors_number);

        noDoc = view.findViewById(R.id.noDocLayout);
        editDoc = view.findViewById(R.id.setDocLayout);
        docInfo = view.findViewById(R.id.docInfoLayout);

        docInfo.setVisibility(View.INVISIBLE);
        noDoc.setVisibility(View.INVISIBLE);
        editDoc.setVisibility(View.INVISIBLE);

        checkDr();

        Button setDoctor = view.findViewById(R.id.button_create_doctor);
        setDoctor.setOnClickListener(this);

        Button save = view.findViewById(R.id.button_save);
        save.setOnClickListener(this);

        Button edit = view.findViewById(R.id.button_edit);
        edit.setOnClickListener(this);

        FloatingActionButton newRecord = view.findViewById(R.id.fab);
        newRecord.setOnClickListener(this);

        callDoc = view.findViewById(R.id.call_button);
        callDoc.setOnTouchListener(this);

        return view;
    }

    public void onResume(){
        super.onResume();
        ((BottomNavActivity)getActivity()).setActionBarTitle("Doctor");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                startActivity(new Intent(view.getContext(), NewRecordActivity.class));
                break;
            case R.id.button_create_doctor:
                noDoc.setVisibility(View.INVISIBLE);
                editDoc.setVisibility(View.VISIBLE);
                break;
            case R.id.button_save:
                String name = drName.getText().toString();
                String phone  = drPhone.getText().toString();
                if(!name.equals("") && !phone.equals("")  && phone.length() == 10){
                    Doctor dr = new Doctor(name, phone);
                    databaseDoctor.setValue(dr);
                    editDoc.setVisibility(View.INVISIBLE);
                    docInfo.setVisibility(View.VISIBLE);
                } else{
                    Toast.makeText(getActivity(), "Invalid data", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_edit:
                docInfo.setVisibility(View.INVISIBLE);
                drName.setText(docName.getText());
                drPhone.setText(docPhone.getText());
                editDoc.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void checkDr(){
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    Doctor dr = dataSnapshot.getValue(Doctor.class);
                    docName.setText(dr.getName());
                    docPhone.setText(dr.getPhoneNumber());
                    docInfo.setVisibility(View.VISIBLE);
                    noDoc.setVisibility(View.INVISIBLE);
                    editDoc.setVisibility(View.INVISIBLE);
                }catch (Exception e){
                    noDoc.setVisibility(View.VISIBLE);
                    editDoc.setVisibility(View.INVISIBLE);
                    docInfo.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseDoctor.addValueEventListener(eventListener);
    }

    @Override
    public boolean onTouch (View v, MotionEvent event){
        switch (v.getId()){

            case R.id.call_button:{
                Vibrator vib = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    vib.vibrate(50);
                    v.animate().scaleXBy(1f).start();
                    v.animate().scaleYBy(1f).start();
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    v.animate().cancel();
                    v.animate().scaleX(1f).start();
                    v.animate().scaleY(1f).start();
                    String phone = docPhone.getText().toString();
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                    startActivity(intent);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(eventListener != null){
            databaseDoctor.removeEventListener(eventListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(eventListener != null){
            databaseDoctor.removeEventListener(eventListener);
        }
    }
}
