package net.iGap.mobileBank.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.mobileBank.repository.model.MobileBankHomeItemsModel;

import java.util.ArrayList;
import java.util.List;

public class BankHomeItemAdapter extends RecyclerView.Adapter<BankHomeItemAdapter.ViewHolderItems> {

    private MobileBankHomeItemsListener listener;
    private List<MobileBankHomeItemsModel> items = new ArrayList<>();

    public void setListener(MobileBankHomeItemsListener listener) {
        this.listener = listener;
    }

    public void setItems(List<MobileBankHomeItemsModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolderItems onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderItems(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_mobile_bank_home_item , parent , false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderItems holder, int position) {
        holder.tvTitle.setText(items.get(position).getTitle());
        holder.tvIcon.setText(items.get(position).getIcon());
        holder.root.setOnClickListener(v -> listener.onItemClicked(holder.getAdapterPosition() , items.get(holder.getAdapterPosition()).getTitle()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolderItems extends RecyclerView.ViewHolder{

        private TextView tvTitle , tvIcon ;
        private View root;

        ViewHolderItems(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvIcon = itemView.findViewById(R.id.tvIcon);
            root = itemView.findViewById(R.id.root);
        }
    }

    public interface MobileBankHomeItemsListener{
        void onItemClicked(int position , int title);
    }

}
