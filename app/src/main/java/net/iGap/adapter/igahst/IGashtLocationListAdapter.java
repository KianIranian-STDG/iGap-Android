package net.iGap.adapter.igahst;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.iGap.G;
import net.iGap.R;
import net.iGap.model.igasht.IGashtLocationItem;

import java.util.List;

public class IGashtLocationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<IGashtLocationItem> items;
    private onLocationItemClickListener locationItemClickListener;
    private String provinceSelectedName;

    public IGashtLocationListAdapter(String provinceSelectedName, onLocationItemClickListener locationItemClickListener) {
        this.provinceSelectedName = provinceSelectedName;
        this.locationItemClickListener = locationItemClickListener;
    }

    public void setItems(List<IGashtLocationItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == -1) {
            return new ViewHolderProvinceInfo(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custome_row_igasht_province_info, viewGroup, false));
        } else {
            return new ViewHolderLocationItem(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custome_row_igasht_location, viewGroup, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? -1 : -2;
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ViewHolderProvinceInfo) {
            ((ViewHolderProvinceInfo) viewHolder).locationItemCount.setText(String.format(((ViewHolderProvinceInfo) viewHolder).locationItemCount.getContext().getString(R.string.igasht_location_found_count), (getItemCount() - 1)));
            ((ViewHolderProvinceInfo) viewHolder).provinceName.setText(String.format(((ViewHolderProvinceInfo) viewHolder).provinceName.getContext().getString(R.string.igasht_selected_location_title), provinceSelectedName));
        } else if (viewHolder instanceof ViewHolderLocationItem) {
            ((ViewHolderLocationItem) viewHolder).itemTitleTextView.setText(items.get(i - 1).getNameWithLanguage());
            ((ViewHolderLocationItem) viewHolder).itemAddressTextView.setText(items.get(i - 1).getAddressWithLanguage());
            ((ViewHolderLocationItem) viewHolder).itemLocationTextView.setText(items.get(i - 1).getLocation());
            if (items.get(i - 1).getmExtraDetail() != null) {
                Glide.with(G.context)
                        .load(items.get(i - 1).getmExtraDetail().getmCoverImage())
                        .placeholder(R.drawable.logo_igap_small)
                        .error(R.drawable.ic_error_igap)
                        .fitCenter()
                        .centerInside()
                        .into(((ViewHolderLocationItem) viewHolder).itemImageView);
            } else {
                Glide.with(G.context).load(R.drawable.logo_igap_small).into(((ViewHolderLocationItem) viewHolder).itemImageView);
            }
            ((ViewHolderLocationItem) viewHolder).buyTicketButton.setOnClickListener(v -> locationItemClickListener.buyTicket(viewHolder.getAdapterPosition() - 1));
            viewHolder.itemView.setOnClickListener(v -> locationItemClickListener.onItem(viewHolder.getAdapterPosition() - 1));
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() + 1 : 1;
    }

    class ViewHolderLocationItem extends RecyclerView.ViewHolder {

        private AppCompatImageView itemImageView;
        private AppCompatTextView itemTitleTextView;
        private AppCompatTextView itemAddressTextView;
        private AppCompatTextView itemLocationTextView;
        private Button buyTicketButton;

        public ViewHolderLocationItem(@NonNull View itemView) {
            super(itemView);

            itemImageView = itemView.findViewById(R.id.itemImage);
            itemTitleTextView = itemView.findViewById(R.id.item_title);
            itemAddressTextView = itemView.findViewById(R.id.itemAddress);
            itemLocationTextView = itemView.findViewById(R.id.itemLocation);
            buyTicketButton = itemView.findViewById(R.id.buyTicketButton);
        }
    }

    class ViewHolderProvinceInfo extends RecyclerView.ViewHolder {

        private AppCompatTextView provinceName;
        private AppCompatTextView locationItemCount;

        public ViewHolderProvinceInfo(@NonNull View itemView) {
            super(itemView);
            provinceName = itemView.findViewById(R.id.provinceName);
            locationItemCount = itemView.findViewById(R.id.locationCount);
        }
    }

    public interface onLocationItemClickListener {

        void buyTicket(int position);

        void onItem(int position);
    }
}
