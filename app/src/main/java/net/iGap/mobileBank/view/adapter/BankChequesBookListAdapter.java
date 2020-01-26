package net.iGap.mobileBank.view.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.mobileBank.repository.model.BankChequeBookListModel;

import java.util.ArrayList;
import java.util.List;

public class BankChequesBookListAdapter extends RecyclerView.Adapter<BankChequesBookListAdapter.ChequeListViewHolder> {

    private List<BankChequeBookListModel> items = new ArrayList<>();
    private ChequeListListener listener;

    public void setListener(ChequeListListener listener) {
        this.listener = listener;
    }

    public void setItems(List<BankChequeBookListModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChequeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChequeListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cheque_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChequeListViewHolder holder, int position) {
        holder.bind(items.get(position));
        holder.root.setOnClickListener(v -> listener.onChequeClicked(items.get(holder.getAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ChequeListViewHolder extends RecyclerView.ViewHolder {

        private View root;
        private TextView tvNumber, tvPageCount, tvPass, tvReject, tvCached, tvBlocked, tvUsable;

        public ChequeListViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            tvNumber = itemView.findViewById(R.id.chequeNumber);
            tvPageCount = itemView.findViewById(R.id.chequeCount);
            tvPass = itemView.findViewById(R.id.tvPass);
            tvReject = itemView.findViewById(R.id.tvReject);
            tvCached = itemView.findViewById(R.id.tvCash);
            tvBlocked = itemView.findViewById(R.id.tvBlock);
            tvUsable = itemView.findViewById(R.id.tvUsable);
        }

        @SuppressLint("SetTextI18n")
        public void bind(BankChequeBookListModel item) {

            tvNumber.setText(getString(R.string.cheque_number) + checkNumbers(item.getNumber()));
            tvPageCount.setText(getString(R.string.page_count) + checkNumbers(item.getPageCount() + ""));
            tvPass.setText(getString(R.string.pass_count) + checkNumbers(item.getPassCheque() + ""));
            tvReject.setText(getString(R.string.reject_count) + checkNumbers(item.getReject() + ""));
            tvCached.setText(getString(R.string.cash_count) + checkNumbers(item.getPartialCash() + ""));
            tvBlocked.setText(getString(R.string.block) + ": " + checkNumbers((item.getPermanentBlocked() + item.getTemporaryBlock()) + ""));
            tvUsable.setText(getString(R.string.usable_count) + checkNumbers(item.getUnusedCheque() + ""));

        }

        private String getString(int id) {
            return tvNumber.getContext().getString(id);
        }

        private String checkNumbers(String text) {
            if (HelperCalander.isPersianUnicode) {
                return HelperCalander.convertToUnicodeFarsiNumber(text);
            }
            return text;
        }
    }

    public interface ChequeListListener {
        void onChequeClicked(BankChequeBookListModel chequeModel);
    }
}
