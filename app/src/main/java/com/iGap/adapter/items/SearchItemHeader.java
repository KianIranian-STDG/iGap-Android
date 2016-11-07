package com.iGap.adapter.items;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.iGap.R;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.List;

public class SearchItemHeader extends AbstractItem<SearchItemHeader, SearchItemHeader.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();
    public String text;

    public SearchItemHeader setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public int getType() {
        return R.id.sfslh_txt_header_text;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.search_fragment_sub_layout_header;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        holder.txtHeader.setText(text);
    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView txtHeader;

        public ViewHolder(View view) {
            super(view);
            txtHeader = (TextView) view.findViewById(R.id.sfslh_txt_header_text);
        }
    }
}


