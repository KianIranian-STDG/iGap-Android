package net.iGap.igasht.locationdetail.buyticket;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.dialog.BottomSheetItemClickCallback;

import java.util.List;

public class TicketTypeListAdapter extends RecyclerView.Adapter<TicketTypeListAdapter.ViewHolder> {

    private List<IGashtServiceAmount> items;
    private BottomSheetItemClickCallback itemClickCallback;

    public TicketTypeListAdapter(List<IGashtServiceAmount> items, BottomSheetItemClickCallback itemClickCallback) {
        this.items = items;
        this.itemClickCallback = itemClickCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_list_item_bottom_sheet, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.title.setText(items.get(i).getVoucherType());
        viewHolder.itemView.setOnClickListener(v -> itemClickCallback.onClick(viewHolder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView title;
        ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.itemTitle);
        }
    }
}
