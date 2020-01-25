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
            tvAmount.setText(getString(R.string.amount_2, item.getAmount() + ""));
            tvSDate.setText(getString(R.string.start_date, item.getBeginDate()));
            tvEDate.setText(getString(R.string.end_date, item.getEndDate()));
            tvBranchName.setText(getString(R.string.branch_name, item.getBranchName()));
            tvLoanCount.setText(getString(R.string.loan_count, item.getLoanNumber()));
            tvState.setText(getString(R.string.state, item.getStatus()));
            tvRemained.setText(getString(R.string.remained_loan, item.getLoanRemainder() + ""));

        }

        private String getString(int label, String value) {
            if (HelperCalander.isPersianUnicode) {
                value = HelperCalander.convertToUnicodeFarsiNumber(value);
            }
            return itemView.getContext().getString(label) + " " + value;
        }
    }

    public interface LoanListListener {
        void onLoanClicked(String num);
    }
}
