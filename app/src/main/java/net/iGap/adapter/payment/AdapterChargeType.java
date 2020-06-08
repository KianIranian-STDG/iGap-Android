package net.iGap.adapter.payment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;

import java.util.List;

public class AdapterChargeType extends RecyclerView.Adapter<AdapterChargeType.ChargeTypeViewHolder> {

    private List<ChargeType> chargeTypes;
    private int selectedPosition;

    public AdapterChargeType(List<ChargeType> chargeTypes) {
        this.chargeTypes = chargeTypes;
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

        void bindChargeType(ChargeType chargeType, int position) {
            radioButton.setChecked(selectedPosition == position);
            radioButton.setOnClickListener(v -> itemView.performClick());
            textView.setText(chargeType.getChargeType());

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

}

