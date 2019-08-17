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

class MplTransActionInfoAdapter extends RecyclerView.Adapter<MplTransActionInfoAdapter.MplTransActionInfoViewHolder> {

    private List<String> strings = new ArrayList<>();

    public void setInfo(List<String> strings) {
        this.strings = strings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MplTransActionInfoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_mpl_transactin_info, viewGroup, false);
        return new MplTransActionInfoViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull MplTransActionInfoViewHolder mplTransActionInfoViewHolder, int i) {
        mplTransActionInfoViewHolder.bindTransaction(strings.get(i));
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    public class MplTransActionInfoViewHolder extends RecyclerView.ViewHolder {
        private TextView dataTv;

        public MplTransActionInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            dataTv = itemView.findViewById(R.id.tv_itemMplTransActionInfo_data);
        }

        public void bindTransaction(String string) {
            dataTv.setText(string);
        }
    }
}
