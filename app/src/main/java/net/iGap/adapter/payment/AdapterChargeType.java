package net.iGap.adapter.payment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.downloader.PRDownloader;

import net.iGap.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterChargeType extends RecyclerView.Adapter<AdapterChargeType.ChargeTypeViewHolder> {

    private List<Amount> amountList ;
    private int selectedPosition = -1;

    public AdapterChargeType(List<Amount> amountList) {
        this.amountList = amountList;
        Amount amount = new Amount();
        amount.setTextAmount("شارژ ساده");
        amountList.add(amount);
        Amount amount1 = new Amount();
        amount1.setTextAmount("شگفت انگیز");
        amountList.add(amount1);
        Amount amount2 = new Amount();
        amount2.setTextAmount("مستقیم");
        amountList.add(amount2);
    }

    @NonNull
    @Override
    public ChargeTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_charge_type, parent, false);
        return new ChargeTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChargeTypeViewHolder holder, int position) {
        holder.bindChargeType(amountList.get(position));
    }

    @Override
    public int getItemCount() {
        return amountList.size();
    }

    public class ChargeTypeViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ChargeTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.amount);
            itemView.setOnClickListener(v -> {
                selectedPosition = getAdapterPosition();
                notifyDataSetChanged();
            });
        }

        public void bindChargeType(Amount amount) {
            textView.setText(amount.getTextAmount());
        }

    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}

