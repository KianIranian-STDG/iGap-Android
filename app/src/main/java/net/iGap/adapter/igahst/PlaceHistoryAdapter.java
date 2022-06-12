package net.iGap.adapter.igahst;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.messenger.theme.Theme;
import net.iGap.model.igasht.IGashtTicketDetail;
import net.iGap.module.dialog.BottomSheetItemClickCallback;

import java.util.List;

public class PlaceHistoryAdapter extends RecyclerView.Adapter<PlaceHistoryAdapter.ViewHolder> {

    private List<IGashtTicketDetail> items;
    private BottomSheetItemClickCallback clickCallback;
    private Context context;

    public PlaceHistoryAdapter(BottomSheetItemClickCallback clickCallback,Context context) {
        this.clickCallback = clickCallback;
        this.context = context;
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
        viewHolder.barCode.setBackground(Theme.tintDrawable(ContextCompat.getDrawable(context, R.drawable.shape_igasht_yellow), context, Theme.getColor(Theme.key_theme_color)));
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
        private AppCompatTextView barCode;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            detail = itemView.findViewById(R.id.itemDetail);
            detail.setTextColor(Theme.getColor(Theme.key_title_text));
            barCode = itemView.findViewById(R.id.barCode);
            date = itemView.findViewById(R.id.itemDate);
            date.setTextColor(Theme.getColor(Theme.key_title_text));
        }
    }
}
