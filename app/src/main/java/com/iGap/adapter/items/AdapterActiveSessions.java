package com.iGap.adapter.items;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.iGap.R;
import com.iGap.module.StructSessionsGetActiveList;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;


public class AdapterActiveSessions extends AbstractItem<AdapterActiveSessions, AdapterActiveSessions.ViewHolder> {

    public StructSessionsGetActiveList item;

    public AdapterActiveSessions(StructSessionsGetActiveList item) {
        this.item = item;
    }

    public void setItem(StructSessionsGetActiveList item) {
        this.item = item;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.aas_rootLayout;
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

        holder.txtCurrentSession.setText("current session");
        holder.txtVersionIgap.setText("iGap Android " + item.getAppVersion());
        holder.txtDeviceModel.setText("" + item.getDeviceName());
        Log.i("CCCCCCCDD", "getBuildVersion: " + item.getBuildVersion());
        holder.txtIp.setText("" + item.getIp());
        holder.txtAndroidSDK.setText(item.getPlatform() + " SDK " + item.getPlatformVersion());
        holder.txtLanguage.setText("" + item.getLanguage().toString());
        Log.i("CCCCCCCDD", "isCurrent: " + item.isCurrent());
        if (item.isCurrent()) {
            holder.txtOnline.setText("online");
        } else {
            holder.txtOnline.setText("offline");
        }
//        Log.i("CCCCCCCDD", "getSessionId: " + item.getSessionId());
//        Log.i("CCCCCCCDD", "getName: " + item.getName());
//        Log.i("CCCCCCCDD", "getAppId: " + item.getAppId());
//        Log.i("CCCCCCCDD", "getBuildVersion: " + item.getBuildVersion());
//        Log.i("CCCCCCCDD", "getAppVersion: " + item.getAppVersion());
//        Log.i("CCCCCCCDD", "getPlatform: " + item.getPlatform());
//        Log.i("CCCCCCCDD", "getPlatformVersion: " + item.getPlatformVersion());
//        Log.i("CCCCCCCDD", "getDevice: " + item.getDevice());
//        Log.i("CCCCCCCDD", "getDeviceName: " + item.getDeviceName());
//        Log.i("CCCCCCCDD", "getLanguage: " + item.getLanguage());
//        Log.i("CCCCCCCDD", "getCountry: " + item.getCountry());
//        Log.i("CCCCCCCDD", "getCreateTime: " + item.getCreateTime());
//        Log.i("CCCCCCCDD", "getActiveTime: " + item.getActiveTime());
//        Log.i("CCCCCCCDD", "getIp: " + item.getIp());


    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        TextView txtCurrentSession;
        TextView txtVersionIgap;
        TextView txtDeviceModel;
        TextView txtAndroidSDK;
        TextView txtIp;
        TextView txtLanguage;
        TextView txtOnline;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.adp_cardView);
            txtCurrentSession = (TextView) view.findViewById(R.id.adp_currentSession);
            txtVersionIgap = (TextView) view.findViewById(R.id.adp_versionIgap);
            txtDeviceModel = (TextView) view.findViewById(R.id.adp_deviceModel);
            txtAndroidSDK = (TextView) view.findViewById(R.id.adp_androidSDK);
            txtIp = (TextView) view.findViewById(R.id.adp_ip);
            txtLanguage = (TextView) view.findViewById(R.id.adp_language);
            txtOnline = (TextView) view.findViewById(R.id.adp_online);
        }
    }
}