package org.taitascioredev.android.sampleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import org.taitascioredev.android.adachat.ChatActivity;
import org.taitascioredev.android.adachat.R;
import org.taitascioredev.android.util.ChatUtils;

/**
 * Created by roberto on 05/12/16.
 */

public class MainActivity extends AppCompatActivity {

    EditText etSender;
    EditText etReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etSender   = (EditText) findViewById(R.id.et_sender);
        etReceiver = (EditText) findViewById(R.id.et_receiver);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sender   = Integer.parseInt(etSender.getText().toString());
                int receiver = Integer.parseInt(etReceiver.getText().toString());
                ChatUtils.startChat(getApplicationContext(), ChatActivity.class, sender, receiver,
                        "Chat");
            }
        });
    }
}
