package net.iGap.mobileBank.view.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.mobileBank.repository.model.ChequeModel;

import java.util.ArrayList;
import java.util.List;

public class BankChequesListAdapter extends RecyclerView.Adapter<BankChequesListAdapter.ChequeListViewHolder> {

    private List<ChequeModel> items = new ArrayList<>();
    private ChequeListListener listener;

    public void setListener(ChequeListListener listener) {
        this.listener = listener;
    }

    public void setItems(List<ChequeModel> items) {
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
        private TextView tvNumber, tvPageCount, tvPass, tvReject, tvCached, tvPBlocked, tvTBlocked, tvUsable;

        public ChequeListViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            tvNumber = itemView.findViewById(R.id.chequeNumber);
            tvPageCount = itemView.findViewById(R.id.chequeCount);
            tvPass = itemView.findViewById(R.id.tvPass);
            tvReject = itemView.findViewById(R.id.tvReject);
            tvCached = itemView.findViewById(R.id.tvCash);
            tvPBlocked = itemView.findViewById(R.id.tvPermanentBlock);
            tvTBlocked = itemView.findViewById(R.id.tvTempBlock);
            tvUsable = itemView.findViewById(R.id.tvUsable);
        }

        @SuppressLint("SetTextI18n")
        public void bind(ChequeModel item) {

            tvNumber.setText(getString(R.string.cheque_number) + item.getNumber());
            tvPageCount.setText(getString(R.string.page_count) + item.getPageCount());
            tvPass.setText(getString(R.string.pass_count) + item.getPageCount());
            tvReject.setText(getString(R.string.reject_count) + item.getPageCount());
            tvCached.setText(getString(R.string.cash_count) + item.getPageCount());
            tvPBlocked.setText(getString(R.string.permanent_block) + item.getPermanentBlocked());
            tvTBlocked.setText(getString(R.string.temporary_block) + item.getTemporaryBlock());
            tvUsable.setText(getString(R.string.usable_count) + item.getUnusedCheque());

        }

        private String getString(int id) {
            return tvNumber.getContext().getString(id);
        }
    }

    public interface ChequeListListener {
        void onChequeClicked(ChequeModel chequeModel);
    }
}
