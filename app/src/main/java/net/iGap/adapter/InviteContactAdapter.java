package net.iGap.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.messenger.ui.cell.UnRegisteredContactCell;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.module.structs.StructListOfContact;

import java.util.ArrayList;
import java.util.List;

public class InviteContactAdapter extends RecyclerListView.SelectionAdapter {

    private List<StructListOfContact> contacts;
    private final Context context;

    public InviteContactAdapter(Context context) {
        this.context = context;
        contacts = new ArrayList<>();
    }

    public void setContacts(List<StructListOfContact> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    @Override
    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        return true;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(new UnRegisteredContactCell(context));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UnRegisteredContactCell contactCell = (UnRegisteredContactCell) holder.itemView;
        contactCell.setValues(contacts.get(position));
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);
            itemView.setOnClickListener(v -> {
                try {
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + contacts.get(getAdapterPosition()).getPhone()));
                    smsIntent.putExtra("sms_body", context.getResources().getString(R.string.invitation_message) + "+" + AccountManager.getInstance().getCurrentUser().getPhoneNumber());
                    smsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    G.context.startActivity(smsIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, context.getString(R.string.device_dosenot_support), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}