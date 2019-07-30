package net.iGap.kuknos.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.kuknos.service.model.KuknosWHistoryM;

import java.util.List;

public class WalletHistoryRAdapter extends RecyclerView.Adapter<WalletHistoryRAdapter.ViewHolder> {

    private List<KuknosWHistoryM> kuknosWHistoryMS;
    private Context context;

    public WalletHistoryRAdapter(List<KuknosWHistoryM> kuknosWHistoryMS, Context context) {
        this.kuknosWHistoryMS = kuknosWHistoryMS;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_kuknos_whistory_cell, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.initView(kuknosWHistoryMS.get(i));
    }

    @Override
    public int getItemCount() {
        return kuknosWHistoryMS.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView date;
        private TextView amount;
        private TextView desc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.kuknos_wHistoryCell_date);
            amount = itemView.findViewById(R.id.kuknos_wHistoryCell_amount);
            desc = itemView.findViewById(R.id.kuknos_wHistoryCell_desc);

        }

        public void initView(KuknosWHistoryM model) {
            date.setText(model.getDate());
            amount.setText(model.getAmount());
            desc.setText(model.getDesc());
        }
    }
}
