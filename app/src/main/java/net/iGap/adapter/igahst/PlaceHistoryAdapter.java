package net.iGap.adapter.igahst;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.model.igasht.IGashtTicketDetail;
import net.iGap.module.dialog.BottomSheetItemClickCallback;

import java.util.List;

public class PlaceHistoryAdapter extends RecyclerView.Adapter<PlaceHistoryAdapter.ViewHolder> {

    private List<IGashtTicketDetail> items;
    private BottomSheetItemClickCallback clickCallback;

    public PlaceHistoryAdapter(BottomSheetItemClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    public void setItems(List<IGashtTicketDetail> items) {
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
        viewHolder.itemView.setOnClickListener(v -> clickCallback.onClick(viewHolder.getAdapterPosition()));
        viewHolder.date.setText(HelperCalander.getClocktime((long) items.get(i).getTicketInfo().getCreated(), false));
        viewHolder.detail.setText(items.get(i).getTicketInfo().getLocationNameWithLanguage());
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView date;
        private AppCompatTextView detail;
        /*private AppCompatTextView barCode;*/

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            detail = itemView.findViewById(R.id.itemDetail);
            /*barCode = itemView.findViewById(R.id.barCode);*/
            date = itemView.findViewById(R.id.itemDate);
        }
    }
}
