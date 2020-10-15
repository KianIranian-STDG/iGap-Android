package net.iGap.adapter.kuknos;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.kuknos.Model.Parsian.KuknosAsset;

import java.util.List;

public class AddAssetAdvAdapter extends RecyclerView.Adapter<AddAssetAdvAdapter.ViewHolder> {

    private List<KuknosAsset.Asset> mdata;
    private Context context;
    private OnItemClickListener listener;

    private int itemMargin = 0;
    private int itemWidth = 0;
    private DisplayMetrics metrics;

    public AddAssetAdvAdapter(List<KuknosAsset.Asset> data, Context context, DisplayMetrics metrics) {
        this.mdata = data;
        this.context = context;
        this.metrics = metrics;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_kuknos_add_asset_ad_cell, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        int currentItemWidth = itemWidth;
        if (position == 0) {
            currentItemWidth += itemMargin;
            viewHolder.itemView.setPadding(itemMargin, 0, 0, 0);
        } else if (position == getItemCount() - 1) {
            currentItemWidth += itemMargin;
            viewHolder.itemView.setPadding(0, 0, itemMargin, 0);
        }

        int height = viewHolder.itemView.getLayoutParams().height;
        viewHolder.itemView.setLayoutParams(new ViewGroup.LayoutParams(currentItemWidth, height));
        viewHolder.initView(position);
    }

    @Override
    public int getItemCount() {
        if (mdata.size() < 3)
            return mdata.size();
        else
            return 3;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView subTitle;
        private TextView desc;
        private ImageView icon;
        private CardView container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.cellAddAssetTitle);
            subTitle = itemView.findViewById(R.id.cellAddAssetSubTitle);
            desc = itemView.findViewById(R.id.cellAddAssetDesc);
            icon = itemView.findViewById(R.id.cellAddAssetImage);
            container = itemView.findViewById(R.id.cellAddAssetContainer);

        }

        public void initView(int position) {
            title.setText(mdata.get(position).getLabel());
            subTitle.setText(mdata.get(position).getAssetIssuer());
            container.setOnClickListener(v -> listener.onItemClick(position));
            // TODO: 8/18/2019 load from api for adv
            /*desc.setText("");
            Picasso.get()
                    .load(R.drawable.icon_igap)
                    .into(icon);*/
        }
    }

    public void setItemMargin(int itemMargin) {
        this.itemMargin = itemMargin;
    }

    public void updateDisplayMetrics() {
        itemWidth = metrics.widthPixels - itemMargin * 2;
    }

    public interface OnItemClickListener {
        void onItemClick(int item);
    }
}
