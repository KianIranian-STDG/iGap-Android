package net.iGap.adapter.kuknos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.kuknos.Model.Parsian.KuknosBalance;

import java.util.ArrayList;
import java.util.List;

public class AddAssetCurrentAdapter extends RecyclerView.Adapter<AddAssetCurrentAdapter.ViewHolder> {

    private ArrayList<KuknosBalance.Balance> mdata;
    private Context context;

    public AddAssetCurrentAdapter(List<KuknosBalance.Balance> data, Context context) {
        if (this.mdata == null)
            this.mdata = new ArrayList<>();
        this.mdata.addAll(data);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_kuknos_add_asset_cell, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.initView(mdata.get(position));
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title, icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.cellAddAssetTitle);
            icon = itemView.findViewById(R.id.addIcon);

        }

        public void initView(KuknosBalance.Balance model) {
            title.setText(model.getAsset().getType().equals("native") ? "PMN" : model.getAssetCode());
            icon.setVisibility(View.GONE);
        }
    }

}
