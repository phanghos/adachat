package org.taitascioredev.android.listeners;

import org.taitascioredev.android.adachat.Mensaje;

import java.util.List;

/**
 * Created by roberto on 05/12/16.
 */

public interface ConversationListener {

    void onSucess(List<Mensaje> list);
}
