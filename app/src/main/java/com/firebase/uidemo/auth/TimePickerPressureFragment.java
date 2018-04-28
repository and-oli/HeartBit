package com.firebase.uidemo.auth;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;


public class TimePickerPressureFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    private FirebaseUser user = null;
    DatabaseReference databaseReference;

    public TimePickerPressureFragment(){

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
                .child("pressure");

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
        PressureReminder reminder = new PressureReminder(hourOfDay, minute, key, hCode);
        databaseReference.child(key).setValue(reminder);
        addNotification(reminder);
    }

    public void addNotification (PressureReminder reminder){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, reminder.getHour());
        calendar.set(Calendar.MINUTE, reminder.getMinute());
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(getActivity().getApplicationContext(), NotificationReceiver.class);
        intent.putExtra("REQ_CODE", String.valueOf(reminder.getHash()));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), reminder.getHash(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
