package com.firebase.uidemo.auth;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.firebase.uidemo.R;

public class NotificationReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        int reqCode = Integer.parseInt(intent.getStringExtra("REQ_CODE"));
        String medName = intent.getStringExtra("MED_NAME");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context
                .NOTIFICATION_SERVICE);
        Intent repeatingIntent = new Intent(context, BottomNavActivity.class);
        repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, repeatingIntent, PendingIntent
                .FLAG_UPDATE_CURRENT);

        String title = "";
        String text = "";

        if(medName == null){
            title = "Take blood pressure reminder";
            text = "Time to measure your blood pressure";
        } else {
            title = "Take medicine reminder";
            text = "Time to take your medicine: " + medName;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_pulse)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true);

        notificationManager.notify(reqCode, builder.build());
    }
}
