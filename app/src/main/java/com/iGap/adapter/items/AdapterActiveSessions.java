/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.adapter.items;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.iGap.G;
import com.iGap.R;
import com.iGap.helper.HelperCalander;
import com.iGap.module.StructSessionsGetActiveList;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import java.util.List;

import static com.iGap.R.id.adp_currentSession;
import static com.iGap.R.id.adp_rootLayout;


public class AdapterActiveSessions extends AbstractItem<AdapterActiveSessions, AdapterActiveSessions.ViewHolder> {

    public StructSessionsGetActiveList item;

    public StructSessionsGetActiveList getItem() {
        return item;
    }

    public AdapterActiveSessions(StructSessionsGetActiveList item) {
        this.item = item;
    }

    public void setItem(StructSessionsGetActiveList item) {
        this.item = item;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return adp_rootLayout;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.adapter_active_sessions;
    }

    //The logic to bind your data to the view

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);


        if (item.isCurrent()) {
            holder.txtCurrentSession.setText(G.context.getResources().getString(R.string.current_session));
            holder.vgRootLayout.setBackgroundColor(G.context.getResources().getColor(android.R.color.white));
            holder.line.setVisibility(View.GONE);
            holder.txtTerminate.setVisibility(View.GONE);

        } else {
            holder.txtCurrentSession.setText(G.context.getResources().getString(R.string.Active_session));
            holder.vgRootLayout.setBackgroundColor(G.context.getResources().getColor(R.color.st_background2));
            holder.line.setVisibility(View.VISIBLE);
            holder.txtTerminate.setVisibility(View.VISIBLE);
        }

        holder.txtDevice.setText("" + item.getDeviceName());
        holder.txtPlatform.setText("" + item.getPlatform());
        holder.txtCountry.setText("" + item.getCountry());
        holder.txtIp.setText("" + item.getIp());

        String changeTime = HelperCalander.checkHijriAndReturnTime(item.getActiveTime());

        holder.txtCreateTime.setText("" + changeTime);


//        holder.vgRootLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (item.isCurrent()){
//
//                }else {
//                    Toast.makeText(G.context, "active", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private ViewGroup vgRootLayout;
        private TextView txtCurrentSession;
        private TextView txtDevice;
        private TextView txtPlatform;
        private TextView txtCountry;
        private TextView txtIp;
        private TextView txtCreateTime;
        private TextView txtTerminate;
        private View line;

        public ViewHolder(View view) {
            super(view);
            vgRootLayout = (ViewGroup) view.findViewById(adp_rootLayout);
            txtCurrentSession = (TextView) view.findViewById(adp_currentSession);
            txtDevice = (TextView) view.findViewById(R.id.adp_device);
            txtPlatform = (TextView) view.findViewById(R.id.adp_platform);
            txtCountry = (TextView) view.findViewById(R.id.adp_country);
            txtIp = (TextView) view.findViewById(R.id.adp_ip);
            txtCreateTime = (TextView) view.findViewById(R.id.adp_create_time);
            txtTerminate = (TextView) view.findViewById(R.id.adp_terminate);
            line = view.findViewById(R.id.adp_line);

        }
    }

    //the static ViewHolderFactory which will be used to generate the ViewHolder for this Item
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    /**
     * our ItemFactory implementation which creates the ViewHolder for our adapter.
     * It is highly recommended to implement a ViewHolderFactory as it is 0-1ms faster for ViewHolder creation,
     * and it is also many many times more efficient if you define custom listeners on views within your item.
     */
    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    /**
     * return our ViewHolderFactory implementation here
     */
    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }
}