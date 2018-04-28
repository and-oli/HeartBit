package com.firebase.uidemo.auth;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.uidemo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Calendar;


public class TimePickerMedicineFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    private FirebaseUser user = null;
    DatabaseReference databaseReference;
    FragmentActivity activity = null;

    public TimePickerMedicineFragment(){

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("usersData")
                .child(user.getUid())
                .child("reminders")
                .child("medicines");
        activity = getActivity();
        return new TimePickerDialog(getActivity(), this,
                hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String key = databaseReference.push().getKey();
        int hCode = 0;
        for (int i = 0; i < key.length(); i++){
            hCode = hCode + Character.getNumericValue(key.charAt(i))*i;
        }
        showInputBox (hourOfDay, minute, key, hCode);
    }

    public void showInputBox (int hourOfDay, int minute, String key, int hCode){

        final int h = hourOfDay;
        final int m = minute;
        final String k = key;
        final int hs = hCode;

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.input_box_medicine_name);
        final EditText editName = dialog.findViewById(R.id.med_name);

        Button btnSave = dialog.findViewById(R.id.save_reminder);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString();
                if(!name.equals("")){
                    MedicineReminder reminder = new MedicineReminder(h, m, name, k, hs);
                    databaseReference.child(k).setValue(reminder);
                    dialog.dismiss();
                    addNotification(reminder);
                } else {
                    Toast.makeText(activity, "Invalid name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    public void addNotification (MedicineReminder reminder){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, reminder.getHour());
        calendar.set(Calendar.MINUTE, reminder.getMinute());
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(activity.getApplicationContext(), NotificationReceiver.class);
        intent.putExtra("REQ_CODE", String.valueOf(reminder.getHash()));
        intent.putExtra("MED_NAME", reminder.getName());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity.getApplicationContext(), reminder.getHash(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
