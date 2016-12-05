package org.taitascioredev.android.adachat;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.taitascioredev.android.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by roberto on 28/11/16.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    Activity context;
    List<Mensaje> list;
    ChatConfiguration conf;

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mensaje;
        TextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            mensaje = (TextView) itemView.findViewById(R.id.tv_mensaje);
            time    = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }

    public ChatAdapter(Activity context, List<Mensaje> list, ChatConfiguration conf) {
        this.context = context;
        this.list    = list;
        this.conf    = conf;
    }

    public ChatAdapter(Activity context, ChatConfiguration conf) {
        this.context = context;
        this.list    = new ArrayList<Mensaje>();
        this.conf    = conf;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_row_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder vh, int pos) {
        Mensaje m = get(pos);
        vh.mensaje.setText(Html.fromHtml(m.getMensaje()));
        Date time = m.getTime();

        if (time != null) {
            String hora = String.format("%02d:%02d:%02d", time.getHours(), time.getMinutes(), time.getSeconds());
            if (Utils.today(time)) vh.time.setText(hora);
            else {
                String fecha = String.format("%02d/%02d", time.getDate(), time.getMonth());
                vh.time.setText(fecha + "/" + time.getYear() + " a las " + hora);
            }
        }
        Log.d("MENSAJE (ADAPTER)", m.getMensaje());

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) vh.mensaje.getLayoutParams();
        RelativeLayout.LayoutParams paramsTime = (RelativeLayout.LayoutParams) vh.time.getLayoutParams();

        if (m.getIdSender() == Utils.getLoggedUserId(context)) {
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
            paramsTime.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            paramsTime.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
            paramsTime.addRule(RelativeLayout.BELOW, R.id.tv_mensaje);

            GradientDrawable d = (GradientDrawable) ContextCompat.getDrawable(
                    context, R.drawable.background_msg_primary);
            d.setColor(ContextCompat.getColor(context, conf.getSenderBgColor()));
            vh.mensaje.setBackground(d);
        }
        else {
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            paramsTime.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);
            paramsTime.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            paramsTime.addRule(RelativeLayout.BELOW, R.id.tv_mensaje);

            GradientDrawable d = (GradientDrawable) ContextCompat.getDrawable(
                    context, R.drawable.background_msg_accent);
            d.setColor(ContextCompat.getColor(context, conf.getReceiverBgColor()));
            vh.mensaje.setBackground(d);
        }

        vh.mensaje.setTextColor(ContextCompat.getColor(context, conf.getTextColor()));
        vh.mensaje.setTextSize(conf.getTextSize());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Mensaje get(int pos) {
        return list.get(pos);
    }

    public void add(Mensaje m) {
        list.add(m);
        notifyItemInserted(getItemCount());
    }
}
