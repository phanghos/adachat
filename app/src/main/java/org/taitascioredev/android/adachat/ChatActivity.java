package org.taitascioredev.android.adachat;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.taitascioredev.android.listeners.OnMessageReceiveListener;
import org.taitascioredev.android.util.ChatUtils;
import org.taitascioredev.android.util.Utils;

/**
 * Created by roberto on 28/11/16.
 */
public class ChatActivity extends AppCompatActivity {

    int id;
    int receiver;

    protected ChatConfiguration mConf;

    private OnMessageReceiveListener mOnMessageReceiveListener = new OnMessageReceiveListener() {
        @Override
        public void onMessageReceived(Mensaje mensaje) {

        }
    };

    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        App.context = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbar();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getInt("id");
            receiver = extras.getInt("receiver");
            String title = extras.getString("title", "");
            if (title == null) title = "";
            setTitle(title);
            Utils.saveLoggedUserId(this, id);
            ChatUtils.startService(this, id);
        }

        if (savedInstanceState == null) {
            Fragment f = new ChatFragment();
            if (getConfiguration() == null) setConfiguration(new ChatConfiguration(this));

            if (extras != null) {
                extras.putSerializable("mConf", getConfiguration());
                f.setArguments(extras);
            } else {
                extras = new Bundle();
                extras.putSerializable("mConf", getConfiguration());
                f.setArguments(extras);
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, f).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    protected void setConfiguration(ChatConfiguration conf) {
        this.mConf = conf;
    }

    public ChatConfiguration getConfiguration() {
        return mConf;
    }

    public void setOnMessageReceiveListener(OnMessageReceiveListener listener) {
        mOnMessageReceiveListener = listener;
    }

    public OnMessageReceiveListener getOnMessageReceivedListener() {
        return mOnMessageReceiveListener;
    }

    private void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public static class Builder {

        private ChatConfiguration conf;

        public Builder(Activity context) {
            conf = new ChatConfiguration(context);
        }

        public ChatConfiguration build() {
            return conf;
        }

        public Builder setSenderBackgroundColor(int color) {
            conf.setSenderBgColor(color);
            return this;
        }

        public Builder setReceiverBackgroundColor(int color) {
            conf.setReceiverBgColor(color);
            return this;
        }

        public Builder setTextSize(int size) {
            conf.setTextSize(size);
            return this;
        }

        public Builder setTextColor(int color) {
            conf.setTextColor(color);
            return this;
        }

        public Builder setProgressWheelColor(int color) {
            conf.setProgressWheelColor(color);
            return this;
        }

        public Builder setEditTextLineColor(int color) {
            conf.setEditTextLineColor(color);
            return this;
        }

        public Builder setNotificationIcon(int icon) {
            conf.setNotificationIcon(icon);
            return this;
        }

        public Builder setEditTextHint(String hint) {
            conf.setEditTextHint(hint);
            return this;
        }

        public Builder setNotificationTitle(String title) {
            conf.setNotificationTitle(title);
            return this;
        }
    }
}
