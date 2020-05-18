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


public class AdapterHistoryNumber extends RecyclerView.Adapter<AdapterHistoryNumber.ContactNumberViewHolder> {
    private List<FavoriteNumber> historyNumberList;
    private int selectedPosition = -1;

    public AdapterHistoryNumber(List<FavoriteNumber> historyNumberList) {
        this.historyNumberList = historyNumberList;
    }

    @NonNull
    @Override
    public ContactNumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_charge_history, parent, false);
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

    public class ContactNumberViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ContactNumberViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.number_contact);
        }

        public void bindNUmber(FavoriteNumber historyNumber, int position) {
            textView.setText(historyNumber.getPhoneNumber());
            itemView.setSelected(selectedPosition == position);
            itemView.setOnClickListener(v -> {
                int tmp = selectedPosition;
                notifyItemChanged(tmp);
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
