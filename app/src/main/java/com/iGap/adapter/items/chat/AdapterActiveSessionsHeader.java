package com.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.iGap.R;
import com.iGap.module.StructSessionsGetActiveList;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;


public class AdapterActiveSessionsHeader extends AbstractItem<AdapterActiveSessionsHeader, AdapterActiveSessionsHeader.ViewHolder> {

    public StructSessionsGetActiveList item;

    private List<StructSessionsGetActiveList> itemList;

    public List<StructSessionsGetActiveList> getItem() {
        return itemList;
    }

    public AdapterActiveSessionsHeader(List<StructSessionsGetActiveList> item) {
        itemList = item;
    }

    public void setItem(List<StructSessionsGetActiveList> item) {
        this.itemList = item;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.adph_rootLayout;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.adapter_active_sessions_header;
    }

    //The logic to bind your data to the view

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

//        holder.root.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////
//                for (int i = 0; i < itemList.size(); i++) {
//                    if (!itemList.get(i).isCurrent()) {
//                        new RequestUserSessionTerminate().userSessionTerminate(itemList.get(i).getSessionId());
//
//                    }
//                }
//            }
//        });

    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private ViewGroup root;

        public ViewHolder(View view) {
            super(view);

            root = (ViewGroup) view.findViewById(R.id.adph_rootLayout);
        }
    }
}