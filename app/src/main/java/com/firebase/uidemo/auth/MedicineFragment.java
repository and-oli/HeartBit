package com.firebase.uidemo.auth;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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

public class MedicineFragment extends Fragment implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;
    ListView listViewReminders;
    private View view = null;
    DatabaseReference databaseReference;
    private FirebaseUser user = null;
    List <MedicineReminder> remindersList;

    TextView noRecordText;
    ImageView noImageView;
    FragmentActivity activity;
    ValueEventListener eventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_medicine, container, false);
        FloatingActionButton newMedicineReminder = view.findViewById(R.id.fab);
        newMedicineReminder.setOnClickListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("usersData")
                .child(user.getUid())
                .child("reminders")
                .child("medicines");
        remindersList = new ArrayList<>();
        activity = getActivity();

        listViewReminders = view.findViewById(R.id.medicine_reminders);
        listViewReminders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MedicineReminder reminder = (MedicineReminder) parent.getItemAtPosition(position);
                showDialogBox(reminder);
            }
        });

        noRecordText = view.findViewById(R.id.no_records);
        noImageView =  view.findViewById(R.id.no_records_image);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                DialogFragment timePicker = new TimePickerMedicineFragment();
                timePicker.show(getActivity().getSupportFragmentManager(), "time picker");
                break;
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStart() {
        super.onStart();
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                remindersList.clear();
                for(DataSnapshot recordSnapshot: dataSnapshot.getChildren()){

                    int hour = recordSnapshot.child("hour").getValue(Integer.class);
                    int minute = recordSnapshot.child("minute").getValue(Integer.class);
                    String name = recordSnapshot.child("name").getValue(String.class);
                    String key = recordSnapshot.getKey();
                    int hCode = 0;
                    for (int i = 0; i < key.length(); i++){
                        hCode = hCode + Character.getNumericValue(key.charAt(i))*i;
                    }
                    MedicineReminder reminder = new MedicineReminder(hour, minute, name, key, hCode);
                    remindersList.add(reminder);
                }
                if(getActivity() != null){
                    if(!remindersList.isEmpty()) {
                        MedicineRemindersList adapter = new MedicineRemindersList(getActivity(), remindersList);
                        listViewReminders.setAdapter(adapter);
                        noRecordText.setVisibility(View.INVISIBLE);
                        noImageView.setVisibility(View.INVISIBLE);
                        listViewReminders.setVisibility(View.VISIBLE);
                    } else {
                        listViewReminders.setVisibility(View.INVISIBLE);
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
    public void showDialogBox(MedicineReminder reminder){
        final Dialog dialog = new Dialog(getActivity());
        dialog.setTitle("Edit/Delete reminder");
        dialog.setContentView(R.layout.dialog_box_edit_reminder);

        final MedicineReminder actualReminder = reminder;

        Button btnEdit = dialog.findViewById(R.id.edit_reminder);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Later", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnDelete = dialog.findViewById(R.id.delete_reminder);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = actualReminder.getKey();
                databaseReference.child(key).removeValue();

                Intent intent = new Intent(activity.getApplicationContext(), NotificationReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(activity.getApplicationContext(), actualReminder.getHash(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);

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
