package net.iGap.adapter.igahst;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.model.igasht.IGashtLocationService;
import net.iGap.observers.interfaces.TicketListCountChangeListener;

import java.util.List;

public class OrderedTicketListAdapter extends RecyclerView.Adapter<OrderedTicketListAdapter.ViewHolder> {

    private List<IGashtLocationService> items;
    private int entrancePosition = -1;
    private TicketListCountChangeListener listCountChangeListener;

    public OrderedTicketListAdapter(List<IGashtLocationService> items, TicketListCountChangeListener ticketListCountChangeListener) {
        this.items = items;
        this.listCountChangeListener = ticketListCountChangeListener;
    }

    public void addNewItem(List<IGashtLocationService> newItem) {
        items.addAll(newItem);
        notifyItemInserted(items.size() - 1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_list_item_igasht_ticket_order, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if (items.get(position).getModelId().equals("1")) {
            entrancePosition = viewHolder.getAdapterPosition();
        }

        viewHolder.ticketTitle.setText(items.get(position).getServiceNameWithLanguage());
        viewHolder.ticketCount.setText(items.get(position).getCount() + "");
        viewHolder.ticketPrice.setText(items.get(position).getCount() * items.get(position).getPersianTicket().getAmount() + "");

        viewHolder.plusButton.setOnClickListener(v -> {
            items.get(viewHolder.getAdapterPosition()).setCount(items.get(viewHolder.getAdapterPosition()).getCount() + 1);
            notifyItemChanged(viewHolder.getAdapterPosition());
            updateEntranceItemCount();
        });

        viewHolder.minusButton.setOnClickListener(v -> {
            if (items.get(viewHolder.getAdapterPosition()).getCount() > 0) {
                items.get(viewHolder.getAdapterPosition()).setCount(items.get(viewHolder.getAdapterPosition()).getCount() - 1);
                notifyItemChanged(viewHolder.getAdapterPosition());
                updateEntranceItemCount();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    private void updateEntranceItemCount() {
        if (entrancePosition > -1) {
            int maxTicketCount = findMaxTicketCount();
            if (maxTicketCount != -1 && items.get(entrancePosition).getCount() < maxTicketCount) {
                items.get(entrancePosition).setCount(maxTicketCount);
                notifyItemChanged(entrancePosition);
            }
        }
    }

    private int findMaxTicketCount() {
        int t = -1;
        int totalPrice = 0;
        for (int i = 0; i < getItemCount(); i++) {
            if (i != entrancePosition) {
                if (items.get(i).getCount() > t) {
                    t = items.get(i).getCount();
                }
            }
            totalPrice += (items.get(i).getCount() * items.get(i).getPersianTicket().getAmount());
        }
        listCountChangeListener.setTicketTotalPrice(totalPrice);
        return t;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView ticketTitle;
        private AppCompatTextView ticketCount;
        private AppCompatTextView ticketPrice;
        private View plusButton;
        private View minusButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ticketTitle = itemView.findViewById(R.id.service_ticket);
            ticketCount = itemView.findViewById(R.id.count_ticket);
            ticketPrice = itemView.findViewById(R.id.price_ticket);
            minusButton = itemView.findViewById(R.id.minusButton);
            plusButton = itemView.findViewById(R.id.plusButton);
        }
    }
}
