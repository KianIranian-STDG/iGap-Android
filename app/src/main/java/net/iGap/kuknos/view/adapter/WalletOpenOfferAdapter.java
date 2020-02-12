package net.iGap.kuknos.view.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.module.dialog.DefaultRoundDialog;
import net.iGap.helper.HelperCalander;
import net.iGap.kuknos.service.model.Parsian.KuknosOfferResponse;

import java.util.List;

public class WalletOpenOfferAdapter extends RecyclerView.Adapter<WalletOpenOfferAdapter.ViewHolder> {

    private List<KuknosOfferResponse.OfferResponse> kuknosTradeHistoryMS;
    private Context context;

    public WalletOpenOfferAdapter(List<KuknosOfferResponse.OfferResponse> kuknosTradeHistoryMS) {
        this.kuknosTradeHistoryMS = kuknosTradeHistoryMS;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_kuknos_trade_history_cell, viewGroup, false);
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

    private void deleteCell(KuknosOfferResponse.OfferResponse model) {
        // TODO: 2/3/2020 make api call
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

        public void initView(KuknosOfferResponse.OfferResponse model) {
            sell.setText(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(model.getSelling().getType()) : model.getSelling().getType());
            amount.setText(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(model.getAmount()) : model.getAmount());
            recieve.setText(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(model.getBuying().getType()) : model.getBuying().getType());
            date.setVisibility(View.GONE);
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(v -> {
                DefaultRoundDialog defaultRoundDialog = new DefaultRoundDialog(context);
                defaultRoundDialog.setTitle(context.getResources().getString(R.string.kuknos_tradeDialogDelete_title))
                        .setMessage(context.getResources().getString(R.string.kuknos_tradeDialogDelete_message));
                defaultRoundDialog.setPositiveButton(context.getResources().getString(R.string.kuknos_tradeDialogDelete_btn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteCell(model);
                    }
                });
                defaultRoundDialog.show();
            });
//            }
        }
    }
}
