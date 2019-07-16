package net.iGap.igasht;

import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import net.iGap.R;

import java.util.List;

public class IGashtLocationListAdapter extends RecyclerView.Adapter<IGashtLocationListAdapter.ViewHolder> {

    private List<String> items;

    public void setItems(List<String> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custome_row_igasht_location, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.itemTitleTextView.setText("برج میلاد");
        viewHolder.itemDestantTextView.setText("فاصله 2.3 کیلوکتر");
        viewHolder.itemLikeCountTextView.setText("126");
        viewHolder.itemVisitTimeTextView.setText("ساعت بازدید: 9 الی 23");
        viewHolder.itemPriceTextView.setText("قیمت برای هر نفر: 25000 تومان");
        viewHolder.itemPriceForeignTextView.setText("قیمت برای گردشگر خارجی: 2 دلار");
        Picasso.get().load("test").placeholder(R.drawable.logo).error(R.drawable.ic_error_igap).fit().centerCrop().into(viewHolder.itemImageView);
        viewHolder.addToFavoriteButton.setOnClickListener(v -> {

        });
        viewHolder.buyTicketButton.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatImageView itemImageView;
        private AppCompatTextView itemTitleTextView;
        private AppCompatTextView itemDestantTextView;
        private AppCompatTextView itemLikeCountTextView;
        private AppCompatTextView itemVisitTimeTextView;
        private AppCompatTextView itemPriceTextView;
        private AppCompatTextView itemPriceForeignTextView;
        private MaterialButton addToFavoriteButton;
        private MaterialButton buyTicketButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImageView = itemView.findViewById(R.id.itemImage);
            itemTitleTextView = itemView.findViewById(R.id.itemTitle);
            itemDestantTextView = itemView.findViewById(R.id.itemDestination);
            itemLikeCountTextView = itemView.findViewById(R.id.itemLikeCount);
            itemVisitTimeTextView = itemView.findViewById(R.id.itemVisitTime);
            itemPriceTextView = itemView.findViewById(R.id.itemPrice);
            itemPriceForeignTextView = itemView.findViewById(R.id.itemPriceForeign);
            addToFavoriteButton = itemView.findViewById(R.id.addToFavoriteButton);
            buyTicketButton = itemView.findViewById(R.id.buyTicketButton);
        }
    }
}
