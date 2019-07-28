package net.iGap.igasht.historylocation;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.dialog.BottomSheetItemClickCallback;

import java.util.List;

public class PlaceHistoryAdapter extends RecyclerView.Adapter<PlaceHistoryAdapter.ViewHolder> {

    private List<String> items;
    private BottomSheetItemClickCallback clickCallback;

    public PlaceHistoryAdapter(BottomSheetItemClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    public void setItems(List<String> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_list_item_igasht_history, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.barCode.setOnClickListener(v -> clickCallback.onClick(viewHolder.getAdapterPosition()));

        viewHolder.date.setText("تاریخ خرید");
        viewHolder.detail.setText("مشخصات خرید");
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView date;
        private AppCompatTextView detail;
        private AppCompatTextView barCode;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            detail = itemView.findViewById(R.id.itemDetail);
            barCode = itemView.findViewById(R.id.barCode);
            date = itemView.findViewById(R.id.itemDate);
        }
    }
}
