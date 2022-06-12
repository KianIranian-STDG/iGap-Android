/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.adapter.items;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.items.AbstractItem;

import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.structs.StructSessions;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AdapterActiveSessions extends AbstractItem<AdapterActiveSessions, AdapterActiveSessions.ViewHolder> {

    public StructSessions item;

    public AdapterActiveSessions(StructSessions item) {
        this.item = item;
    }

    public StructSessions getItem() {
        return item;
    }

    public void setItem(StructSessions item) {
        this.item = item;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.adp_rootLayout;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.adapter_active_sessions;
    }

    //The logic to bind your data to the view

    @Override
    public void bindView(@NotNull ViewHolder holder, @NotNull List payloads) {
        super.bindView(holder, payloads);
        holder.txtTerminate.setTextColor(Theme.getColor(Theme.key_red));
        if (item.isCurrent()) {
            holder.txtCurrentSession.setText(R.string.current_session);
            holder.txtTerminate.setVisibility(View.GONE);
        } else {
            holder.txtCurrentSession.setText(R.string.Active_session);
            holder.txtTerminate.setVisibility(View.VISIBLE);
        }

        holder.deviceInfo.setText(String.format("%s , %s", item.getDeviceName(), item.getPlatform()));
        holder.country.setText(item.getCountry());
        holder.ip.setText(item.getIp());
        holder.createTime.setText(HelperCalander.checkHijriAndReturnTime(item.getCreateTime()));
        holder.activeTime.setText(HelperCalander.checkHijriAndReturnTime(item.getActiveTime()));
    }

    @NotNull
    @Override
    public ViewHolder getViewHolder(@NotNull View v) {
        return new ViewHolder(v);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private ViewGroup vgRootLayout;
        private TextView txtCurrentSession;
        private AppCompatTextView deviceInfo;
        private AppCompatTextView country;
        private AppCompatTextView ip;
        private AppCompatTextView activeTime;
        private AppCompatTextView createTime;
        private TextView txtTerminate;

        public ViewHolder(View view) {
            super(view);
            vgRootLayout = view.findViewById(R.id.adp_rootLayout);
            txtCurrentSession = view.findViewById(R.id.currentSessionTitle);
            deviceInfo = view.findViewById(R.id.deviceInfo);
            country = view.findViewById(R.id.country);
            ip = view.findViewById(R.id.ip);
            activeTime = view.findViewById(R.id.activeTime);
            createTime = view.findViewById(R.id.createTime);
            txtTerminate = view.findViewById(R.id.terminate);
        }
    }
}