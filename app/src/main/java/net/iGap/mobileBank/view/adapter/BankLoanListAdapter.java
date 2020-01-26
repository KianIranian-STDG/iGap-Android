package net.iGap.mobileBank.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.mobileBank.repository.model.LoanListModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BankLoanListAdapter extends RecyclerView.Adapter<BankLoanListAdapter.BankLoanViewHolder> {

    private List<LoanListModel> items = new ArrayList<>();
    private LoanListListener listener;

    public void setListener(LoanListListener listener) {
        this.listener = listener;
    }

    public void setItems(List<LoanListModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BankLoanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BankLoanViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_loan_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BankLoanViewHolder holder, int position) {
        holder.bind(items.get(position));
        holder.itemView.setOnClickListener(v -> listener.onLoanClicked(items.get(holder.getAdapterPosition()).getLoanNumber()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class BankLoanViewHolder extends RecyclerView.ViewHolder {

        private TextView tvLoanNumber, tvAmount, tvSDate, tvEDate, tvLoanCount, tvRemained, tvState, tvBranchName;

        public BankLoanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvSDate = itemView.findViewById(R.id.tvBeganDate);
            tvEDate = itemView.findViewById(R.id.tvEndDate);
            tvLoanCount = itemView.findViewById(R.id.tvLoanCount);
            tvRemained = itemView.findViewById(R.id.tvRemained);
            tvState = itemView.findViewById(R.id.tvState);
            tvBranchName = itemView.findViewById(R.id.tvBranch);
            tvLoanNumber = itemView.findViewById(R.id.tvLoanNumber);
        }

        public void bind(LoanListModel item) {

            tvLoanNumber.setText(getString(R.string.loan_number, item.getLoanNumber()));
            tvAmount.setText(getString(R.string.amount_2, decimalFormatter(item.getAmount())));
            tvSDate.setText(getString(R.string.start_date, item.getBeginDate()));
            tvEDate.setText(getString(R.string.end_date, item.getEndDate()));
            tvBranchName.setText(getString(R.string.branch_name, item.getBranchName()));
            tvLoanCount.setText(getString(R.string.loan_count, item.getPayNumber() + ""));
            tvState.setText(getString(R.string.state, getStatusString(item.getStatus())));
            tvRemained.setText(getString(R.string.remained_loan, item.getLoanRemainder() + ""));

        }

        private String decimalFormatter(long entry) {
            try {
                DecimalFormat df = new DecimalFormat(",###");
                return df.format(entry);
            } catch (Exception e) {
                return entry + "";
            }
        }

        private String getStatusString(String status) {
            switch (status) {

                case "UNPAID":
                    return getString(R.string.unpaid);
                case "PAID_INCOMPLETE":
                    return getString(R.string.PAID_INCOMPLETE);
                case "ACTIVE":
                    return getString(R.string.ACTIVE);
                case "DOUBTFUL_RECEIPT":
                    return getString(R.string.DOUBTFUL_RECEIPT);
                case "SETTLEMENT_READY":
                    return getString(R.string.SETTLEMENT_READY);
                case "FREE":
                    return getString(R.string.FREE);
                case "OTHER":
                    return getString(R.string.OTHER);
                case "INCOMPLETE_COLLATERAL":
                    return getString(R.string.INCOMPLETE_COLLATERAL);
                case "ARREARS":
                    return getString(R.string.ARREARS);
                case "PAST_DUE":
                    return getString(R.string.PAST_DUE);
                case "CURRENT_DUE":
                    return getString(R.string.CURRENT_DUE);
                case "REJECTED":
                    return getString(R.string.REJECTED);
                default:
                    return status;
            }
        }

        private String getString(int label, String value) {
            if (HelperCalander.isPersianUnicode) {
                value = HelperCalander.convertToUnicodeFarsiNumber(value);
            }
            return itemView.getContext().getString(label) + " " + value;
        }

        private String getString(int id) {
            return itemView.getContext().getString(id);
        }
    }

    public interface LoanListListener {
        void onLoanClicked(String num);
    }
}
