package net.iGap.adapter.payment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;

public class AdapterHistoryNumber extends RecyclerView.Adapter<AdapterHistoryNumber.HistoryNumberViewHolder> {

    @NonNull
    @Override
    public HistoryNumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_charge_contact, parent, false);
        return new HistoryNumberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryNumberViewHolder holder, int position) {
        holder.bindNumber();
    }

    @Override
    public int getItemCount() {
        return 30;
    }


    public class HistoryNumberViewHolder extends RecyclerView.ViewHolder {
        private TextView phoneNumber;
        private TextView contactName;

        public HistoryNumberViewHolder(@NonNull View itemView) {
            super(itemView);
            phoneNumber = itemView.findViewById(R.id.phone_Number);
            contactName = itemView.findViewById(R.id.contact_Name);

        }

        public void bindNumber() {
            phoneNumber.setText("091212345678");
            contactName.setText("نازنین عمرانی");
        }
    }
}
