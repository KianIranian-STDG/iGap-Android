package net.iGap.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.model.MultiSelectStruct;

import java.util.ArrayList;
import java.util.List;

public class SelectedItemAdapter extends RecyclerView.Adapter<SelectedItemAdapter.SelectedItemViewHolder> {
    private List<MultiSelectStruct> itemsList = new ArrayList<>();
    private OnMultiSelectItemCallBack callBack;

    public void setItemsList(List<MultiSelectStruct> itemsList) {
        this.itemsList = itemsList;
        notifyDataSetChanged();
    }

    public void setCallBack(OnMultiSelectItemCallBack callBack) {
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public SelectedItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectedItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multi_select, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedItemViewHolder holder, int position) {
        holder.bindData(itemsList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class SelectedItemViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTv;

        public SelectedItemViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.tv_itemMultiSelect);
        }

        void bindData(MultiSelectStruct multiSelect) {
            nameTv.setText(itemView.getContext().getResources().getString(multiSelect.getName()));
            itemView.setOnClickListener(v -> {
                if (callBack != null)
                    callBack.onClick(multiSelect.getAction());
            });
        }
    }

    @FunctionalInterface
    public interface OnMultiSelectItemCallBack {
        void onClick(int action);
    }
}
