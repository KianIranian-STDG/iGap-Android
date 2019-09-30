package net.iGap.dialog.account;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.RowDilogAccountBinding;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.model.AccountModel;

import java.util.ArrayList;
import java.util.List;

public class AccountsDialogAdapter extends RecyclerView.Adapter<AccountsDialogAdapter.AccountViewHolder> {

    private List<AccountModel> mAccountsList = new ArrayList<>();
    private AccountDialogListener mListener;
    private AvatarHandler mAvatarHandler;

    public void setAccountsList(List<AccountModel> accounts) {

        this.mAccountsList.addAll(accounts);

        //if account count was 2 or 1 , add blank account to show add new view
        if (mAccountsList.size() != 3) {
            mAccountsList.add(new AccountModel(true));
        }

        notifyDataSetChanged();
    }

    public void setListener(AccountDialogListener listener) {
        this.mListener = listener;
    }

    public void setAvatarHandler(AvatarHandler avatarHandler) {
        this.mAvatarHandler = avatarHandler;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowDilogAccountBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_dilog_account, parent, false);
        return new AccountViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        holder.bindView(mAccountsList.get(holder.getAdapterPosition()));

    }

    @Override
    public int getItemCount() {
        return mAccountsList.size();
    }

    class AccountViewHolder extends RecyclerView.ViewHolder {

        RowDilogAccountBinding binding;

        public AccountViewHolder(@NonNull RowDilogAccountBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

        public void bindView(AccountModel account) {
            binding.setAccount(account);
            binding.setIsRtl(G.isAppRtl);
            binding.executePendingBindings();

            if (account.isAddNew()) {
                binding.avatar.setImageResource(R.drawable.add_chat_background);
            } else {
                mAvatarHandler.getAvatar(new ParamWithAvatarType(binding.avatar, account.getId()).avatarType(AvatarHandler.AvatarType.USER).showMain());
            }

            binding.root.setOnClickListener(v -> {
                if (account.isAddNew()) {
                    mListener.onNewAccountClick();
                } else {
                    mListener.onAccountClick(account.getId());
                }
            });

        }
    }
}
