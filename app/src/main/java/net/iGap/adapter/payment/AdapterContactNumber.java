package net.iGap.adapter.payment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;

import java.util.List;

public class AdapterContactNumber extends RecyclerView.Adapter<AdapterContactNumber.HistoryNumberViewHolder> {
    private List<ContactNumber> contactNumbers;
    private int selectedPosition = -1;

    public AdapterContactNumber(List<ContactNumber> contactNumbers) {
        this.contactNumbers = contactNumbers;
    }

    public AdapterContactNumber() {
    }

    public void setContactNumbers(List<ContactNumber> contactNumbers) {
        this.contactNumbers = contactNumbers;
    }

    @NonNull
    @Override
    public HistoryNumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_charge_contact, parent, false);
        return new HistoryNumberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryNumberViewHolder holder, int position) {
        holder.bindNumber(contactNumbers.get(position), position);

    }

    @Override
    public int getItemCount() {
        return contactNumbers.size();
    }


    class HistoryNumberViewHolder extends RecyclerView.ViewHolder {
        private TextView phoneNumber;
        private TextView contactName;

        HistoryNumberViewHolder(@NonNull View itemView) {
            super(itemView);
            phoneNumber = itemView.findViewById(R.id.phone_Number);
            contactName = itemView.findViewById(R.id.contact_Name);
        }

        void bindNumber(ContactNumber amount, int position) {
            contactName.setText(amount.getDisplayName());
            phoneNumber.setText(amount.getPhone());

            itemView.setSelected(selectedPosition == position);

            itemView.setOnClickListener(v -> {
                int tmp = selectedPosition;
                notifyItemChanged(tmp);
                selectedPosition = getAdapterPosition();
                notifyItemChanged(selectedPosition);
            });
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public List<ContactNumber> getContactNumbers() {
        return contactNumbers;
    }
}
