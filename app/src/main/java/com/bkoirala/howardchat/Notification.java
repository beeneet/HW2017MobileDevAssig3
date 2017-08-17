package com.bkoirala.howardchat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by bkoirlal on 8/7/17.
 */

public class Notification extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i("BK", "service start");
        setupDatabase();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("BK", "service stop");
    }

    private void setupDatabase(){
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference childrenRef = databaseRef.child("messages");
        Query endMessage = childrenRef.limitToLast(1);
        endMessage.addValueEventListener(new ValueEventListener() {
            boolean mMessageLoaded = false;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mMessageLoaded == false){
                    mMessageLoaded = true;
                    return;
                }
                DataSnapshot msgSnapshot = dataSnapshot.getChildren().iterator().next();
                Message showMessage = new Message(msgSnapshot);
                if (!showMessage.getmUserName().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
                    showNotification(showMessage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showNotification(Message message){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        android.app.Notification n = new NotificationCompat.Builder(this)
                .setContentTitle("New Message")
                .setContentText(" " + message.getmUserName() + " : " + message.getmContent())
                .setSmallIcon(android.R.drawable.btn_dropdown)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(0, n);

    }
}
