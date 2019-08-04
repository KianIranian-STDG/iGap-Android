package net.iGap.kuknos.view.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.helper.HelperCalander;
import net.iGap.kuknos.service.model.KuknosTradeHistoryM;
import net.iGap.kuknos.service.model.KuknosWHistoryM;

import java.util.List;

public class WalletTradeHistoryAdapter extends RecyclerView.Adapter<WalletTradeHistoryAdapter.ViewHolder> {

    private List<KuknosTradeHistoryM> kuknosTradeHistoryMS;
    private Context context;
    // mode : 0 history / 1 active
    private int mode;

    public WalletTradeHistoryAdapter(List<KuknosTradeHistoryM> kuknosTradeHistoryMS, int mode, Context context) {
        this.kuknosTradeHistoryMS = kuknosTradeHistoryMS;
        this.context = context;
        this.mode = mode;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_kuknos_trade_history_cell, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.initView(kuknosTradeHistoryMS.get(i), mode);
    }

    @Override
    public int getItemCount() {
        return kuknosTradeHistoryMS.size();
    }

    private void deleteCell(KuknosTradeHistoryM model) {
        kuknosTradeHistoryMS.remove(model);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView sell;
        private TextView amount;
        private TextView recieve;
        private TextView date;
        private TextView delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sell = itemView.findViewById(R.id.kuknos_tradeHistoryCell_sell);
            amount = itemView.findViewById(R.id.kuknos_tradeHistoryCell_amount);
            recieve = itemView.findViewById(R.id.kuknos_tradeHistoryCell_receive);
            date = itemView.findViewById(R.id.kuknos_tradeHistoryCell_date);
            delete = itemView.findViewById(R.id.kuknos_tradeHistoryCell_delete);

        }

        public void initView(KuknosTradeHistoryM model, int mode) {
            sell.setText(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(model.getSell()) : model.getSell());
            amount.setText(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(model.getAmount()) : model.getAmount());
            recieve.setText(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(model.getReceive()) : model.getReceive());
            if (mode == 0) {
                date.setText(model.getDate());
                date.setVisibility(View.VISIBLE);
                delete.setVisibility(View.GONE);
            }
            else {
                date.setVisibility(View.GONE);
                delete.setVisibility(View.VISIBLE);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DefaultRoundDialog defaultRoundDialog = new DefaultRoundDialog(context);
                        defaultRoundDialog.setTitle(context.getResources().getString(R.string.kuknos_tradeDialogDelete_title))
                                .setMessage(context.getResources().getString(R.string.kuknos_tradeDialogDelete_message));
                        defaultRoundDialog.setPositiveButton(context.getResources().getString(R.string.kuknos_tradeDialogDelete_btn), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteCell(model);
                            }
                        });
                        defaultRoundDialog.show();
                    }
                });
            }
        }
    }
}
