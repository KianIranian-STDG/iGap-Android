package net.iGap.adapter.payment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;

import java.util.List;

public class AdapterHistoryNumber extends RecyclerView.Adapter<AdapterHistoryNumber.ContactNumberViewHolder> {
    private List<Amount> amountList;
    private int selectedPosition = -1;

    public AdapterHistoryNumber(List<Amount> amountList) {
        Amount amount = new Amount();
        amount.setTextAmount("091212345678");
        amountList.add(amount);
        Amount amount1 = new Amount();
        amount1.setTextAmount("093511111111");
        amountList.add(amount1);
        Amount amount2 = new Amount();
        amount2.setTextAmount("09198888888");
        amountList.add(amount2);
        Amount amount3 = new Amount();
        amount3.setTextAmount("091233333333");
        amountList.add(amount3);
    }

    @NonNull
    @Override
    public ContactNumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_charge_history, parent, false);
        return new ContactNumberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactNumberViewHolder holder, int position) {
        holder.bindNUmber();
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public class ContactNumberViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ContactNumberViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.number_contact);

            itemView.setOnClickListener(v -> {
                selectedPosition = getAdapterPosition();
                notifyDataSetChanged();
            });
        }

        public void bindNUmber() {
            textView.setText("091212345678");
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}
