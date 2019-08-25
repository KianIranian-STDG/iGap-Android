package net.iGap.fragments.mplTranaction;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iGap.R;

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

        public MplTransactionInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            dataTv = itemView.findViewById(R.id.tv_itemMplTransActionInfo_data);
            titleTv = itemView.findViewById(R.id.tv_itemMplTransActionInfo_title);
        }

        public void bindTransaction(MplTransactionInfoFragment.MilTransActionStruct data) {
            titleTv.setText(data.getTitle());
            dataTv.setText(data.getData());
        }
    }
}
