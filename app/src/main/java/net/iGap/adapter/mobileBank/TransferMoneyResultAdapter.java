package net.iGap.adapter.mobileBank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.helper.HelperMobileBank;
import net.iGap.model.mobileBank.TransferMoneyCtcResultModel;

import java.util.ArrayList;
import java.util.List;

public class TransferMoneyResultAdapter extends RecyclerView.Adapter<TransferMoneyResultAdapter.ViewHolder> {

    private List<TransferMoneyCtcResultModel> items = new ArrayList<>();

    public void setItems(List<TransferMoneyCtcResultModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_mb_transfer_money_result, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (position % 2 != 0) {
            holder.root.setBackgroundResource(0);
        }

        holder.tvKey.setText(items.get(position).getKey());
        holder.tvValue.setText(HelperMobileBank.checkNumbersInMultiLangs(items.get(position).getValue()));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private View root;
        private TextView tvKey, tvValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvKey = itemView.findViewById(R.id.tvKey);
            tvValue = itemView.findViewById(R.id.tvValue);
            root = itemView.findViewById(R.id.root);
        }
    }

}
