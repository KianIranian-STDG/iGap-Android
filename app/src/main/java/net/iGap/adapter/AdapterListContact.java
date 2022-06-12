package net.iGap.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.messenger.theme.Theme;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.G;
import net.iGap.R;
import net.iGap.module.structs.StructListOfContact;

import java.util.List;

public class AdapterListContact extends RecyclerView.Adapter<AdapterListContact.ViewHolder> {

    public String item;
    private List<StructListOfContact> mPhoneContactList;
    private Context context;

    public AdapterListContact(List<StructListOfContact> mPhoneContactList, Context context) {
        this.mPhoneContactList = mPhoneContactList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_list_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.initView(mPhoneContactList.get(position));
    }


    @Override
    public int getItemCount() {
        return mPhoneContactList.size();
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView subtitle;
        private ViewGroup rootView;
        private View line;

        public ViewHolder(View view) {
            super(view);

            rootView = view.findViewById(R.id.liContactItem);
            title = view.findViewById(R.id.title);
            title.setTextColor(Theme.getColor(Theme.key_title_text));
            subtitle = view.findViewById(R.id.subtitle);
            subtitle.setTextColor(Theme.getColor(Theme.key_title_text));
            line = view.findViewById(R.id.topLine);

        }

        void initView(StructListOfContact contact) {

            if (getAdapterPosition() != 0) {
                line.setVisibility(View.VISIBLE);
            } else {
                line.setVisibility(View.GONE);
            }
            title.setText(contact.getDisplayName());
            subtitle.setText(contact.getPhone());

            rootView.setOnClickListener(v -> {
                try {
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + contact.getPhone()));
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
