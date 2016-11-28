package org.taitascioredev.android.adachat;

import java.util.Date;

/**
 * Created by roberto on 23/08/16.
 */
public class Mensaje implements Comparable {

    private String key;
    private String mensaje;
    private int idSender;
    private int idReceiver;
    private boolean visto;
    private Date time;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public int getIdSender() {
        return idSender;
    }

    public void setIdSender(int idSender) {
        this.idSender = idSender;
    }

    public int getIdReceiver() {
        return idReceiver;
    }

    public void setIdReceiver(int idReceiver) {
        this.idReceiver = idReceiver;
    }

    public boolean isVisto() {
        return visto;
    }

    public void setVisto(boolean visto) {
        this.visto = visto;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public int compareTo(Object o) {
        Mensaje that = (Mensaje) o;
        return this.getTime().compareTo(that.getTime());
    }
}
