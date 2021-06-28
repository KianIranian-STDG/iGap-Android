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
import net.iGap.helper.HelperError;
import net.iGap.kuknos.Model.Parsian.KuknosAsset;

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

        private TextView title, icon;
        private CardView container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.cellAddAssetTitle);
            container = itemView.findViewById(R.id.cellAddAssetContainer);
            icon = itemView.findViewById(R.id.addIcon);

        }

        public void initView(int position) {
            title.setText(mdata.get(position).getAsset().getType().equals("native") ? "PMN" : mdata.get(position).getAssetCode());
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mdata.get(position).isTrusted())
                        listener.onAdd(position);
                    else
                        HelperError.showSnackMessage(context.getResources().getString(R.string.kuknos_addAsset_alreadyTrusted), true);
                }
            });
            if (mdata.get(position).isTrusted())
                icon.setText(R.string.icon_sent);
        }
    }

    public interface onClickListener {
        void onAdd(int position);
    }
}
