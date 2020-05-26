package net.iGap.adapter.payment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdapterContactNumber extends RecyclerView.Adapter<AdapterContactNumber.HistoryNumberViewHolder> {
    private List<ContactNumber> contactNumbers;
    private List<ContactNumber> searchedNumbers;
    private boolean shouldSearch = false;
    private int selectedPosition = -1;

    public void search(String search) {
        if (contactNumbers == null || contactNumbers.size() == 0)
            return;

        searchedNumbers = new ArrayList<>();
        search = search.trim().toLowerCase();
        for (int i = 0; i < contactNumbers.size(); i++) {
            String keyToSearch = contactNumbers.get(i).getFirstName() + contactNumbers.get(i).getLastName() + contactNumbers.get(i).getDisplayName() + contactNumbers.get(i).getPhone();
            keyToSearch = keyToSearch.trim().toLowerCase();
            if (keyToSearch.contains(search)) {
                searchedNumbers.add(contactNumbers.get(i));
            }
        }
        shouldSearch = true;
        notifyDataSetChanged();
    }

    public void setContactNumbers(List<ContactNumber> contactNumbers) {
        this.contactNumbers = contactNumbers;
        Collections.sort(this.contactNumbers, (o1, o2) -> o1.getDisplayName().compareTo(o2.getDisplayName()));
    }

    @NonNull
    @Override
    public HistoryNumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_charge_contact, parent, false);
        return new HistoryNumberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryNumberViewHolder holder, int position) {
        if (shouldSearch)
            holder.bindNumber(searchedNumbers.get(position), position);
        else
            holder.bindNumber(contactNumbers.get(position), position);

    }

    @Override
    public int getItemCount() {
        return shouldSearch ? searchedNumbers == null ? 0 : searchedNumbers.size() : contactNumbers == null ? 0 : contactNumbers.size();
    }


    class HistoryNumberViewHolder extends RecyclerView.ViewHolder {
        private TextView phoneNumber;
        private TextView contactName;

        HistoryNumberViewHolder(@NonNull View itemView) {
            super(itemView);
            phoneNumber = itemView.findViewById(R.id.phone_Number);
            contactName = itemView.findViewById(R.id.contact_Name);

            itemView.setOnClickListener(v -> {
                int tmp = selectedPosition;
                if (tmp >= 0)
                    notifyItemChanged(tmp);
                selectedPosition = getAdapterPosition();
                notifyItemChanged(selectedPosition);
            });
        }

        void bindNumber(ContactNumber amount, int position) {
            contactName.setText(amount.getDisplayName());
            phoneNumber.setText(amount.getPhone().replace(" ", "").replace("+98", "0"));

            itemView.setSelected(selectedPosition == position);
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public List<ContactNumber> getContactNumbers() {
        if (shouldSearch)
            return searchedNumbers;
        return contactNumbers;
    }
}
