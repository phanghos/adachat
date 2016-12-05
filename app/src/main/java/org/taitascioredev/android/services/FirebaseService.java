package org.taitascioredev.android.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.taitascioredev.android.adachat.App;
import org.taitascioredev.android.adachat.ChatActivity;
import org.taitascioredev.android.adachat.ChatConfiguration;
import org.taitascioredev.android.adachat.Mensaje;
import org.taitascioredev.android.util.Utils;

/**
 * Created by roberto on 21/08/16.
 */
public class FirebaseService extends Service {

    public static int CHAT_MESSAGE_NOTIFICATION_ID  = 007;
    public static boolean isRunning = false;
    public static int nMsg = 0;

    int user;

    FirebaseDatabase database;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("onStartCommand", "Starting service...");
        if (intent != null) user = intent.getIntExtra("id", 0);
        else user = Utils.getLoggedUserId(getApplicationContext());
        database = FirebaseDatabase.getInstance();

        if (!isRunning) {
            addEventListenerForChat();
            isRunning = true;
        }

        //addEventListenerForChat();

        return START_STICKY;
    }

    private void addEventListenerForChat() {
        DatabaseReference ref = database.getReference("mensajes2");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("onChildAdded (service)", dataSnapshot.getValue().toString()+"");

                Mensaje mensaje = dataSnapshot.getValue(Mensaje.class);
                if (mensaje.getIdSender() == user || mensaje.getIdReceiver() == user) {
                    if (mensaje != null && mensaje.getIdReceiver() == user && !mensaje.isVisto()
                            && !App.isChatOpen) {
                        Log.d("Debug", "Se mostrara notificacion de mensaje");
                        showNotificationForMessage(mensaje);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("onChildChanged(service)", dataSnapshot.getValue().toString()+"");

                Mensaje mensaje = dataSnapshot.getValue(Mensaje.class);
                if (mensaje.getIdSender() == user || mensaje.getIdReceiver() == user) {
                    if (mensaje != null && mensaje.getIdReceiver() == user && !mensaje.isVisto()
                            && !App.isChatOpen) {
                        Log.d("Debug", "Se mostrara notificacion de mensaje");
                        showNotificationForMessage(mensaje);
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showNotificationForMessage(Mensaje m) {
        ChatConfiguration conf = App.context.getConfiguration();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(conf.getNotificationIcon())
                        .setContentTitle(conf.getNotificationTitle())
                        .setContentText(m.getMensaje())
                        .setNumber(++nMsg);

        Intent resultIntent = new Intent(this, ChatActivity.class);
        Bundle extras = new Bundle();
        extras.putInt("id", m.getIdReceiver());
        extras.putInt("receiver", m.getIdSender());
        resultIntent.putExtras(extras);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        CHAT_MESSAGE_NOTIFICATION_ID,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        // Sets an ID for the notification
        //int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        Notification notif = mBuilder.build();
        notif.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotifyMgr.notify(CHAT_MESSAGE_NOTIFICATION_ID, mBuilder.build());
        Utils.vibrate(this);
        //if (Utils.vibrateOnNotification(getApplicationContext())) Utils.vibrate(this);
    }
}
