package net.iGap.adapter.payment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;

import java.util.List;

public class AdapterContactNumber extends RecyclerView.Adapter<AdapterContactNumber.HistoryNumberViewHolder> {
    private List<Amount> amountList;
    private int selectedPosition;

    public AdapterContactNumber(List<Amount> amountList) {
        this.amountList = amountList;
        Amount amount = new Amount();
        amount.setTextAmount("نازنین عمرانی");
        amountList.add(amount);
        Amount amount1 = new Amount();
        amount1.setTextAmount("عباسی");
        amountList.add(amount1);
        Amount amount2 = new Amount();
        amount2.setTextAmount("بگوند");
        amountList.add(amount2);
        Amount amount3 = new Amount();
        amount3.setTextAmount("نظری");
        amountList.add(amount3);
        Amount amount4 = new Amount();
        amount4.setTextAmount("امیری");
        amountList.add(amount4);
        Amount amount5 = new Amount();
        amount5.setTextAmount("امینی");
        amountList.add(amount5);

    }

    @NonNull
    @Override
    public HistoryNumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_charge_contact, parent, false);
        return new HistoryNumberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryNumberViewHolder holder, int position) {
        holder.bindNumber(amountList.get(position));
    }

    @Override
    public int getItemCount() {
        return amountList.size();
    }


    public class HistoryNumberViewHolder extends RecyclerView.ViewHolder {
        private TextView phoneNumber;
        private TextView contactName;

        public HistoryNumberViewHolder(@NonNull View itemView) {
            super(itemView);
            phoneNumber = itemView.findViewById(R.id.phone_Number);
            contactName = itemView.findViewById(R.id.contact_Name);

            itemView.setOnClickListener(v -> {
                selectedPosition = getAdapterPosition();
                notifyDataSetChanged();
            });

        }

        public void bindNumber(Amount amount) {
            contactName.setText(amount.getTextAmount());
            phoneNumber.setText("091212345678");
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}
