package com.firebase.uidemo.auth;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.uidemo.R;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MedicineRemindersList extends ArrayAdapter <MedicineReminder>{

    private Activity context;
    private List<MedicineReminder> remindersList;

    public MedicineRemindersList(Activity context, List<MedicineReminder> remindersList){
        super(context, R.layout.medicine_reminders_list_layout, remindersList);
        this.context = context;
        this.remindersList = remindersList;
        Collections.reverse(remindersList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate( R.layout.medicine_reminders_list_layout, null, true);

        TextView textViewMedicine = listViewItem.findViewById(R.id.textViewMedicine);
        TextView textViewHour = listViewItem.findViewById(R.id.textViewHour);
        MedicineReminder reminder = remindersList.get(position);

        String displayedMedicine = reminder.getName();
        textViewMedicine.setText(displayedMedicine);

        String displayedHour = "";

        if(reminder.getHour() >= 10 && reminder.getMinute() >= 10) {
            displayedHour = reminder.getHour() + ":" + reminder.getMinute();
        }
        else if(reminder.getHour() >= 10 && reminder.getMinute() < 10){
            displayedHour = reminder.getHour() + ":0" + reminder.getMinute();
        }
        else if(reminder.getHour() < 10 && reminder.getMinute() >= 10){
            displayedHour = "0" + reminder.getHour() + ":" + reminder.getMinute();
        }
        else if(reminder.getHour() < 10 && reminder.getMinute() < 10){
            displayedHour = "0" + reminder.getHour() + ":0" + reminder.getMinute();
        }

        textViewHour.setText(displayedHour);
        return listViewItem;
    }
}
