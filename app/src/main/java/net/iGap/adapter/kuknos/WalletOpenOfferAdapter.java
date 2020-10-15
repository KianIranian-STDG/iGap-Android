package net.iGap.adapter.kuknos;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.kuknos.Model.Parsian.KuknosOfferResponse;
import net.iGap.module.dialog.DefaultRoundDialog;

import java.text.DecimalFormat;
import java.util.List;

public class WalletOpenOfferAdapter extends RecyclerView.Adapter<WalletOpenOfferAdapter.ViewHolder> {

    private List<KuknosOfferResponse.OfferResponse> kuknosTradeHistoryMS;
    private Context context;
    private onClickListener clickListener;

    public WalletOpenOfferAdapter(List<KuknosOfferResponse.OfferResponse> kuknosTradeHistoryMS, onClickListener clickListener) {
        this.kuknosTradeHistoryMS = kuknosTradeHistoryMS;
        this.clickListener = clickListener;
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
        viewHolder.initView(i);
    }

    @Override
    public int getItemCount() {
        return kuknosTradeHistoryMS.size();
    }

    private void deleteCell(int position) {
        clickListener.onDelete(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView sell;
        private TextView amount;
        private TextView receive;
        private TextView date;
        private TextView delete;
        private TextView price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sell = itemView.findViewById(R.id.kuknos_tradeHistoryCell_sell);
            amount = itemView.findViewById(R.id.kuknos_tradeHistoryCell_amount);
            receive = itemView.findViewById(R.id.kuknos_tradeHistoryCell_receive);
            date = itemView.findViewById(R.id.kuknos_tradeHistoryCell_date);
            delete = itemView.findViewById(R.id.kuknos_tradeHistoryCell_delete);
            price = itemView.findViewById(R.id.kuknos_tradeHistoryCell_price);

        }

        public void initView(int position) {
            KuknosOfferResponse.OfferResponse model = kuknosTradeHistoryMS.get(position);
            String sellTXT = "" + (model.getSelling().getAsset().getType().equals("native") ? "PMN" : model.getSelling().getAssetCode());
            sell.setText(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(sellTXT) : sellTXT);

            DecimalFormat df = new DecimalFormat("#,##0.00");
            amount.setText(HelperCalander.isPersianUnicode ?
                    HelperCalander.convertToUnicodeFarsiNumber(df.format(Double.parseDouble(model.getAmount())))
                    : df.format(Double.parseDouble(model.getAmount())));

            String recieveTXT = "" + (model.getBuying().getAsset().getType().equals("native") ? "PMN" : model.getBuying().getAssetCode());
            receive.setText(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(recieveTXT) : recieveTXT);

            date.setVisibility(View.GONE);
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(v -> {
                DefaultRoundDialog defaultRoundDialog = new DefaultRoundDialog(context);
                defaultRoundDialog.setTitle(context.getResources().getString(R.string.kuknos_tradeDialogDelete_title))
                        .setMessage(context.getResources().getString(R.string.kuknos_tradeDialogDelete_message));
                defaultRoundDialog.setPositiveButton(context.getResources().getString(R.string.kuknos_tradeDialogDelete_btn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteCell(position);
                    }
                });
                defaultRoundDialog.show();
            });

            price.setText(HelperCalander.isPersianUnicode ?
                    HelperCalander.convertToUnicodeFarsiNumber(df.format(Double.parseDouble(model.getPrice())))
                    : df.format(Double.parseDouble(model.getPrice())));
//            }
        }
    }

    public interface onClickListener {
        void onDelete(int position);
    }
}
