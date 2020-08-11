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

public class ChargeContactNumberAdapter extends RecyclerView.Adapter<ChargeContactNumberAdapter.HistoryNumberViewHolder> {
    private List<ContactNumber> contactNumbers;
    private List<ContactNumber> searchedNumbers;
    private boolean shouldSearch = false;
    private IOnItemClickListener onItemClickListener;


    public void setOnItemClickListener(IOnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

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
        if (contactNumbers == null || contactNumbers.size() == 0)
            return;

        Collections.sort(contactNumbers, (o1, o2) -> o1.getDisplayName().compareTo(o2.getDisplayName()));
        if (this.contactNumbers == null)
            this.contactNumbers = new ArrayList<>();

        this.contactNumbers.clear();
        int lastIndex = 0;
        this.contactNumbers.add(contactNumbers.get(lastIndex));
        for (int i = 1; i < contactNumbers.size(); i++) {
            if (!contactNumbers.get(lastIndex).getDisplayName().equals(contactNumbers.get(i).getDisplayName())) {
                lastIndex = i;
                this.contactNumbers.add(contactNumbers.get(lastIndex));
            }
        }
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
            holder.bindNumber(searchedNumbers.get(position));
        else
            holder.bindNumber(contactNumbers.get(position));

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
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClicked(getAdapterPosition());
                }
            });
        }

        void bindNumber(ContactNumber amount) {
            contactName.setText(amount.getDisplayName());
            phoneNumber.setText(amount.getPhone().replace(" ", "").replace("+98", "0"));
        }
    }

    public interface IOnItemClickListener {
        void onItemClicked(int position);
    }

    public List<ContactNumber> getContactNumbers() {
        if (shouldSearch)
            return searchedNumbers;
        return contactNumbers;
    }

}
