package net.iGap.module.dialog;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.CustomListItemBottomSheetBinding;
import net.iGap.messenger.theme.Theme;

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
        binding.items.setBackgroundColor(Theme.getColor(Theme.key_popup_background));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setTitle(items.get(position));
        holder.binding.setIcon(findRelevantIcon(position));
        holder.binding.setPosition(position);
        holder.binding.executePendingBindings();
        holder.binding.itemTitle.setSelected(true/*position < range*/);
        holder.binding.itemTitle.setTextColor(Theme.getColor(Theme.key_icon));
        holder.binding.itemIcon.setTextColor(Theme.getColor(Theme.key_icon));
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

    public String findRelevantIcon(int position) {
        String item = items.get(position);
        if (item.equals(G.context.getResources().getString(R.string.replay_item_dialog))) {
            return G.context.getResources().getString(R.string.icon_reply);
        } else if (item.equals(G.context.getResources().getString(R.string.share_item_dialog)) ||
                item.equals(G.context.getResources().getString(R.string.share_link_item_dialog)) ||
                item.equals(G.context.getResources().getString(R.string.share_file_link)) ||
                item.equals(G.context.getResources().getString(R.string.share_image)) ||
                item.equals(G.context.getResources().getString(R.string.share_video_file))) {
            return G.context.getResources().getString(R.string.icon_share);
        } else if (item.equals(G.context.getResources().getString(R.string.forward_item_dialog))) {
            return G.context.getResources().getString(R.string.icon_forward);
        } else if (item.equals(G.context.getResources().getString(R.string.delete_item_dialog))) {
            return G.context.getResources().getString(R.string.icon_Delete);
        } else if (item.equals(G.context.getResources().getString(R.string.delete_from_storage))) {
            return G.context.getResources().getString(R.string.icon_clear_history);
        } else if (item.equals(G.context.getResources().getString(R.string.save_to_gallery))) {
            return G.context.getResources().getString(R.string.icon_gallery);
        } else if (item.equals(G.context.getResources().getString(R.string.save_to_Music))) {
            return G.context.getResources().getString(R.string.icon_music_file);
        } else if (item.equals(G.context.getResources().getString(R.string.saveToDownload_item_dialog))) {
            return G.context.getResources().getString(R.string.icon_download);
        } else if (item.equals(G.context.getResources().getString(R.string.copy_item_dialog))) {
            return G.context.getResources().getString(R.string.icon_copy);
        } else if (item.equals(G.context.getResources().getString(R.string.edit_item_dialog))) {
            return G.context.getResources().getString(R.string.icon_edit);
        } else if (item.equals(G.context.getResources().getString(R.string.PIN))) {
            return G.context.getResources().getString(R.string.icon_pin_to_top);
        } else if (item.equals(G.context.getResources().getString(R.string.report))) {
            return G.context.getResources().getString(R.string.icon_error);
        } else if (item.equals(G.context.getResources().getString(R.string.convert_text_to_voice))) {
            return G.context.getResources().getString(R.string.icon_play);
        } else {
            return "";
        }
    }
}