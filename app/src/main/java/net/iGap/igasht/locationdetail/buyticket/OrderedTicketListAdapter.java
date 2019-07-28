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

public class OrderedTicketListAdapter extends RecyclerView.Adapter<OrderedTicketListAdapter.ViewHolder> {

    private List<IGashtServiceAmount> items;
    private BottomSheetItemClickCallback removeItemCallback;

    public OrderedTicketListAdapter(List<IGashtServiceAmount> items, BottomSheetItemClickCallback removeItemCallback) {
        this.items = items;
        this.removeItemCallback = removeItemCallback;
    }

    public void addNewItem(IGashtServiceAmount newItem) {
        items.add(newItem);
        notifyItemInserted(items.size() - 1);
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_list_item_igasht_ticket_order, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.title.setText(String.format(viewHolder.itemView.getContext().getString(R.string.igasht_order_ticket_title), items.get(i).getTitle(), items.get(i).getCount()));
        viewHolder.remove.setOnClickListener(v -> removeItemCallback.onClick(viewHolder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView title;
        private AppCompatTextView remove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            remove = itemView.findViewById(R.id.remove);
        }
    }
}
