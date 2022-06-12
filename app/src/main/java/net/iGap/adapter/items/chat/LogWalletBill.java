/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.adapter.items.chat;

import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.helper.HelperCalander;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.CustomTextViewMedium;
import net.iGap.module.ReserveSpaceRoundedImageView;
import net.iGap.module.TimeUtils;
import net.iGap.observers.interfaces.IMessageItem;
import net.iGap.proto.ProtoGlobal;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LogWalletBill extends AbstractMessage<LogWalletBill, LogWalletBill.ViewHolder> {

    public LogWalletBill(MessagesAdapter<AbstractMessage> mAdapter, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(mAdapter, true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.ll_bill;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_log_bill;
    }


    @Override
    public void bindView(final ViewHolder holder, List payloads) {// TODO: 12/29/20 MESSAGE_REFACTOR_NEED_TEST
        super.bindView(holder, payloads);
            String amount = String.valueOf(messageObject.wallet.billObject.amount);
            String billId = messageObject.wallet.billObject.billId;
            String payId = messageObject.wallet.billObject.payId;
            String cardNumber = messageObject.wallet.billObject.cardNumber;
            String orderId = String.valueOf(messageObject.wallet.billObject.orderId);
            String terminalNo = String.valueOf(messageObject.wallet.billObject.terminalNo);
            String rrn = String.valueOf(messageObject.wallet.billObject.rrn);
            String traceNumber = String.valueOf(messageObject.wallet.billObject.traceNumber);
            String persianCalender = HelperCalander.checkHijriAndReturnTime(messageObject.wallet.billObject.requestTime) + " " + "-" + " " + TimeUtils.toLocal(messageObject.wallet.billObject.requestTime * DateUtils.SECOND_IN_MILLIS, G.CHAT_MESSAGE_TIME);

            if (HelperCalander.isPersianUnicode) {
                amount = HelperCalander.convertToUnicodeFarsiNumber(amount);
                billId = HelperCalander.convertToUnicodeFarsiNumber(billId);
                payId = HelperCalander.convertToUnicodeFarsiNumber(payId);
                cardNumber = HelperCalander.convertToUnicodeFarsiNumber(cardNumber);
                orderId = HelperCalander.convertToUnicodeFarsiNumber(orderId);
                terminalNo = HelperCalander.convertToUnicodeFarsiNumber(terminalNo);
                rrn = HelperCalander.convertToUnicodeFarsiNumber(rrn);
                traceNumber = HelperCalander.convertToUnicodeFarsiNumber(traceNumber);
                persianCalender = HelperCalander.convertToUnicodeFarsiNumber(persianCalender);
            }

            holder.amount.setText(amount);
            holder.billType.setText(messageObject.wallet.billObject.billType);
            holder.billId.setText(billId);
            holder.payId.setText(payId);
            holder.cardNumber.setText(cardNumber);
            holder.orderId.setText(orderId);
            holder.terminalNo.setText(terminalNo);
            holder.rrn.setText(rrn);
            holder.traceNumber.setText(traceNumber);
            holder.requestTime.setText(persianCalender);

    }

    @NotNull
    @Override
    public ViewHolder getViewHolder(@NotNull View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView amount;
        private final TextView billType;
        private final TextView billId;
        private final TextView payId;
        private final TextView orderId;
        private final TextView cardNumber;
        private final TextView terminalNo;
        private final TextView rrn;
        private final TextView traceNumber;
        private final TextView requestTime;

        protected ReserveSpaceRoundedImageView image;

        public ViewHolder(View view) {
            super(view);
            TextView amountText = view.findViewById(R.id.amount);
            amount = view.findViewById(R.id.tv_bill_amount);
            TextView billTypeText = view.findViewById(R.id.billType);
            billType = view.findViewById(R.id.tv_bill_billType);
            TextView billIdText = view.findViewById(R.id.billId);
            billId = view.findViewById(R.id.tv_bill_billId);
            TextView payIdText = view.findViewById(R.id.payId);
            payId = view.findViewById(R.id.tv_bill_payId);
            TextView orderIdText = view.findViewById(R.id.orderId);
            orderId = view.findViewById(R.id.tv_bill_orderId);
            TextView cardNumberText = view.findViewById(R.id.cardNumber);
            cardNumber = view.findViewById(R.id.tv_bill_cardNumber);
            TextView terminalNoText = view.findViewById(R.id.terminalNo);
            terminalNo = view.findViewById(R.id.tv_bill_terminalNo);
            TextView rrnText = view.findViewById(R.id.referenceNo);
            rrn = view.findViewById(R.id.tv_bill_referenceNo);
            TextView traceNumberText = view.findViewById(R.id.traceNumber);
            traceNumber = view.findViewById(R.id.tv_bill_traceNumber);
            requestTime = view.findViewById(R.id.tv_bill_requestDate);
        }
    }
}