package com.iGap.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iGap.R;
import com.iGap.adapter.items.ContactItem;
import com.iGap.module.CustomTextViewMedium;
import com.mikepenz.fastadapter.AbstractAdapter;
import com.mikepenz.fastadapter.IItem;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

public class StickyHeaderAdapter extends AbstractAdapter implements StickyRecyclerHeadersAdapter {
    @Override
    public long getHeaderId(int position) {
        IItem item = getItem(position);

        //we want a separate header per first letter of our items
        if (item instanceof ContactItem && ((ContactItem) item).mContact != null) {
            return ((ContactItem) item).mContact.displayName.charAt(0);
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        //we create the view for the header
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_header_item, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        CustomTextViewMedium textView = (CustomTextViewMedium) holder.itemView;

        IItem item = getItem(position);
        if (item instanceof ContactItem && ((ContactItem) item).mContact != null) {
            //based on the position we set the headers text
            textView.setText(String.valueOf(((ContactItem) item).mContact.displayName.charAt(0)));
        }
    }

    /**
     * REQUIRED FOR THE FastAdapter. Set order to < 0 to tell the FastAdapter he can ignore this one.
     *
     * @return int
     */
    @Override
    public int getOrder() {
        return -100;
    }

    @Override
    public int getAdapterItemCount() {
        return 0;
    }

    @Override
    public List<IItem> getAdapterItems() {
        return null;
    }

    @Override
    public IItem getAdapterItem(int position) {
        return null;
    }

    @Override
    public int getAdapterPosition(IItem item) {
        return -1;
    }

    @Override
    public int getGlobalPosition(int position) {
        return -1;
    }
}