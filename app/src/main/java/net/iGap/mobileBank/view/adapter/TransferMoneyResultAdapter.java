package net.iGap.mobileBank.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;

import java.util.ArrayList;
import java.util.List;

public class TransferMoneyResultAdapter extends RecyclerView.Adapter<TransferMoneyResultAdapter.ViewHolder> {

    private List<TranferCtcResultModel> items = new ArrayList<>();

    public void setItems(List<TranferCtcResultModel> items) {
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
        holder.tvValue.setText(items.get(position).getValue());

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

    public class TranferCtcResultModel {

        String key;
        String value;

        public TranferCtcResultModel(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public TranferCtcResultModel() {
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
