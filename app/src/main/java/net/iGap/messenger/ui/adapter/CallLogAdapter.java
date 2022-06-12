package net.iGap.messenger.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.controllers.MessageController;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.ui.cell.CallLogCell;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.realm.RealmCallLog;

import java.util.List;

public class CallLogAdapter extends RecyclerListView.SelectionAdapter {
    private final int currentAccount;
    private final int type;
    private final Context context;
    private boolean isCallMultiSelectEnable;

    public CallLogAdapter(Context context, int type) {
        currentAccount = AccountManager.selectedAccount;
        this.context = context;
        this.type = type;
    }

    @Override
    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        return true;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CallLogCell callLogCell = new CallLogCell(context);
        callLogCell.setTextColor(Theme.getColor(Theme.key_default_text));
        return new RecyclerListView.Holder(callLogCell);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CallLogCell callLogCell = (CallLogCell) holder.itemView;
        RealmCallLog callLog = getItem(position);
        if (callLog != null) {
            callLogCell.setValue(callLog, isCallMultiSelectEnable);
        }
    }

    public void setMultiSelect(boolean isCallMultiSelectEnable) {
        this.isCallMultiSelectEnable = isCallMultiSelectEnable;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return getCallLogs().size();
    }

    public RealmCallLog getItem(int i) {
        return getCallLogs().get(i);
    }

    public List<RealmCallLog> getCallLogs() {
        return MessageController.getInstance(currentAccount).getCallLogList(type);
    }
}
