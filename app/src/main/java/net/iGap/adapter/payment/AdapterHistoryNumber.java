package net.iGap.adapter.payment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.model.paymentPackage.FavoriteNumber;
import net.iGap.observers.interfaces.IOnItemClickListener;

import java.text.DecimalFormat;
import java.util.List;


public class AdapterHistoryNumber extends RecyclerView.Adapter<AdapterHistoryNumber.ContactNumberViewHolder> {
    private List<FavoriteNumber> historyNumberList;
    private IOnItemClickListener<Integer> onItemClickListener;

    public void setOnItemClickListener(IOnItemClickListener<Integer> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public AdapterHistoryNumber(List<FavoriteNumber> historyNumberList) {
        this.historyNumberList = historyNumberList;
    }

    @NonNull
    @Override
    public ContactNumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_charge_history, parent, false);
        return new ContactNumberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactNumberViewHolder holder, int position) {
        holder.bindNUmber(historyNumberList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return historyNumberList.size();
    }

    class ContactNumberViewHolder extends RecyclerView.ViewHolder {
        private TextView amount;
        private TextView phoneNumber;

        ContactNumberViewHolder(@NonNull View itemView) {
            super(itemView);
            phoneNumber = itemView.findViewById(R.id.amount_contact);
            amount = itemView.findViewById(R.id.number_contact);

            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClicked(getAdapterPosition());
                }
            });
        }

        void bindNUmber(FavoriteNumber historyNumber, int position) {
            phoneNumber.setText(historyNumber.getPhoneNumber());
            DecimalFormat df = new DecimalFormat(",###");
            if (historyNumber.getAmount() == null)
                return;

            String price = df.format(historyNumber.getAmount());

            amount.setText(String.format("%s %s", price, itemView.getContext().getResources().getString(R.string.rial)));
        }
    }

    public List<FavoriteNumber> getHistoryNumberList() {
        return historyNumberList;
    }
}
