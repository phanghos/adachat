package org.taitascioredev.android.util;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.taitascioredev.android.adachat.Mensaje;

import java.util.Date;

/**
 * Created by roberto on 29/11/16.
 */

public final class ChatUtils {

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static void sendMessage(int sender, int receiver, String msg) {
        Mensaje m = fillMessageInfo(sender, receiver, msg);

        DatabaseReference ref = database.getReference("mensajes2").child(sender+"").child(receiver+"");
        DatabaseReference pushRef = ref.child("mensajes2").push();
        String key = pushRef.getKey();
        m.setKey(key);
        pushRef.setValue(m);

        ref = database.getReference("mensajes2").child(receiver+"").child(sender+"");
        pushRef = ref.child("mensajes2").push();
        key = pushRef.getKey();
        m.setKey(key);
        pushRef.setValue(m);
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
}
