package com.iGap.adapter.items;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

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


        Log.i("CCCCCCC", "bindView: " + item.getAppId());


    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(View view) {
            super(view);

        }
    }
}