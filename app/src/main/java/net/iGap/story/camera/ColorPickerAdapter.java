package net.iGap.story.camera;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.module.AndroidUtils;

import java.util.ArrayList;
import java.util.List;

public class ColorPickerAdapter extends RecyclerView.Adapter<ColorPickerAdapter.ViewHolder> {
    private View view;
    private List<Integer> colorPickerColors;
    private OnColorPickerClickListener onColorPickerClickListener;

    public ColorPickerAdapter(Context context) {
        this.colorPickerColors = getKelly22Colors(context);
    }

    @NonNull
    @Override
    public ColorPickerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = new View(parent.getContext());
        view.setBackgroundColor(Color.WHITE);
        view.setLayoutParams(new ViewGroup.LayoutParams(AndroidUtils.dp(40), AndroidUtils.dp(50)));
        return new ColorPickerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorPickerAdapter.ViewHolder holder, int position) {
        holder.bindView(colorPickerColors.get(position));
    }

    @Override
    public int getItemCount() {
        return colorPickerColors.size();
    }

    public void setOnColorPickerClickListener(OnColorPickerClickListener onColorPickerClickListener) {
        this.onColorPickerClickListener = onColorPickerClickListener;
    }

    private List<Integer> getKelly22Colors(Context context) {
        Resources resources = context.getResources();
        List<Integer> colorList = new ArrayList<>();
        for (int i = 0; i <= 21; i++) {
            int resourceId = resources.getIdentifier("kelly_" + (i + 1), "color", context.getPackageName());
            colorList.add(resources.getColor(resourceId));
        }
        return colorList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View colorPickerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            colorPickerView = itemView;
            itemView.setOnClickListener(v -> {
                if (onColorPickerClickListener != null)
                    onColorPickerClickListener.onColorPickerClickListener(colorPickerColors.get(getAdapterPosition()));
            });
        }

        public void bindView(int color) {
            itemView.setBackgroundColor(color);
        }

    }

    public interface OnColorPickerClickListener {
        void onColorPickerClickListener(int colorCode);
    }
}
