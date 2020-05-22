package net.iGap.adapter.payment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.model.paymentPackage.FavoriteNumber;

import java.util.List;


public class AdapterHistoryPackage extends RecyclerView.Adapter<AdapterHistoryPackage.ContactNumberViewHolder> {
    private List<FavoriteNumber> historyNumberList;
    private int selectedPosition = -1;

    public AdapterHistoryPackage(List<FavoriteNumber> historyNumberList) {
        this.historyNumberList = historyNumberList;
    }

    @NonNull
    @Override
    public ContactNumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_pakage_history, parent, false);
        return new ContactNumberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactNumberViewHolder holder, int position) {
        holder.bindNUmber(historyNumberList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return historyNumberList.size();
    }

    class ContactNumberViewHolder extends RecyclerView.ViewHolder {
        private TextView detailTextView;
        private TextView phoneNumberTextView;

        ContactNumberViewHolder(@NonNull View itemView) {
            super(itemView);
            phoneNumberTextView = itemView.findViewById(R.id.tv_itemInternetPackage_number);
            detailTextView = itemView.findViewById(R.id.tv_itemInternetPackage_detail);
        }

        void bindNUmber(FavoriteNumber historyNumber, int position) {
            phoneNumberTextView.setText(historyNumber.getPhoneNumber());
            detailTextView.setText(historyNumber.getPackageDescription());
            itemView.setSelected(selectedPosition == position);
            itemView.setOnClickListener(v -> {
                notifyItemChanged(selectedPosition);
                selectedPosition = getAdapterPosition();
                notifyItemChanged(selectedPosition);
            });
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public List<FavoriteNumber> getHistoryNumberList() {
        return historyNumberList;
    }
}
