package org.taitascioredev.android.adachat;

import android.app.Activity;

import java.io.Serializable;

/**
 * Created by roberto on 28/11/16.
 */

public class ChatConfiguration implements Serializable {

    private int senderBgColor;
    private int receiverBgColor;
    private int textSize;
    private int textColor;

    private Activity context;

    public ChatConfiguration(Activity context) {
        senderBgColor   = R.color.colorPrimary;
        receiverBgColor = R.color.colorAccent;
        textSize        = 16;
        textColor       = android.R.color.white;
    }

    public int getSenderBgColor() {
        return senderBgColor;
    }

    public void setSenderBgColor(int senderBgColor) {
        this.senderBgColor = senderBgColor;
    }

    public int getReceiverBgColor() {
        return receiverBgColor;
    }

    public void setReceiverBgColor(int receiverBgColor) {
        this.receiverBgColor = receiverBgColor;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
