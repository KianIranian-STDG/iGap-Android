package net.iGap.fragments.mplTranaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.messenger.theme.Theme;

import java.util.ArrayList;
import java.util.List;

class MplTransactionInfoAdapter extends RecyclerView.Adapter<MplTransactionInfoAdapter.MplTransactionInfoViewHolder> {

    private List<MplTransactionInfoFragment.MilTransActionStruct> data = new ArrayList<>();

    public void setData(List<MplTransactionInfoFragment.MilTransActionStruct> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MplTransactionInfoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_mpl_transactin_info, viewGroup, false);
        return new MplTransactionInfoViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull MplTransactionInfoViewHolder mplTransActionInfoViewHolder, int i) {
        mplTransActionInfoViewHolder.bindTransaction(data.get(i));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MplTransactionInfoViewHolder extends RecyclerView.ViewHolder {
        private TextView dataTv;
        private TextView titleTv;
        private View line;

        public MplTransactionInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            dataTv = itemView.findViewById(R.id.tv_itemMplTransActionInfo_data);
            dataTv.setTextColor(Theme.getColor(Theme.key_default_text));
            titleTv = itemView.findViewById(R.id.tv_itemMplTransActionInfo_title);
            titleTv.setTextColor(Theme.getColor(Theme.key_default_text));
            line = itemView.findViewById(R.id.line);
            line.setBackgroundColor(Theme.getColor(Theme.key_default_text));
        }

        public void bindTransaction(MplTransactionInfoFragment.MilTransActionStruct data) {
            titleTv.setText(data.getTitle());
            dataTv.setText(data.getData());
        }
    }
}
