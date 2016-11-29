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
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.taitascioredev.android.adachat.App;
import org.taitascioredev.android.adachat.ChatActivity;
import org.taitascioredev.android.adachat.Conversacion;
import org.taitascioredev.android.adachat.Mensaje;
import org.taitascioredev.android.adachat.R;
import org.taitascioredev.android.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by roberto on 21/08/16.
 */
public class FirebaseService extends Service {

    public static boolean isRunning = false;
    public static int nMsg = 0;
    public static int CHAT_MESSAGE_NOTIFICATION_ID  = 007;
    public static List<Mensaje> list;

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
        list = new ArrayList<Mensaje>();
        user = Utils.getLoggedUserId(this);
        database = FirebaseDatabase.getInstance();

        if (!isRunning) {
            addEventListenerForChat();
            isRunning = true;
        }

        return START_STICKY;
    }

    private void addEventListenerForChat() {
        DatabaseReference ref = database.getReference("mensajes2").child(user+"");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("onChildAdded", dataSnapshot.getValue().toString()+"");

                try {
                    Conversacion c = dataSnapshot.getValue(Conversacion.class);
                    if (c.getMensajes() == null) return;
                    HashMap<String, Mensaje> mensajes = c.getMensajes();

                    for (String key : mensajes.keySet()) {
                        Mensaje mensaje = mensajes.get(key);
                        if (mensaje != null && mensaje.getIdReceiver() > 0 && !mensaje.isVisto()
                                && !App.isChatOpen)
                            showNotificationForMessage(mensaje, c.getId_ticket());
                    }
                } catch (DatabaseException e) {

                } catch (ClassCastException e) {

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("onChildChanged", dataSnapshot.getValue().toString()+"");

                try {
                    Conversacion c = dataSnapshot.getValue(Conversacion.class);
                    if (c.getMensajes() == null) return;
                    HashMap<String, Mensaje> mensajes = c.getMensajes();
                    Object[] keys = mensajes.keySet().toArray();
                    Mensaje[] list = new Mensaje[keys.length];
                    for (int i = 0; i < keys.length; i++) list[i] = mensajes.get(keys[i]+"");
                    Arrays.sort(list);

                    Mensaje mensaje = list[list.length - 1];
                    Log.d("Ultimo mensaje", mensaje.getMensaje());
                    if (mensaje != null && mensaje.getIdReceiver() > 0 && !mensaje.isVisto()
                            && !App.isChatOpen) {
                        Log.d("Debug", "Se mostrata notificacion de mensaje");
                        showNotificationForMessage(mensaje, c.getId_ticket());
                    }
                } catch (DatabaseException e) {

                } catch (ClassCastException e) {

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

    private void showNotificationForMessage(Mensaje m, int idTicket) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_chat_white_24dp)
                        .setContentTitle("Te han enviado un mensaje")
                        .setContentText(m.getMensaje())
                        .setNumber(++nMsg);

        Intent resultIntent = new Intent(this, ChatActivity.class);
        Bundle b = new Bundle();
        b.putInt("id", idTicket);
        b.putInt("receiver", m.getIdSender());
        b.putInt("notif_id", idTicket);
        resultIntent.putExtras(b);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        idTicket,
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
        //if (Utils.vibrateOnNotification(getApplicationContext())) Utils.vibrate(this);
    }
}
