package com.firebase.uidemo.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.uidemo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.speech.RecognizerIntent;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class TabTypeFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {

    private static final String TAG = "TabTypeFragment";
    private View view = null;

    EditText systolic;
    EditText diastolic;
    Button save;
    FloatingActionButton dictateSys;
    FloatingActionButton dictateDia;
    Boolean isSystolic;

    private FirebaseUser user = null;
    DatabaseReference databaseRecords;
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_type_fragment, container, false);

        systolic = view.findViewById(R.id.edit_sys);
        diastolic = view.findViewById(R.id.edit_dias);

        dictateSys = view.findViewById(R.id.sys_button);
        dictateSys.setOnTouchListener(this);

        dictateDia = view.findViewById(R.id.dias_button);
        dictateDia.setOnTouchListener(this);

        save = view.findViewById(R.id.save_typed_record);
        save.setOnClickListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseRecords = FirebaseDatabase.getInstance().getReference("usersData")
                                                        .child(user.getUid())
                                                        .child("records");
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save_typed_record:{

                int sys = -1;
                int dia = -1;

                try{
                    sys = Integer.parseInt(systolic.getText().toString());
                    dia = Integer.parseInt(diastolic.getText().toString());

                    if(sys <= 0 || dia <= 0) {
                        throw new Exception();
                    } else {
                        String key = databaseRecords.push().getKey();
                        long timeStamp = System.currentTimeMillis();
                        Record record = new Record(sys, dia, timeStamp, key);
                        databaseRecords.child(key).setValue(record);
                        Toast.makeText(getActivity(), "Record added", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                } catch (Exception e){
                    Toast.makeText(getActivity(), "Invalid record", Toast.LENGTH_SHORT).show();
                }
            } break;
        }
    }

    @Override
    public boolean onTouch (View v, MotionEvent event){
        switch (v.getId()){

            case R.id.sys_button:{
                Vibrator vib = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    vib.vibrate(50);
                    v.animate().scaleXBy(1f).start();
                    v.animate().scaleYBy(1f).start();
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    vib.vibrate(50);
                    v.animate().cancel();
                    v.animate().scaleX(1f).start();
                    v.animate().scaleY(1f).start();
                    startVoiceRecognitionActivity(true);
                }
                return true;
            }
            case R.id.dias_button:{
                Vibrator vib = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    vib.vibrate(50);
                    v.animate().scaleXBy(1f).start();
                    v.animate().scaleYBy(1f).start();
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    vib.vibrate(50);
                    v.animate().cancel();
                    v.animate().scaleX(1f).start();
                    v.animate().scaleY(1f).start();
                    startVoiceRecognitionActivity(false);
                }
                return true;
            }
        }
        return false;
    }

    public void startVoiceRecognitionActivity(boolean syst) {

        isSystolic = syst;

        String action = "";
        if(syst){
            action += "Dictate systolic pressure";
        } else {
            action += "Dictate diastolic pressure";
        }

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, action);
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            int measure = -1;
            ArrayList matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            for (int i = 0; i < matches.size(); i++){
                try{
                    int temp = Integer.parseInt(matches.get(i).toString());
                    measure = temp;
                } catch (Exception e){
                    if(i == matches.size()-1 && measure == -1){
                        Toast.makeText(getActivity(), "No measure detected. Try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            if(measure != -1){
                if(isSystolic){
                    systolic.setText(String.valueOf(measure));
                } else {
                    diastolic.setText(String.valueOf(measure));
                }
            }
        }
    }
}
