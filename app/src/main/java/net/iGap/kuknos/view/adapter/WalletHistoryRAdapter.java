package net.iGap.kuknos.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.kuknos.service.model.KuknosWHistoryM;

import org.stellar.sdk.responses.Page;
import org.stellar.sdk.responses.operations.CreateAccountOperationResponse;
import org.stellar.sdk.responses.operations.OperationResponse;
import org.stellar.sdk.responses.operations.PaymentOperationResponse;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.List;

public class WalletHistoryRAdapter extends RecyclerView.Adapter<WalletHistoryRAdapter.ViewHolder> {

    private Page<OperationResponse> kuknosWHistoryMS;
    private Context context;

    public WalletHistoryRAdapter(Page<OperationResponse> kuknosWHistoryMS, Context context) {
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
        if (kuknosWHistoryMS.getRecords().get(i) instanceof  PaymentOperationResponse) {
            viewHolder.initView((PaymentOperationResponse) kuknosWHistoryMS.getRecords().get(i));
        }
        else {
            viewHolder.initViewCreateAccount((CreateAccountOperationResponse) kuknosWHistoryMS.getRecords().get(i));
        }
    }

    @Override
    public int getItemCount() {
        return kuknosWHistoryMS.getRecords().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView date;
        private TextView amount;
        private TextView desc;
        private TextView icon;
        private TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.kuknos_wHistoryCell_date);
            amount = itemView.findViewById(R.id.kuknos_wHistoryCell_amount);
            desc = itemView.findViewById(R.id.kuknos_wHistoryCell_desc);
            icon = itemView.findViewById(R.id.kuknos_wHistoryCell_icon);
            time = itemView.findViewById(R.id.kuknos_wHistoryCell_time);

        }

        public void initView(PaymentOperationResponse model) {
            String[] temp = model.getCreatedAt().split("T");
            date.setText(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(temp[0]) : temp[0]);
            time.setText(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(temp[1].substring(0,5)) : temp[1].substring(0,5));
            DecimalFormat df = new DecimalFormat("#,###.00");
            amount.setText(HelperCalander.isPersianUnicode ?
                    HelperCalander.convertToUnicodeFarsiNumber(df.format(Double.valueOf(model.getAmount())))
                    : df.format(Double.valueOf(model.getAmount())));
            if (model.getSourceAccount().equals(model.getFrom())) {
                desc.setText(context.getResources().getString(R.string.kuknos_wHistory_sent));
                icon.setText(context.getResources().getString(R.string.upload_ic));
                icon.setTextColor(Color.RED);
            }
            else {
                desc.setText(context.getResources().getString(R.string.kuknos_wHistory_receive));
                icon.setText(context.getResources().getString(R.string.download_ic));
                icon.setTextColor(Color.GREEN);
            }
        }

        public void initViewCreateAccount(CreateAccountOperationResponse model) {
            String[] temp = model.getCreatedAt().split("T");
            date.setText(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(temp[0]) : temp[0]);
            time.setText(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(temp[1].substring(0,5)) : temp[1].substring(0,5));
            DecimalFormat df = new DecimalFormat("#,###.00");
            amount.setText(HelperCalander.isPersianUnicode ?
                    HelperCalander.convertToUnicodeFarsiNumber(df.format(Double.valueOf(model.getStartingBalance())))
                    : df.format(Double.valueOf(model.getStartingBalance())));
            desc.setText(context.getResources().getString(R.string.kuknos_wHistory_creatAccount));
            icon.setTextColor(Color.GREEN);
        }
    }
}
