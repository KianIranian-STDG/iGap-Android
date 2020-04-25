package net.iGap.adapter.kuknos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.model.kuknos.Parsian.KuknosAsset;

import java.util.ArrayList;
import java.util.List;

public class AddAllAssetAdapter extends RecyclerView.Adapter<AddAllAssetAdapter.ViewHolder> {

    private ArrayList<KuknosAsset.Asset> mdata;
    private Context context;
    private onClickListener listener;

    public AddAllAssetAdapter(List<KuknosAsset.Asset> data, Context context, onClickListener listener) {
        if (this.mdata == null)
            this.mdata = new ArrayList<>();
        this.mdata.addAll(data);
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_kuknos_add_asset_cell, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.initView(position);
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private CardView container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.cellAddAssetTitle);
            container = itemView.findViewById(R.id.cellAddAssetContainer);

        }

        public void initView(int position) {
            title.setText(mdata.get(position).getAsset().getType().equals("native") ? "PMN" : mdata.get(position).getAssetCode());
            container.setOnClickListener(v -> listener.onAdd(position));
        }
    }

    public interface onClickListener {
        void onAdd(int position);
    }
}
