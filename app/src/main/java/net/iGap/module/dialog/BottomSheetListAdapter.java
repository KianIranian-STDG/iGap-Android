package net.iGap.module.dialog;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.databinding.CustomListItemBottomSheetBinding;

import java.util.List;

public class BottomSheetListAdapter extends RecyclerView.Adapter<BottomSheetListAdapter.ViewHolder> {

    private List<String> items;
    private int range;

    @Nullable
    private final BottomSheetItemClickCallback itemClickCallback;

    public BottomSheetListAdapter(List<String> items, int range, @Nullable BottomSheetItemClickCallback itemClickCallback) {
        this.items = items;
        this.range = range;
        this.itemClickCallback = itemClickCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CustomListItemBottomSheetBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.custom_list_item_bottom_sheet, parent, false);
        binding.setCallback(itemClickCallback);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setTitle(items.get(position));
        holder.binding.setPosition(position);
        holder.binding.executePendingBindings();
        holder.binding.itemTitle.setSelected(true/*position < range*/);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final CustomListItemBottomSheetBinding binding;

        ViewHolder(@NonNull CustomListItemBottomSheetBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}