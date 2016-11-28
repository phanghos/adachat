package org.taitascioredev.android.adachat;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * Created by roberto on 28/11/16.
 */
public class ChatActivity extends AppCompatActivity {

    int id;
    int receiver;

    protected ChatConfiguration conf;

    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbar();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getInt("id");
            receiver = extras.getInt("receiver");
        }

        if (savedInstanceState == null) {
            Fragment f = new ChatFragment();
            if (getConfiguration() == null) setConfiguration(new ChatConfiguration(this));

            if (extras != null) {
                extras.putSerializable("conf", getConfiguration());
                f.setArguments(extras);
            } else {
                extras = new Bundle();
                extras.putSerializable("conf", getConfiguration());
                f.setArguments(extras);
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, f).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            super.onBackPressed();

        return super.onOptionsItemSelected(item);
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    protected void setConfiguration(ChatConfiguration conf) {
        this.conf = conf;
    }

    protected ChatConfiguration getConfiguration() {
        return conf;
    }

    protected void setTitle(String title) {
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

        public Builder setTextColor(int color) {
            conf.setTextColor(color);
            return this;
        }

        public Builder setTextSize(int size) {
            conf.setTextSize(size);
            return this;
        }


    }
}
