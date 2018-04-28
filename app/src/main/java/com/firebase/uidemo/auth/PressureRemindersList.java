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

import java.util.Collections;
import java.util.List;

public class PressureRemindersList extends ArrayAdapter <PressureReminder>{

    private Activity context;
    private List<PressureReminder> remindersList;

    public PressureRemindersList(Activity context, List<PressureReminder> remindersList){
        super(context, R.layout.pressure_reminders_list_layout, remindersList);
        this.context = context;
        this.remindersList = remindersList;
        Collections.reverse(remindersList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate( R.layout.pressure_reminders_list_layout, null, true);

        TextView textViewHour = listViewItem.findViewById(R.id.textViewHour);
        ImageView imgView = listViewItem.findViewById(R.id.hourIcon);
        PressureReminder reminder = remindersList.get(position);

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

        if(reminder.getHour()<12){
            imgView.setImageResource(R.drawable.ic_breakfast);
        }
        else if(reminder.getHour()>= 12 && reminder.getHour()< 18){
            imgView.setImageResource(R.drawable.ic_lunch);
        }
        else if(reminder.getHour() >= 18){
            imgView.setImageResource(R.drawable.ic_meal);
        }

        return listViewItem;
    }
}
