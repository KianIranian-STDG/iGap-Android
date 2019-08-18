package net.iGap.igasht.locationlist;

import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.squareup.picasso.Picasso;

import net.iGap.R;

import java.util.List;

public class IGashtLocationListAdapter extends RecyclerView.Adapter<IGashtLocationListAdapter.ViewHolder> {

    private List<IGashtLocationItem> items;
    private onLocationItemClickListener locationItemClickListener;

    public void setItems(List<IGashtLocationItem> items, onLocationItemClickListener locationItemClickListener) {
        this.items = items;
        this.locationItemClickListener = locationItemClickListener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custome_row_igasht_location, viewGroup, false));
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.itemTitleTextView.setText(items.get(i).getNameWithLanguage());
        viewHolder.itemAddressTextView.setText(items.get(i).getAddressWithLanguage());
      /*  viewHolder.itemLikeCountTextView.setText("126");*/
        viewHolder.itemLocationTextView.setText(items.get(i).getLocation());
        Picasso.get().load("test").placeholder(R.drawable.logo).error(R.drawable.ic_error_igap).fit().centerCrop().into(viewHolder.itemImageView);
//        viewHolder.addToFavoriteButton.setOnClickListener(v -> locationItemClickListener.addToFavorite(viewHolder.getAdapterPosition()));
        viewHolder.buyTicketButton.setOnClickListener(v -> locationItemClickListener.buyTicket(viewHolder.getAdapterPosition()));
        viewHolder.itemView.setOnClickListener(v -> locationItemClickListener.onItem(viewHolder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatImageView itemImageView;
        private AppCompatTextView itemTitleTextView;
        private AppCompatTextView itemAddressTextView;
        /*  private AppCompatTextView itemLikeCountTextView;
         private AppCompatTextView itemVisitTimeTextView;
          private AppCompatTextView itemPriceTextView;
          private AppCompatTextView itemPriceForeignTextView;
        private MaterialButton addToFavoriteButton;*/
        private AppCompatTextView itemLocationTextView;
        private Button buyTicketButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImageView = itemView.findViewById(R.id.itemImage);
            itemTitleTextView = itemView.findViewById(R.id.itemTitle);
            itemAddressTextView = itemView.findViewById(R.id.itemAddress);
/*            itemLikeCountTextView = itemView.findViewById(R.id.itemLikeCount);
            addToFavoriteButton = itemView.findViewById(R.id.addToFavoriteButton);*/
            itemLocationTextView = itemView.findViewById(R.id.itemLocation);
            buyTicketButton = itemView.findViewById(R.id.buyTicketButton);
        }
    }

    public interface onLocationItemClickListener {
//        void addToFavorite(int position);

        void buyTicket(int position);

        void onItem(int position);
    }
}
