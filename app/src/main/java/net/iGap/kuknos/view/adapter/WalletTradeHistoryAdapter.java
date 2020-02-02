package net.iGap.kuknos.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.kuknos.service.model.Parsian.KuknosTradeResponse;

import org.stellar.sdk.responses.OfferResponse;

import java.util.List;

public class WalletTradeHistoryAdapter extends RecyclerView.Adapter<WalletTradeHistoryAdapter.ViewHolder> {

    private List<KuknosTradeResponse.TradeResponse> kuknosTradeHistoryMS;

    public WalletTradeHistoryAdapter(List<KuknosTradeResponse.TradeResponse> kuknosTradeHistoryMS) {
        this.kuknosTradeHistoryMS = kuknosTradeHistoryMS;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_kuknos_trade_history_cell, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.initView(kuknosTradeHistoryMS.get(i));
    }

    @Override
    public int getItemCount() {
        return kuknosTradeHistoryMS.size();
    }

    private void deleteCell(OfferResponse model) {
        kuknosTradeHistoryMS.remove(model);
        notifyDataSetChanged();
        // TODO: 8/21/2019 send delete request to server 
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

        public void initView(KuknosTradeResponse.TradeResponse model) {
            sell.setText(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(model.getBaseAsset().getType()) : model.getBaseAsset().getType());
            amount.setText(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(model.getBaseAmount()) : model.getBaseAmount());
            recieve.setText(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(model.getCounterAsset().getType()) : model.getCounterAsset().getType());
//            if (mode == 0) {
            date.setText(model.getLedgerCloseTime());
            date.setVisibility(View.VISIBLE);
            delete.setVisibility(View.GONE);
            /*} else {
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
            }*/
        }
    }
}
