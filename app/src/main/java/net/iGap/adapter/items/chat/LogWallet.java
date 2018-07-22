/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
 */

package net.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperRadius;
import net.iGap.interfaces.IMessageItem;
import net.iGap.module.ReserveSpaceRoundedImageView;
import net.iGap.module.enums.LocalFileType;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRegisteredInfo;

import java.util.List;

import io.realm.Realm;

import static net.iGap.module.AndroidUtils.suitablePath;

public class LogWallet extends AbstractMessage<LogWallet, LogWallet.ViewHolder> {
    private Realm mRealm;

    public LogWallet(Realm realmChat, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(realmChat, true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutLogWallet;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_log_wallet;
    }


    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        RealmRegisteredInfo mRealmRegisteredInfoFrom = RealmRegisteredInfo.getRegistrationInfo(getRealm(), mMessage.structWallet.fromUserId);
        RealmRegisteredInfo mRealmRegisteredInfoTo = RealmRegisteredInfo.getRegistrationInfo(getRealm(), mMessage.structWallet.toUserId);

        holder.fromUserId.setText("" + mRealmRegisteredInfoFrom.getDisplayName());
        holder.toUserId.setText("" + mRealmRegisteredInfoTo.getDisplayName());
        holder.amount.setText("" + mMessage.structWallet.amount);
        holder.traceNumber.setText("" + mMessage.structWallet.traceNumber);
        holder.invoiceNumber.setText("" + mMessage.structWallet.invoiceNumber);
        holder.payTime.setText("" + mMessage.structWallet.payTime);
        holder.description.setText(mMessage.structWallet.description);

        getRealm().close();
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView fromUserId;
        private TextView toUserId;
        private TextView amount;
        private TextView traceNumber;
        private TextView invoiceNumber;
        private TextView payTime;
        private TextView description;


        protected ReserveSpaceRoundedImageView image;

        public ViewHolder(View view) {
            super(view);

            fromUserId = view.findViewById(R.id.fromUserId);
            toUserId = view.findViewById(R.id.toUserId);
            amount = view.findViewById(R.id.amount);
            traceNumber = view.findViewById(R.id.traceNumber);
            invoiceNumber = view.findViewById(R.id.invoiceNumber);
            payTime = view.findViewById(R.id.payTime);
            description = view.findViewById(R.id.description);

            //image = (ReserveSpaceRoundedImageView) view.findViewById(R.id.thumbnail);
        }
    }

    private Realm getRealm() {
        if (mRealm == null || mRealm.isClosed()) {
            mRealm = Realm.getDefaultInstance();
        }
        return mRealm;
    }
}
