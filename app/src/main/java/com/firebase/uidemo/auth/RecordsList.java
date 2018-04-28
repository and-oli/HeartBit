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

public class RecordsList extends ArrayAdapter <Record>{

    private Activity context;
    private List<Record> recordsList;

    public RecordsList (Activity context, List<Record> recordsList){
        super(context, R.layout.records_list_layout, recordsList);
        this.context = context;
        this.recordsList = recordsList;
        Collections.reverse(recordsList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate( R.layout.records_list_layout, null, true);

        TextView textViewData = listViewItem.findViewById(R.id.textViewData);
        TextView textViewDate = listViewItem.findViewById(R.id.textViewDate);
        ImageView imgViewColor = listViewItem.findViewById(R.id.recordColor);

        Record record = recordsList.get(position);

        String displayedRecord = record.getSystolicPressure() + "/" + record.getDiastolicPressure();
        textViewData.setText(displayedRecord);

        long dateInMillis = record.getAdded();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        Date toDate = new Date (dateInMillis);
        String displayedDate = sdf.format(toDate);
        textViewDate.setText(displayedDate);

        Random random = new Random();
        int rand = random.nextInt(3)+1;
        switch (rand){
            case 1:{
                imgViewColor.setImageResource(R.drawable.ic_red);
            }break;
            case 2:{
                imgViewColor.setImageResource(R.drawable.ic_yellow);
            }break;
            case 3:{
                imgViewColor.setImageResource(R.drawable.ic_green);
            }break;
        }
        return listViewItem;
    }
}
