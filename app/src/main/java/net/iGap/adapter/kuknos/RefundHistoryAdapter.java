package net.iGap.adapter.kuknos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.kuknos.Model.Parsian.KuknosRefundHistory;

import java.text.DecimalFormat;

public class RefundHistoryAdapter extends RecyclerView.Adapter<RefundHistoryAdapter.RefundViewHolder> {

    private KuknosRefundHistory refundHistory;
    private Context context;

    public RefundHistoryAdapter(KuknosRefundHistory refundHistory, Context context) {
        this.refundHistory = refundHistory;
        this.context = context;
    }

    @NonNull
    @Override
    public RefundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_kuknos_refund_history_cell,parent,false);
        return new RefundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RefundViewHolder holder, int position) {
        holder.initViews(refundHistory.getRefunds().get(position));
    }

    @Override
    public int getItemCount() {
        return refundHistory.getRefunds().size();
    }

    public class RefundViewHolder extends RecyclerView.ViewHolder{

        private TextView refundAssetCode, refundAmount, refundFee, refundCount;
        public RefundViewHolder(@NonNull View view) {
            super(view);

            refundAssetCode = view.findViewById(R.id.kuknos_refund_assetCode);
            refundAmount = view.findViewById(R.id.kuknos_refund_amount);
            refundFee = view.findViewById(R.id.kuknos_refund_fee);
            refundCount = view.findViewById(R.id.kuknos_refund_count);
        }

        public void initViews(KuknosRefundHistory.Refund refund) {

            refundAssetCode.setText(refund.getAssetCode());
            DecimalFormat df = new DecimalFormat("#,##0.00");

            refundAmount.setText(
                    HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(df.format(Long.valueOf(refund.getAmount())))
                            : df.format(Long.valueOf(refund.getAmount()) + " ﷼")
            );

            refundFee.setText(
                    HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(df.format(Float.valueOf(refund.getFee())))
                            : df.format(Float.valueOf(refund.getFee()))
            );

            refundCount.setText(
                    HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(df.format(Float.valueOf(refund.getAssetCount())))
                            : df.format(Float.valueOf(refund.getAssetCount()))
            );
        }
    }
}
