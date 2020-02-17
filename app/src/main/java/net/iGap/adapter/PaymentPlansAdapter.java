package net.iGap.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.helper.HelperMobileBank;
import net.iGap.model.payment.PaymentFeature;

import java.util.ArrayList;
import java.util.List;

public class PaymentPlansAdapter extends RecyclerView.Adapter<PaymentPlansAdapter.ChequeListViewHolder> {

    private List<PaymentFeature> items = new ArrayList<>();
    private PlanListListener listener;
    private Context context;

    public PaymentPlansAdapter(List<PaymentFeature> items, PlanListListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void setListener(PlanListListener listener) {
        this.listener = listener;
    }

    public void setItems(List<PaymentFeature> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChequeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ChequeListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_universal_payment_cell, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChequeListViewHolder holder, int position) {
        holder.bind(position);
        holder.root.setOnClickListener(v -> listener.onPlanClicked(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ChequeListViewHolder extends RecyclerView.ViewHolder {

        private View root;
        private TextView title, userScore, spentScore, price;

        public ChequeListViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.cardHolder);
            title = itemView.findViewById(R.id.planTitle);
            userScore = itemView.findViewById(R.id.planUserScore);
            spentScore = itemView.findViewById(R.id.planSpentScore);
            price = itemView.findViewById(R.id.planPrice);
        }

        @SuppressLint("SetTextI18n")
        public void bind(int position) {

            title.setText(items.get(position).getTitle());
            userScore.setText(getString(R.string.payment_userScore) + HelperMobileBank.checkNumbersInMultiLangs("" + items.get(position).getUserScore()));
            spentScore.setText(getString(R.string.payment_spentScore) + HelperMobileBank.checkNumbersInMultiLangs("" + items.get(position).getSpentScore()));
            price.setText(getString(R.string.payment_price) + HelperMobileBank.checkNumbersInMultiLangs("" + items.get(position).getPrice()) + getString(R.string.rial));

        }

        private String getString(int id) {
            return context.getString(id);
        }

    }

    public interface PlanListListener {
        void onPlanClicked(int position);
    }
}
