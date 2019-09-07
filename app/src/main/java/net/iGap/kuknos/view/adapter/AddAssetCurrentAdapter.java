package net.iGap.kuknos.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.iGap.R;

import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.AssetResponse;

import java.util.ArrayList;
import java.util.List;

public class AddAssetCurrentAdapter extends RecyclerView.Adapter<AddAssetCurrentAdapter.ViewHolder> {

    private ArrayList<AccountResponse.Balance> mdata;
    private Context context;

    public AddAssetCurrentAdapter(List<AccountResponse.Balance> data, Context context) {
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

        private TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.cellAddAssetTitle);

        }

        public void initView(AccountResponse.Balance model) {
            title.setText(model.getAsset().getType().equals("native") ? "PMN" : model.getAssetCode());
        }
    }

}
