package net.iGap.mobileBank.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;

import java.util.ArrayList;
import java.util.List;

public class TransferMoneyTypeAdapter extends RecyclerView.Adapter<TransferMoneyTypeAdapter.ViewHolder> {

    private List<TransferMoneyTypeModel> mTypes = new ArrayList<>();
    private String[] types = {"پایا", "ساتنا", "عادی"};
    public String mSelectedType;

    public void setTypes() {
        TransferMoneyTypeModel model;
        for (String type : types) {
            model = new TransferMoneyTypeModel();
            model.type = type;
            mTypes.add(model);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_bpm_dialog_transfer_money, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mTypes.get(position).isSelected) {
            holder.rbChecked.setChecked(true);
            holder.root.setBackgroundResource(R.drawable.shape_round_stroke);
        } else {
            holder.rbChecked.setChecked(false);
            holder.root.setBackgroundResource(0);
        }

        holder.tvType.setText(mTypes.get(position).type);

        holder.root.setOnClickListener(v -> {
            boolean state = !mTypes.get(holder.getAdapterPosition()).isSelected;
            for (TransferMoneyTypeModel model : mTypes) model.isSelected = false;
            mTypes.get(holder.getAdapterPosition()).isSelected = state;
            mSelectedType = state ? mTypes.get(holder.getAdapterPosition()).type : null;
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return mTypes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private View root;
        private TextView tvType;
        private RadioButton rbChecked;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvType = itemView.findViewById(R.id.tvType);
            rbChecked = itemView.findViewById(R.id.rbCheck);
            root = itemView.findViewById(R.id.lytRoot);
        }
    }

    class TransferMoneyTypeModel {

        public String type;
        public boolean isSelected;

    }
}
