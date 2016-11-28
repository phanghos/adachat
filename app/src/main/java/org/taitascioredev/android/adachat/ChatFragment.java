package org.taitascioredev.android.adachat;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import java.util.Date;

/**
 * Created by roberto on 23/08/16.
 */
public class ChatFragment extends Fragment {

    int sender;
    int receiver;

    FirebaseDatabase database;
    DatabaseReference ref;

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
        FirebaseClientService.nMsg = 0;

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
            conf     = (ChatConfiguration) extras.getSerializable("conf");
            cancelNotification(FirebaseClientService.CHAT_MESSAGE_NOTIFICATION_ID);
        }

        addEventListener();

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = etMsj.getText().toString();

                if (!msg.isEmpty()) {
                    Mensaje m = fillMessageInfo();

                    DatabaseReference ref = database.getReference("mensajes2").child(sender +"").child(receiver+"");
                    DatabaseReference pushRef = ref.child("mensajes2").push();
                    String key = pushRef.getKey();
                    m.setKey(key);

                    /*
                    if (ticket != null) {
                        ref.child("id_ticket").setValue(ticket.getId());
                        ref.child("id_cliente").setValue(ticket.getIdSender());
                        ref.child("asunto").setValue(ticket.getAsunto());
                        ref.child("consulta").setValue(ticket.getConsulta());
                        ref.child("producto").setValue(ticket.getNombre_producto());
                    }
                    */

                    pushRef.setValue(m);

                    ref = database.getReference("mensajes2").child(receiver+"").child(sender+"");
                    pushRef = ref.child("mensajes2").push();
                    key = pushRef.getKey();
                    m.setKey(key);
                    pushRef.setValue(m);

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
    public void onDestroy() {
        super.onDestroy();
        FirebaseClientService.nMsg = 0;
        App.isChatOpen = false;
    }

    private void cancelNotification(int id) {
        NotificationManager mNotifMngr = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifMngr.cancel(id);
    }

    private void addEventListener() {
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("mensajes2").child(sender+"").child(receiver+"").child("mensajes2");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                wheel.setVisibility(View.GONE);
                if (dataSnapshot.getChildrenCount() == 0) {
                    empty.setText("Aún no has enviado un mensaje");
                    empty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("onChildAdded", dataSnapshot.getValue().toString());
                try {
                    Mensaje m = dataSnapshot.getValue(Mensaje.class);
                    Log.d("MENSAJE", m.getMensaje());
                    if (m.getIdReceiver() > 0 && !m.isVisto()) {
                        m.setVisto(true);
                        ref.child(m.getKey()).child("visto").setValue(true);
                    }
                    mAdapter.add(m);
                    mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                } catch (DatabaseException e) {

                } catch (ClassCastException e) {

                }

                empty.setVisibility(View.GONE);
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
        });
    }

    private Mensaje fillMessageInfo() {
        Mensaje m = new Mensaje();
        //int idCliente = Utils.getLoggedUserId(getActivity());
        m.setIdSender(sender);
        m.setIdReceiver(receiver);
        m.setVisto(false);
        m.setMensaje(etMsj.getText().toString());
        m.setTime(new Date());

        return m;
    }
}
