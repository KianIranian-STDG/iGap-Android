package net.iGap.adapter.payment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;

public class AdapterContactNumber extends RecyclerView.Adapter<AdapterContactNumber.ContactNumberViewHolder> {

    @NonNull
    @Override
    public ContactNumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_charge_contact, parent, false);
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
        }

        public void bindNUmber() {
            textView.setText("091212345678");
        }
    }
}
