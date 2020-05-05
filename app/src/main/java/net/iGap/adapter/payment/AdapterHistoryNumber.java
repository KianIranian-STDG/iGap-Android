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

public class AdapterHistoryNumber extends RecyclerView.Adapter<AdapterHistoryNumber.ContactNumberViewHolder> {
    private List<HistoryNumber> historyNumberList = new ArrayList<>();
    private int selectedPosition;

    public AdapterHistoryNumber() {
        HistoryNumber amount = new HistoryNumber();
        amount.setHistoryNumber("091211111111");
        historyNumberList.add(amount);
        HistoryNumber amount1 = new HistoryNumber();
        amount1.setHistoryNumber("09122222222");
        historyNumberList.add(amount1);
        HistoryNumber amount2 = new HistoryNumber();
        amount2.setHistoryNumber("091233333333");
        historyNumberList.add(amount2);
        HistoryNumber amount3 = new HistoryNumber();
        amount3.setHistoryNumber("091244444444");
        historyNumberList.add(amount3);

    }

    @NonNull
    @Override
    public ContactNumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_charge_history, parent, false);
        return new ContactNumberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactNumberViewHolder holder, int position) {
        holder.bindNUmber(historyNumberList.get(position));
    }

    @Override
    public int getItemCount() {
        return historyNumberList.size();
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

        public void bindNUmber(HistoryNumber historyNumber) {
            textView.setText(historyNumber.getHistoryNumber());
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }


    public List<HistoryNumber> getHistoryNumberList() {
        return historyNumberList;
    }
}
