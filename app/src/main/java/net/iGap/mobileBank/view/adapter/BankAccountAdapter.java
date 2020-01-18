package net.iGap.mobileBank.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import net.iGap.R;
import net.iGap.Theme;
import net.iGap.mobileBank.repository.model.BankAccountModel;

import java.util.ArrayList;
import java.util.List;

public class BankAccountAdapter extends RecyclerView.Adapter<BankAccountAdapter.AccountViewHolder>{

    private List<BankAccountModel> items = new ArrayList<>();
    private BankAccountModel selectedAccount ;

    public BankAccountModel getSelectedAccount() {
        return selectedAccount;
    }

    public void setItems(List<BankAccountModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AccountViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_bank_account , parent , false));
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {

        if (items.get(position).isSelected()){
            holder.root.setCardBackgroundColor(Theme.getInstance().getDividerColor(holder.root.getContext()));
        }else {
            holder.root.setCardBackgroundColor(Theme.getInstance().getCardViewColor(holder.root.getContext()));
        }

        holder.tvNumber.setText(items.get(position).getAccountNumber());

        holder.root.setOnClickListener(v -> {
            if (items.get(holder.getAdapterPosition()).isSelected()){
                selectedAccount = null;
                items.get(holder.getAdapterPosition()).setSelected(false);
                notifyDataSetChanged();
            }else {
                selectedAccount = items.get(holder.getAdapterPosition());
                for (BankAccountModel item : items) item.setSelected(false);
                items.get(holder.getAdapterPosition()).setSelected(true);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class AccountViewHolder extends RecyclerView.ViewHolder{

        private MaterialCardView root;
        private TextView tvNumber ;

        AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            tvNumber = itemView.findViewById(R.id.tvNumber);
        }
    }
}
