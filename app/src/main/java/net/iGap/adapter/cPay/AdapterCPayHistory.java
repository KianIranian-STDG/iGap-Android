package net.iGap.adapter.cPay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.model.cPay.CPayHistoryModel;

import java.util.ArrayList;
import java.util.List;

public class AdapterCPayHistory extends RecyclerView.Adapter<AdapterCPayHistory.CPayHistoryViewHolder> {

    private List<CPayHistoryModel> items = new ArrayList<>();
    private LayoutInflater inflater;

    public AdapterCPayHistory(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    public void setHistoryItems(List<CPayHistoryModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CPayHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.row_cpay_history, viewGroup, false);
        return new CPayHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CPayHistoryViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class CPayHistoryViewHolder extends RecyclerView.ViewHolder {

        public CPayHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(CPayHistoryModel item) {

        }
    }
}
