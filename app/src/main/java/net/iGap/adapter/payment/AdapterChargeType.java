package net.iGap.adapter.payment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.model.paymentPackage.TopupChargeType;

import java.util.List;

public class AdapterChargeType extends RecyclerView.Adapter<AdapterChargeType.ChargeTypeViewHolder> {

    private List<TopupChargeType> chargeTypes;
    private int selectedPosition;

    public AdapterChargeType(List<TopupChargeType> chargeTypes, int selectedPosition) {
        this.chargeTypes = chargeTypes;
        this.selectedPosition = selectedPosition;
    }

    @NonNull
    @Override
    public ChargeTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_charge_type, parent, false);
        return new ChargeTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChargeTypeViewHolder holder, int position) {
        holder.bindChargeType(chargeTypes.get(position), position);
    }

    @Override
    public int getItemCount() {
        return chargeTypes.size();
    }

    class ChargeTypeViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private RadioButton radioButton;

        ChargeTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.amount);
            radioButton = itemView.findViewById(R.id.radio_amount);
        }

        void bindChargeType(TopupChargeType chargeType, int position) {
            radioButton.setOnClickListener(v -> itemView.performClick());
            itemView.setOnClickListener(v -> {
                selectedPosition = getAdapterPosition();
                notifyDataSetChanged();
            });
            radioButton.setChecked(selectedPosition == position);
            textView.setText(chargeType.getTitle());

        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

}

