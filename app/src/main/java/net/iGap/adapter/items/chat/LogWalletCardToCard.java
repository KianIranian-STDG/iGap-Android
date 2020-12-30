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

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.module.ReserveSpaceRoundedImageView;
import net.iGap.observers.interfaces.IMessageItem;
import net.iGap.proto.ProtoGlobal;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class LogWalletCardToCard extends AbstractMessage<LogWalletCardToCard, LogWalletCardToCard.ViewHolder> {

    public LogWalletCardToCard(MessagesAdapter<AbstractMessage> mAdapter, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(mAdapter, true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutLogWalletCardToCard;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_log_wallet_card_to_card;
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {// TODO: 12/29/20 MESSAGE_REFACTOR
        super.bindView(holder, payloads);
//        RealmRoomMessageWalletCardToCard realmRoomMessageWalletCardToCard = mMessage.getRoomMessageWallet().getRealmRoomMessageWalletCardToCard();
//        holder.titleTxt.setText(R.string.CARD_TRANSFER_MONEY);
//        DbManager.getInstance().doRealmTask(realm -> {
//            RealmRegisteredInfo mRealmRegisteredInfoFrom = RealmRegisteredInfo.getRegistrationInfo(realm, realmRoomMessageWalletCardToCard.getFromUserId());
//
//            String persianCalender = HelperCalander.checkHijriAndReturnTime(realmRoomMessageWalletCardToCard.getRequestTime()) + " " + "-" + " " +
//                    TimeUtils.toLocal(realmRoomMessageWalletCardToCard.getRequestTime() * DateUtils.SECOND_IN_MILLIS, G.CHAT_MESSAGE_TIME);
//
//            String fromDisplayName = "";
//            if (mRealmRegisteredInfoFrom != null) {
//                fromDisplayName = mRealmRegisteredInfoFrom.getDisplayName();
//            }
//
//            String traceNumber = String.valueOf(realmRoomMessageWalletCardToCard.getTraceNumber());
//            String rrn = String.valueOf(realmRoomMessageWalletCardToCard.getRrn());
//            String destCardNumber = realmRoomMessageWalletCardToCard.getDestCardNumber();
//            String sourceCardNumber = realmRoomMessageWalletCardToCard.getSourceCardNumber();
//
//            if (HelperCalander.isPersianUnicode) {
//                traceNumber = HelperCalander.convertToUnicodeFarsiNumber(traceNumber);
//                rrn = HelperCalander.convertToUnicodeFarsiNumber(rrn);
//                persianCalender = HelperCalander.convertToUnicodeFarsiNumber(persianCalender);
//                destCardNumber = HelperCalander.convertToUnicodeFarsiNumber(destCardNumber);
//                sourceCardNumber = HelperCalander.convertToUnicodeFarsiNumber(sourceCardNumber);
//            }
//            holder.fromUserId.setText(fromDisplayName);
//            holder.toUserId.setText(realmRoomMessageWalletCardToCard.getCardOwnerName());
//            holder.amount.setText(DirectPayHelper.convertNumberToPriceRial(realmRoomMessageWalletCardToCard.getAmount()));
//            holder.bankName.setText(realmRoomMessageWalletCardToCard.getBankName());
//            holder.destBankName.setText(realmRoomMessageWalletCardToCard.getDestBankName());
//            holder.destCardNumber.setText(destCardNumber);
//            holder.sourceCardNumber.setText(sourceCardNumber);
//            holder.traceNumber.setText(traceNumber);
//            holder.rrn.setText(rrn);
//            holder.requestTime.setText(persianCalender);
//
//        });
    }

    @NotNull
    @Override
    public ViewHolder getViewHolder(@NotNull View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTxt;
        private TextView fromUserId;
        private TextView toUserId;
        private TextView amount;
        private TextView bankName;
        private TextView destBankName;
        private TextView traceNumber;
        private TextView sourceCardNumber;
        private TextView destCardNumber;
        private TextView rrn;
        private TextView requestTime;

        protected ReserveSpaceRoundedImageView image;

        public ViewHolder(View view) {
            super(view);
            titleTxt = view.findViewById(R.id.titleTxt);
            fromUserId = view.findViewById(R.id.fromUserId);
            toUserId = view.findViewById(R.id.toUserId);
            amount = view.findViewById(R.id.amount);
            bankName = view.findViewById(R.id.SourceBank);
            destBankName = view.findViewById(R.id.DestBank);
            destBankName = view.findViewById(R.id.DestBank);
            traceNumber = view.findViewById(R.id.traceNumber);
            sourceCardNumber = view.findViewById(R.id.cardNumber);
            destCardNumber = view.findViewById(R.id.cardNumberDest);
            rrn = view.findViewById(R.id.rrn);
            requestTime = view.findViewById(R.id.payTime);
        }
    }
}
