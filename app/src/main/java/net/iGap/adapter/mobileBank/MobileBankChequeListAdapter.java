package net.iGap.adapter.mobileBank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.model.mobileBank.BankChequeSingle;
import net.iGap.module.Theme;
import net.iGap.module.mobileBank.JalaliCalendar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MobileBankChequeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<BankChequeSingle> mdata;
    private OnItemClickListener listener;

    public MobileBankChequeListAdapter(List<BankChequeSingle> mdata, OnItemClickListener listener) {
        if (mdata != null)
            this.mdata = mdata;
        else
            this.mdata = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mobile_bank_cheque_list_cell, viewGroup, false));
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

    public BankChequeSingle getItem(int position) {
        return mdata.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView chNumber, chValue, chStatus, chDate;
        private Button block, customize;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            chNumber = itemView.findViewById(R.id.chequeNumber);
            chValue = itemView.findViewById(R.id.chequePrice);
            chStatus = itemView.findViewById(R.id.chequeStatus);
            chDate = itemView.findViewById(R.id.chequeDate);
            block = itemView.findViewById(R.id.blockCheque);
            customize = itemView.findViewById(R.id.customizeCheque);

        }

        void initView(int position) {
            chNumber.setText(CompatibleUnicode(mdata.get(position).getNumber()));

            if (mdata.get(position).getBalance() != null) {
                chValue.setText(CompatibleUnicode(decimalFormatter(Double.parseDouble("" + mdata.get(position).getBalance()))) + itemView.getContext().getResources().getString(R.string.rial));
            } else {
                chValue.setText(itemView.getContext().getResources().getString(R.string.mobile_bank_balance_error_no_price));
            }

            String date = mdata.get(position).getChangeStatusDate();
            if (date == null || date.equals("")) {
                chDate.setVisibility(View.GONE);
            } else {
                chDate.setText(itemView.getContext().getString(R.string.date) + ": " + JalaliCalendar.getPersianDate(date));
                chDate.setVisibility(View.VISIBLE);
            }
            String status = mdata.get(position).getStatus();

            if (status.equals("USED")) {
                customize.setVisibility(View.VISIBLE);
            } else {
                customize.setVisibility(View.GONE);
            }
            block.setVisibility(View.GONE);

            chStatus.setText(getStatusText(status).toLowerCase());
            if (status.equals("CASH")) {
                chStatus.setTextColor(Theme.getInstance().getPrimaryTextIconColor(chStatus.getContext()));
            } else {
                chStatus.setTextColor(chStatus.getContext().getResources().getColor(R.color.brown));
            }

            block.setOnClickListener(v -> listener.onBlock(getAdapterPosition()));
            customize.setOnClickListener(v -> listener.onCustomizeClicked(getAdapterPosition()));
        }

        private String getStatusText(String status) {
            switch (status) {
                case "USED":
                    return itemView.getContext().getString(R.string.USED);
                case "CASH":
                    return itemView.getContext().getString(R.string.CASH);
                case "REJECT":
                    return itemView.getContext().getString(R.string.REJECT);
                case "RETURN":
                    return itemView.getContext().getString(R.string.RETURN);
                case "PAY":
                    return itemView.getContext().getString(R.string.PAY);
                case "HOLD":
                    return itemView.getContext().getString(R.string.HOLD);
                case "INTERBANK_BLOCK":
                    return itemView.getContext().getString(R.string.INTERBANK_BLOCK);
                case "INVALID":
                    return itemView.getContext().getString(R.string.INVALID_PARSIAN);
                case "REGISTER":
                    return itemView.getContext().getString(R.string.REGISTER);
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

        void onCustomizeClicked(int position);
    }

}
