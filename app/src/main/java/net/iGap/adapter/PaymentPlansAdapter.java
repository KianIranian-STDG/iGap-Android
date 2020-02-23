package net.iGap.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
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
    private int lastChecked = -1;

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
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ChequeListViewHolder extends RecyclerView.ViewHolder {

        private TextView title, userScore, spentScore, price;
        private View click;
        private CheckBox checkBox;
        private Group detail;

        public ChequeListViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.planTitle);
            userScore = itemView.findViewById(R.id.planUserScore);
            spentScore = itemView.findViewById(R.id.planSpentScore);
            price = itemView.findViewById(R.id.planPrice);
            click = itemView.findViewById(R.id.planClick);
            checkBox = itemView.findViewById(R.id.planCheckBox);
            detail = itemView.findViewById(R.id.planGroup);
        }

        @SuppressLint("SetTextI18n")
        public void bind(int position) {

            title.setText(items.get(position).getTitle());
            userScore.setText(getString(R.string.payment_userScore) + HelperMobileBank.checkNumbersInMultiLangs("" + items.get(position).getUserScore()));
            spentScore.setText(getString(R.string.payment_spentScore) + HelperMobileBank.checkNumbersInMultiLangs("" + items.get(position).getSpentScore()));
            price.setText(getString(R.string.payment_price) + HelperMobileBank.checkNumbersInMultiLangs("" + items.get(position).getDiscount()) + getString(R.string.rial));

            click.setOnClickListener(null);
            checkBox.setOnCheckedChangeListener(null);

            // check for notify data set changed
            if (items.get(position).isChecked()) {
                detail.setVisibility(View.VISIBLE);
                checkBox.setChecked(true);
            } else {
                detail.setVisibility(View.GONE);
                checkBox.setChecked(false);
            }

            click.setOnClickListener(v -> checkBox.performClick());
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> toggleState(getAdapterPosition()));

        }

        private String getString(int id) {
            return context.getString(id);
        }

        private void toggleState(int position) {

            if (position == lastChecked) {
                items.get(position).toggleCheck();
                lastChecked = -1;
            } else {
                if (lastChecked != -1) {
                    items.get(lastChecked).toggleCheck();
                    listener.onPlanClicked(lastChecked, false);
                }
                items.get(position).toggleCheck();
                lastChecked = position;
            }
            listener.onPlanClicked(position, checkBox.isChecked());
            notifyDataSetChanged();
        }

    }

    public interface PlanListListener {
        void onPlanClicked(int position, boolean state);
    }

}
