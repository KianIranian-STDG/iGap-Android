package com.iGap.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iGap.R;

import java.util.ArrayList;

/**
 *
 */
public class ContactNamesAdapter extends RecyclerView.Adapter<CountryViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0x01;

    private static final int VIEW_TYPE_CONTENT = 0x00;

    private ArrayList<LineItem> mItems = new ArrayList<>();

    private final Context mContext;

    public ContactNamesAdapter(Context context, ArrayList<LineItem> _mItems) {
        mContext = context;
       this.mItems = _mItems;

    }

    @Override
    public CountryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.header_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.text_line_item, parent, false);
        }
        return new CountryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CountryViewHolder holder, int position) {
        final LineItem item = mItems.get(position);
        final View itemView = holder.itemView;


        try {
            holder.bindItem( item.text, item.Status);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).isHeader ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    public static class LineItem {

        public int sectionManager;

        public int sectionFirstPosition;

        public boolean isHeader;

        public String text;

        public String Status;

        public LineItem( String text, String status, boolean isHeader, int sectionManager,
                int sectionFirstPosition) {
            this.isHeader = isHeader;
            this.text = text;
            this.Status = status;
            this.sectionManager = sectionManager;
            this.sectionFirstPosition = sectionFirstPosition;

        }
    }
}
