package net.iGap.module.dialog.account;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.model.AccountUser;
import net.iGap.module.CircleImageView;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.observers.interfaces.OnComplete;
import net.iGap.request.RequestUserInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AccountsDialogAdapter extends RecyclerView.Adapter<AccountsDialogAdapter.AccountViewHolder> {

    private List<AccountUser> mAccountsList;
    private AccountDialogListener mListener;
    private AvatarHandler mAvatarHandler;
    private int currentUserPosition;

    public AccountsDialogAdapter(AvatarHandler mAvatarHandler, AccountDialogListener listener) {
        this.mAccountsList = new ArrayList<>();
        this.mAccountsList.addAll(AccountManager.getInstance().getUserAccountList());
        Collections.reverse(mAccountsList);
        this.mAvatarHandler = mAvatarHandler;
        this.currentUserPosition = mAccountsList.indexOf(AccountManager.getInstance().getCurrentUser());
        this.mListener = listener;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AccountViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_dilog_account, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {

        holder.username.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);
        if (currentUserPosition == position) {
            holder.currentUserView.setVisibility(View.VISIBLE);
        } else {
            holder.currentUserView.setVisibility(View.INVISIBLE);
        }
        if (mAccountsList.get(position).isAssigned()) {
            holder.getOtherAccountUserId(mAccountsList.get(position).getId());
            mAvatarHandler.getAvatar(new ParamWithAvatarType(holder.userAvatar, mAccountsList.get(position).getId()).avatarType(AvatarHandler.AvatarType.USER).showMain(), true);
            holder.username.setText(mAccountsList.get(position).getName());
            holder.messageUnreadCount.setVisibility(View.VISIBLE);
            String t;
            if (mAccountsList.get(position).getUnReadMessageCount() > 99) {
                t = "+99";
                holder.messageUnreadCount.setVisibility(View.VISIBLE);
            } else if (mAccountsList.get(position).getUnReadMessageCount() == 0) {
                t = "";
                holder.messageUnreadCount.setVisibility(View.INVISIBLE);
            } else {
                t = String.valueOf(mAccountsList.get(position).getUnReadMessageCount());
                holder.messageUnreadCount.setVisibility(View.VISIBLE);
            }
            holder.messageUnreadCount.setText(t);
        } else {
            holder.userAvatar.setImageResource(R.drawable.add_chat_background);
            holder.username.setText(R.string.add_new_account);
            holder.messageUnreadCount.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setOnClickListener(v -> mListener.onAccountClick(mAccountsList.get(holder.getAdapterPosition()).isAssigned(), mAccountsList.get(holder.getAdapterPosition()).getId()));
    }

    @Override
    public int getItemCount() {
        return Math.min(mAccountsList.size(), 3);
    }

    class AccountViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView userAvatar;
        private AppCompatTextView username;
        private AppCompatTextView messageUnreadCount;
        private View currentUserView;

        AccountViewHolder(@NonNull View itemView) {
            super(itemView);

            userAvatar = itemView.findViewById(R.id.avatar);
            username = itemView.findViewById(R.id.name);
            messageUnreadCount = itemView.findViewById(R.id.unreadMessageCount);
            currentUserView = itemView.findViewById(R.id.checked);
        }

        private void getOtherAccountUserId(long userId) {
            if (userId != AccountManager.getInstance().getCurrentUser().getId()) {
                new RequestUserInfo().userInfoWithCallBack(new OnComplete() {
                    @Override
                    public void complete(boolean result, String messageOne, String MessageTow) {
                        mAvatarHandler.getAvatar(new ParamWithAvatarType(userAvatar, userId).avatarType(AvatarHandler.AvatarType.USER).showMain(), false);
                    }
                }, userId, userId + "");
            }
        }
    }
}
