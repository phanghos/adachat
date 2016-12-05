package org.taitascioredev.android.util;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.taitascioredev.android.adachat.ChatActivity;
import org.taitascioredev.android.adachat.Mensaje;
import org.taitascioredev.android.listeners.ConversationListener;
import org.taitascioredev.android.services.FirebaseService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by roberto on 29/11/16.
 */

public final class ChatUtils {

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static void sendMessage(int sender, int receiver, String msg) {
        Mensaje m = fillMessageInfo(sender, receiver, msg);

        DatabaseReference ref = database.getReference("mensajes2");
        DatabaseReference pushRef =  ref.push(); //ref.child("mensajes2").push();
        String key = pushRef.getKey();
        m.setKey(key);
        pushRef.setValue(m);

        /*
        ref = database.getReference("mensajes2");
        pushRef = ref.push(); //ref.child("mensajes2").push();
        key = pushRef.getKey();
        m.setKey(key);
        pushRef.setValue(m);
        */
    }

    private static Mensaje fillMessageInfo(int sender, int receiver, String msg) {
        Mensaje m = new Mensaje();
        m.setIdSender(sender);
        m.setIdReceiver(receiver);
        m.setVisto(false);
        m.setMensaje(msg);
        m.setTime(new Date());

        return m;
    }

    public static void startService(Context context, int id) {
        if (!FirebaseService.isRunning) {
            Intent serviceIntent = new Intent(context, FirebaseService.class);
            serviceIntent.putExtra("id", id);
            context.startService(serviceIntent);
        }
    }

    public static void startChat(Context context, Class<?> activity, int sender, int receiver,
                                 String title) {
        Utils.saveLoggedUserId(context, sender);
        Intent i = new Intent(context, activity);
        i.putExtra("id", sender);
        i.putExtra("receiver", receiver);
        i.putExtra("title", title);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public static void startChat(Context context, int sender, int receiver, String title) {
        startChat(context, ChatActivity.class, sender, receiver, title);
    }

    public static void getConversations(final int user, final int receiver,
                                        final ConversationListener listener) {
        DatabaseReference ref = database.getReference("mensajes2");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Mensaje> list = new ArrayList<>();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Mensaje m = child.getValue(Mensaje.class);
                    if (m.getIdSender() == user && m.getIdReceiver() == receiver) list.add(m);
                }

                listener.onSucess(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void getConversations(final int user, final ConversationListener listener) {
        getConversations(user, user, listener);
    }

    public static void getConversations(final ConversationListener listener) {
        DatabaseReference ref = database.getReference("mensajes2");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Mensaje> list = new ArrayList<>();

                for (DataSnapshot child : dataSnapshot.getChildren())
                    list.add(child.getValue(Mensaje.class));

                listener.onSucess(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
