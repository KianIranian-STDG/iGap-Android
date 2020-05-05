package net.iGap.adapter.payment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterContactNumber extends RecyclerView.Adapter<AdapterContactNumber.HistoryNumberViewHolder> {
    private List<ContactNumber> amountList = new ArrayList<>();
    private int selectedPosition;

    public AdapterContactNumber() {
        ContactNumber amount = new ContactNumber();
        amount.setContactNumber("0912912912912");
        amountList.add(amount);
        ContactNumber amount1 = new ContactNumber();
        amount1.setContactNumber("0935935935");
        amountList.add(amount1);
        ContactNumber amount2 = new ContactNumber();
        amount2.setContactNumber("0919919919");
        amountList.add(amount2);
        ContactNumber amount3 = new ContactNumber();
        amount3.setContactNumber("0935935935935");
        amountList.add(amount3);
        ContactNumber amount4 = new ContactNumber();
        amount4.setContactNumber("0937937937937");
        amountList.add(amount4);
        ContactNumber amount5 = new ContactNumber();
        amount5.setContactNumber("0938938938938");
        amountList.add(amount5);

    }

    @NonNull
    @Override
    public HistoryNumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_charge_contact, parent, false);
        return new HistoryNumberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryNumberViewHolder holder, int position) {
        holder.bindNumber(amountList.get(position));

    }

    @Override
    public int getItemCount() {
        return amountList.size();
    }


    public class HistoryNumberViewHolder extends RecyclerView.ViewHolder {
        private TextView phoneNumber;
        private TextView contactName;

        public HistoryNumberViewHolder(@NonNull View itemView) {
            super(itemView);
            phoneNumber = itemView.findViewById(R.id.phone_Number);
            contactName = itemView.findViewById(R.id.contact_Name);

            itemView.setOnClickListener(v -> {
                selectedPosition = getAdapterPosition();
                notifyDataSetChanged();
            });

        }

        public void bindNumber(ContactNumber amount) {
            contactName.setText(amount.getContactNumber());
            phoneNumber.setText("نازنین عمرانی");
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public List<ContactNumber> getAmountList() {
        return amountList;
    }
}
