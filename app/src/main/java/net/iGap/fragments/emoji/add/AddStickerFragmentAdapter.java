package net.iGap.fragments.emoji.add;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.adapter.items.cells.AddStickerCell;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.helper.HelperCalander;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AddStickerFragmentAdapter extends RecyclerView.Adapter<AddStickerFragmentAdapter.ViewHolder> {
    private List<StructIGStickerGroup> stickerGroups = new ArrayList<>();
    private AddStickerAdapterListener listener;

    public void setListener(AddStickerAdapterListener listener) {
        this.listener = listener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AddStickerCell view = new AddStickerCell(parent.getContext());
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindView(stickerGroups.get(position));
    }

    @Override
    public int getItemCount() {
        return stickerGroups.size();
    }

    void addData(List<StructIGStickerGroup> data) {
        this.stickerGroups.addAll(data);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AddStickerCell stickerCell;

        ViewHolder(View itemView) {
            super(itemView);
            stickerCell = (AddStickerCell) itemView;
        }

        private void bindView(StructIGStickerGroup sticker) {
            stickerCell.setStickerGroupId(sticker.getGroupId());
            stickerCell.getButton().setMode(sticker.isInUserList() ? 0 : 1);
            stickerCell.getGroupNameTv().setText(sticker.getName());

            String size = HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(sticker.getStickers().size())) : String.valueOf(sticker.getStickers().size());
            stickerCell.getGroupStickerCountTv().setText(size);

            stickerCell.setOnClickListener(v -> listener.onCellClick(sticker));

            stickerCell.getButton().setOnClickListener(v -> {
                stickerCell.getButton().changeProgressTo(View.VISIBLE);
                listener.onButtonClick(sticker, isFavorite -> {
                    stickerCell.getButton().changeProgressTo(View.GONE);
                    stickerCell.getButton().setMode(isFavorite ? 0 : 1);
                });
            });

            stickerCell.loadAvatar(sticker);

            listener.onButtonStatusChange(isFavorite -> stickerCell.getButton().setMode(isFavorite ? 0 : 1));

        }
    }

    public interface AddStickerAdapterListener {
        void onButtonClick(StructIGStickerGroup stickerGroup, ProgressStatus progressStatus);

        void onCellClick(StructIGStickerGroup stickerGroup);

        void onButtonStatusChange(ButtonsStatus buttonsStatus);
    }

    public interface ProgressStatus {
        void setVisibility(boolean isFavorite);
    }

    public interface ButtonsStatus {
        void changed(boolean isFavorite);
    }
}
