package org.taitascioredev.android.adachat;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.taitascioredev.android.services.FirebaseService;
import org.taitascioredev.android.util.ChatUtils;
import org.taitascioredev.android.util.Utils;

import java.util.Date;

/**
 * Created by roberto on 28/11/16.
 */
public class ChatFragment extends Fragment {

    int sender;
    int receiver;

    FirebaseDatabase database;
    DatabaseReference ref;
    ChildEventListener mChildListener;

    ChatActivity context;

    RecyclerView mRecyclerView;
    ChatAdapter mAdapter;
    LinearLayoutManager mLayoutMngr;

    ChatConfiguration conf;

    EditText etMsj;
    ImageView btnEnviar;

    ProgressWheel wheel;
    TextView empty;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        App.isChatOpen = true;
        FirebaseService.nMsg = 0;
        context = App.context;

        etMsj = (EditText) getActivity().findViewById(R.id.et_mensaje);
        btnEnviar = (ImageView) getActivity().findViewById(R.id.iv_enviar);

        mRecyclerView = (RecyclerView) getView().findViewById(R.id.list_img);
        wheel = (ProgressWheel) getView().findViewById(R.id.progress_wheel);
        empty = (TextView) getView().findViewById(R.id.tv_empty);

        wheel.setVisibility(View.VISIBLE);

        Bundle extras = getArguments();
        if (extras != null) {
            sender   = extras.getInt("id");
            receiver = extras.getInt("receiver");
            conf     = (ChatConfiguration) extras.getSerializable("mConf");
            cancelNotification(FirebaseService.CHAT_MESSAGE_NOTIFICATION_ID);
        }

        int wheelColor = conf.getProgressWheelColor();
        wheel.setBarColor(ContextCompat.getColor(context, wheelColor));

        int editTextLineColor = conf.getEditTextLineColor();
        etMsj.getBackground().setColorFilter(
                ContextCompat.getColor(context, editTextLineColor), PorterDuff.Mode.SRC_ATOP);

        String editTextHint = conf.getEditTextHint();
        etMsj.setHint(editTextHint);

        addEventListener();

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = etMsj.getText().toString();

                if (!msg.isEmpty()) {
                    ChatUtils.sendMessage(sender, receiver, etMsj.getText().toString().trim());

                    /*
                    if (ticket != null) {
                        ref.child("id_ticket").setValue(ticket.getId());
                        ref.child("id_cliente").setValue(ticket.getIdSender());
                        ref.child("asunto").setValue(ticket.getAsunto());
                        ref.child("consulta").setValue(ticket.getConsulta());
                        ref.child("producto").setValue(ticket.getNombre_producto());
                    }
                    */

                    Utils.hideKeyboard(getActivity());
                    etMsj.setText("");
                }
            }
        });

        mLayoutMngr = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutMngr);
        mAdapter = new ChatAdapter(getActivity(), conf);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("Debug", "onStop");
        FirebaseService.nMsg = 0;
        App.isChatOpen = false;
        ref.removeEventListener(mChildListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Debug", "onDestroy");
        FirebaseService.nMsg = 0;
        App.isChatOpen = false;
        ref.removeEventListener(mChildListener);
    }

    private void cancelNotification(int id) {
        NotificationManager mNotifMngr = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifMngr.cancel(id);
    }

    private void addEventListener() {
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("mensajes2");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                wheel.setVisibility(View.GONE);

                int count = 0;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Mensaje m = child.getValue(Mensaje.class);
                    if (m.getIdSender() == sender && m.getIdReceiver() == receiver
                            || m.getIdSender() == receiver && m.getIdReceiver() == sender) count++;
                }

                if (count == 0) {
                    empty.setText("Aún no has enviado un mensaje");
                    empty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("onChildAdded", dataSnapshot.getValue().toString());

                Mensaje m = dataSnapshot.getValue(Mensaje.class);
                if (m.getIdSender() == sender && m.getIdReceiver() == receiver
                        || m.getIdSender() == receiver && m.getIdReceiver() == sender) {
                    context.getOnMessageReceivedListener().onMessageReceived(m);
                    if (m.getIdReceiver() == sender && !m.isVisto() && App.isChatOpen) {
                        m.setVisto(true);
                        ref.child(m.getKey()).child("visto").setValue(true);
                    }

                    mAdapter.add(m);
                    mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                    empty.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("onChildChanged", dataSnapshot.getValue().toString());
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
        };

        ref.addChildEventListener(mChildListener);
    }
}
