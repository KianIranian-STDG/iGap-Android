package net.iGap.igasht.favoritelocation;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import net.iGap.R;
import net.iGap.dialog.BottomSheetItemClickCallback;

import java.util.List;

public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.ViewHolder> {

    private List<String> items;
    private BottomSheetItemClickCallback clickCallback;

    public FavoriteListAdapter(BottomSheetItemClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    public void setItems(List<String> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_list_item_igasht_favorite, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.title.setText("برج میلاد");
        viewHolder.likeCount.setText("126");
        Picasso.get().load("test").error(R.drawable.ic_error).centerInside().fit().into(viewHolder.image);
        viewHolder.itemView.setOnClickListener(v -> clickCallback.onClick(viewHolder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView title;
        private AppCompatTextView likeCount;
        private AppCompatImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.itemImage);
            likeCount = itemView.findViewById(R.id.itemLikeCount);
            title = itemView.findViewById(R.id.itemTitle);
        }
    }
}
