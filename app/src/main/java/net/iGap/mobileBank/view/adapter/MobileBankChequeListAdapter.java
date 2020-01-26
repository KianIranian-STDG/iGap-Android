package net.iGap.mobileBank.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.mobileBank.repository.model.BankChequeSingle;
import net.iGap.mobileBank.repository.util.JalaliCalendar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MobileBankChequeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<BankChequeSingle> mdata;
    private OnItemClickListener blockBTN;
    private Context context;

    public MobileBankChequeListAdapter(List<BankChequeSingle> mdata, OnItemClickListener blockBTN) {
        if (mdata != null)
            this.mdata = mdata;
        else
            this.mdata = new ArrayList<>();
        this.blockBTN = blockBTN;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        context = viewGroup.getContext();
        View singleVH = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mobile_bank_cheque_list_cell, viewGroup, false);
        viewHolder = new ViewHolder(singleVH);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ((ViewHolder) viewHolder).initView(position);
    }

    @Override
    public int getItemCount() {
        if (mdata == null)
            return 0;
        return mdata.size();
    }

    public void addItems(List<BankChequeSingle> postItems) {
        this.mdata.addAll(postItems);
        notifyDataSetChanged();
    }

    public void removeAll() {
        this.mdata.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView chNumber, chValue, chStatus, chDate;
        private Button block;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            chNumber = itemView.findViewById(R.id.chequeNumber);
            chValue = itemView.findViewById(R.id.chequePrice);
            chStatus = itemView.findViewById(R.id.chequeStatus);
            chDate = itemView.findViewById(R.id.chequeDate);
            block = itemView.findViewById(R.id.blockCheque);

        }

        void initView(int position) {
            chNumber.setText(context.getResources().getString(R.string.mobile_bank_cheque_number,
                    CompatibleUnicode(mdata.get(position).getNumber())));

            if (mdata.get(position).getBalance() != null)
                chValue.setText(context.getResources().getString(R.string.mobile_bank_cheque_balance,
                        CompatibleUnicode(decimalFormatter(Double.parseDouble("" + mdata.get(position).getBalance()))))
                        + context.getResources().getString(R.string.rial));
            else {
                chValue.setText(context.getResources().getString(R.string.mobile_bank_balance_error_no_price));
            }

            String date = mdata.get(position).getChangeStatusDate();
            if (date == null || date.equals("")) {
                chDate.setVisibility(View.GONE);
            } else {
                chDate.setText(context.getString(R.string.date) + ": " + JalaliCalendar.getPersianDate(date));
                chDate.setVisibility(View.VISIBLE);
            }
            String status = mdata.get(position).getStatus();

            if (status.equals("USED") || status.equals("REGISTER")) {
                block.setVisibility(View.VISIBLE);
            } else {
                block.setVisibility(View.GONE);
            }

            chStatus.setText(context.getResources().getString(R.string.mobile_bank_cheque_status, getStatusText(status)));

            block.setOnClickListener(v -> blockBTN.onBlock(position));
        }

        private String getStatusText(String status) {
            switch (status) {
                case "USED":
                    return context.getString(R.string.USED);
                case "CASH":
                    return context.getString(R.string.CASH);
                case "REJECT":
                    return context.getString(R.string.REJECT);
                case "RETURN":
                    return context.getString(R.string.RETURN);
                case "PAY":
                    return context.getString(R.string.PAY);
                case "HOLD":
                    return context.getString(R.string.HOLD);
                case "INTERBANK_BLOCK":
                    return context.getString(R.string.INTERBANK_BLOCK);
                case "INVALID":
                    return context.getString(R.string.INVALID_PARSIAN);
                case "REGISTER":
                    return context.getString(R.string.REGISTER);
                default:
                    return status;
            }
        }

        private String decimalFormatter(Double entry) {
            DecimalFormat df = new DecimalFormat(",###");
            return df.format(entry);
        }

        private String CompatibleUnicode(String entry) {
            return HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(entry)) : entry;
        }
    }

    public interface OnItemClickListener {
        void onBlock(int position);
    }

}
