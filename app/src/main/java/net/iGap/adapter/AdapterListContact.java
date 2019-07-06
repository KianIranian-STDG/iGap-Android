package net.iGap.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.module.structs.StructListOfContact;

import java.util.List;

public class AdapterListContact extends RecyclerView.Adapter<AdapterListContact.ViewHolder> {

    public String item;
    public String phone;
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

        public ViewHolder(View view) {
            super(view);

            rootView = view.findViewById(R.id.liContactItem);
            title = view.findViewById(R.id.title);
            subtitle = view.findViewById(R.id.subtitle);

        }

        void initView(StructListOfContact contact) {
            title.setText(contact.getDisplayName());
            subtitle.setText(contact.getPhone());

            rootView.setOnClickListener(v -> new MaterialDialog.Builder(G.fragmentActivity)
                    .title(G.fragmentActivity.getResources()
                            .getString(R.string.igap))
                    .content(G.fragmentActivity.getResources().getString(R.string.invite_friend))
                    .positiveText(G.fragmentActivity.getResources().getString(R.string.ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.cancel))
                    .onPositive((dialog, which) -> {

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra("address", phone);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.invitation_message) + G.userId);
                        sendIntent.setType("text/plain");
                        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        G.context.startActivity(sendIntent);
                    }).show());
        }
    }
}
