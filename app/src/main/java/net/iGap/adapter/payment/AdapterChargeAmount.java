package net.iGap.adapter.payment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;

public class AdapterChargeAmount extends RecyclerView.Adapter<AdapterChargeAmount.ChargeAmountViewHolder> {

    @NonNull
    @Override
    public ChargeAmountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_charge_type, parent, false);
        return new ChargeAmountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChargeAmountViewHolder holder, int position) {
        holder.bindAmount();
    }

    @Override
    public int getItemCount() {
        return 8;
    }

    public class ChargeAmountViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ChargeAmountViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.amount);
        }

        public void bindAmount() {
            textView.setText("50000 ریال");
        }

    }
}
