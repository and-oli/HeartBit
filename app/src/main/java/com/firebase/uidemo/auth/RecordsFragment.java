package com.firebase.uidemo.auth;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.List;

public class RecordsFragment extends Fragment implements View.OnClickListener{

    private FirebaseUser user = null;
    private View view = null;

    ListView listViewRecords;
    TextView noRecordText;
    ImageView noImageView;
    DatabaseReference databaseReference;
    List <Record> recordList;
    FragmentActivity activity;
    ValueEventListener eventListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_records, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        activity = getActivity();

        FloatingActionButton newRecord = view.findViewById(R.id.fab);
        newRecord.setOnClickListener(this);

        listViewRecords = view.findViewById(R.id.listViewRecords);
        listViewRecords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Record record = (Record) parent.getItemAtPosition(position);
                showInputBox(record);
            }
        });

        recordList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("usersData")
                .child(user.getUid())
                .child("records");

        noRecordText = view.findViewById(R.id.no_records);
        noImageView =  view.findViewById(R.id.no_records_image);

        return view;
    }

    public void onResume(){
        super.onResume();
        ((BottomNavActivity)getActivity()).setActionBarTitle("Records");
    }

    @Override
    public void onStart() {
        super.onStart();
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recordList.clear();
                for(DataSnapshot recordSnapshot: dataSnapshot.getChildren()){

                    int sys = recordSnapshot.child("systolicPressure").getValue(Integer.class);
                    int dia = recordSnapshot.child("diastolicPressure").getValue(Integer.class);
                    long add = recordSnapshot.child("added").getValue(Long.class);
                    String key = recordSnapshot.getKey();

                    Record record = new Record(sys, dia, add, key);
                    recordList.add(record);
                }
                if(getActivity() != null){
                    if(!recordList.isEmpty()) {
                        RecordsList adapter = new RecordsList(getActivity(), recordList);
                        listViewRecords.setAdapter(adapter);
                        noRecordText.setVisibility(View.INVISIBLE);
                        noImageView.setVisibility(View.INVISIBLE);
                        listViewRecords.setVisibility(View.VISIBLE);
                    } else {
                        listViewRecords.setVisibility(View.INVISIBLE);
                        noRecordText.setVisibility(View.VISIBLE);
                        noImageView.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addValueEventListener(eventListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                startActivity(new Intent(view.getContext(), NewRecordActivity.class));
                break;
        }
    }

    public void showInputBox (Record record){
        final Dialog dialog=new Dialog(getActivity());
        dialog.setTitle("Edit/Delete record");
        dialog.setContentView(R.layout.input_box_edit_record);

        final Record actualRecord = record;

        final EditText editSys = dialog.findViewById(R.id.edit_sys);
        editSys.setText(String.valueOf(record.getSystolicPressure()));

        final EditText editDia = dialog.findViewById(R.id.edit_dia);
        editDia.setText(String.valueOf(record.getDiastolicPressure()));

        Button btnSave = dialog.findViewById(R.id.edit_reminder);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    int sys = Integer.parseInt(editSys.getText().toString());
                    int dia = Integer.parseInt(editDia.getText().toString());
                    long timeStamp = actualRecord.getAdded();
                    String key = actualRecord.getKey();
                    Record newRecord = new Record(sys, dia, timeStamp, key);
                    databaseReference.child(key).setValue(newRecord);
                    dialog.dismiss();
                } catch (Exception e){
                    Toast.makeText(activity, "Invalid record", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnDelete = dialog.findViewById(R.id.delete_record);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = actualRecord.getKey();
                databaseReference.child(key).removeValue();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        databaseReference.removeEventListener(eventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        databaseReference.removeEventListener(eventListener);
    }
}

