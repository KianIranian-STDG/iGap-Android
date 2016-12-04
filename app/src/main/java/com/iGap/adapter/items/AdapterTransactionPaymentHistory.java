package com.iGap.adapter.items;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iGap.R;
import com.iGap.fragments.FragmentTransactionPaymentHistory;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.List;


public class AdapterTransactionPaymentHistory extends AbstractItem<AdapterTransactionPaymentHistory, AdapterTransactionPaymentHistory.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();
    public FragmentTransactionPaymentHistory.StructHistotyPayment item;

    public AdapterTransactionPaymentHistory setContact(FragmentTransactionPaymentHistory.StructHistotyPayment item) {
        this.item = item;
        return this;
    }

    @Override
    public int getType() {
        return R.id.ftphs_txt_time1;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_transaction_payment_history_sub_layout;
    }

    @Override
    public void bindView(ViewHolder h, List payloads) {
        super.bindView(h, payloads);

        h.txtTime1.setText(item.time1);
        h.txtTime2.setText(item.time2);
        h.txtPrice.setText(item.price);
        h.txtComment.setText(item.comment);

        switch (item.pyamentAction) {
            case pending:
                h.txtCondition.setText("Pending");
                h.imvCircle.setImageResource(R.drawable.emoji_00a9);
                break;
            case completed:
                h.txtCondition.setText("Completed");
                h.imvCircle.setImageResource(R.drawable.circle_color_notificatin_setting);
                break;
        }


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


        protected TextView txtTime1;
        protected TextView txtTime2;
        protected ImageView imvCircle;
        protected TextView txtPrice;
        protected TextView txtComment;
        protected TextView txtCondition;
        protected com.iGap.module.MaterialDesignTextView txtRightArrowIcon;

        public ViewHolder(View view) {
            super(view);

            txtTime1 = (TextView) view.findViewById(R.id.ftphs_txt_time1);
            txtTime2 = (TextView) view.findViewById(R.id.ftphs_txt_time2);
            imvCircle = (ImageView) view.findViewById(R.id.ftphs_imv_circle);
            txtPrice = (TextView) view.findViewById(R.id.ftphs_txt_price);
            txtComment = (TextView) view.findViewById(R.id.ftphs_txt_comment);
            txtCondition = (TextView) view.findViewById(R.id.ftphs_txt_condition);
            txtCondition = (TextView) view.findViewById(R.id.ftphs_txt_condition);
            txtRightArrowIcon = (com.iGap.module.MaterialDesignTextView) view.findViewById(R.id.ftphs_txt_icon_right_arrow);
        }
    }
}


