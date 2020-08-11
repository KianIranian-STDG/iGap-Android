package net.iGap.fragments.emoji.remove;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.adapter.items.cells.AddStickerCell;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.helper.HelperCalander;

import java.util.ArrayList;
import java.util.List;

public class RemoveStickerAdapter extends RecyclerView.Adapter<RemoveStickerAdapter.NormalStickerViewHolder> {
    private List<StructIGStickerGroup> stickerGroups = new ArrayList<>();
    private RemoveStickerDialogListener listener;

    void updateAdapter(List<StructIGStickerGroup> data) {
        this.stickerGroups = data;
        notifyDataSetChanged();
    }

    public void setListener(RemoveStickerDialogListener listener) {
        this.listener = listener;
    }

    StructIGStickerGroup getStickerGroup(int pos) {
        return stickerGroups.get(pos);
    }

    @NonNull
    @Override
    public NormalStickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AddStickerCell view = new AddStickerCell(parent.getContext());
        return new NormalStickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NormalStickerViewHolder holder, int position) {
        holder.bindStickers(stickerGroups.get(position));
    }

    @Override
    public int getItemCount() {
        return stickerGroups.size();
    }

    void removeItem(int removedItemPosition) {
        stickerGroups.remove(removedItemPosition);
        notifyItemRemoved(removedItemPosition);
    }

    class NormalStickerViewHolder extends RecyclerView.ViewHolder {
        private AddStickerCell stickerCell;

        NormalStickerViewHolder(@NonNull View itemView) {
            super(itemView);
            stickerCell = (AddStickerCell) itemView;
        }

        private void bindStickers(StructIGStickerGroup stickerGroup) {
            stickerCell.getGroupNameTv().setText(stickerGroup.getName());
            String size = HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(stickerGroup.getStickersSize())) : String.valueOf(stickerGroup.getStickersSize());
            stickerCell.getGroupStickerCountTv().setText(size);

            stickerCell.getButton().setMode(0);
            stickerCell.getButton().changeProgressTo(View.GONE);

            stickerCell.loadAvatar(stickerGroup);

            stickerCell.getButton().setOnClickListener(v -> listener.onRemoveStickerClick(stickerGroup, getAdapterPosition(), visibility -> {
                stickerCell.getButton().changeProgressTo(visibility ? View.VISIBLE : View.GONE);
                if (!visibility)
                    stickerCell.getButton().setMode(0);
            }));

            stickerCell.setOnClickListener(v -> listener.onStickerClick(stickerGroup));

        }
    }

    public interface RemoveStickerDialogListener {
        void onStickerClick(StructIGStickerGroup stickerGroup);

        void onRemoveStickerClick(StructIGStickerGroup stickerGroup, int position, ProgressStatus progressStatus);
    }

    public interface ProgressStatus {
        void setVisibility(boolean visibility);
    }
}
