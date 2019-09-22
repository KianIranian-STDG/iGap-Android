package net.iGap.adapter;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.Theme;
import net.iGap.dialog.BottomSheetItemClickCallback;
import net.iGap.model.ThemeModel;

import java.util.ArrayList;
import java.util.List;

public class ThemeColorListAdapter extends RecyclerView.Adapter<ThemeColorListAdapter.ViewHolder> {

    private List<ThemeModel> items;
    private int selectedThemePosition = -1;
    private BottomSheetItemClickCallback callback;

    public ThemeColorListAdapter(BottomSheetItemClickCallback callback) {
        items = new ArrayList<>();
        this.callback = callback;
    }

    public void setData(List<ThemeModel> newItem, int selectedPosition) {
        items.addAll(newItem);
        selectedThemePosition = selectedPosition;
        notifyDataSetChanged();
    }

    public void setSelectedTheme(int selectedTheme) {
        int tmp = selectedThemePosition;
        selectedThemePosition = selectedTheme;
        notifyItemChanged(tmp);
        notifyItemChanged(selectedThemePosition);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custome_row_theme_color, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (selectedThemePosition != -1) {
            holder.checkTheme.setText(selectedThemePosition == position ? R.string.check_icon : R.string.empty_error_message);
        } else {
            holder.checkTheme.setText(R.string.empty_error_message);
        }

        setChatReceivedChatBubble(holder.themeColor, getColor(items.get(position).getThemeId()));
        holder.themeName.setText(items.get(position).getThemeNameRes());
        holder.itemView.setOnClickListener(v -> callback.onClick(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView themeName;
        private AppCompatTextView checkTheme;
        private View themeColor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            themeColor = itemView.findViewById(R.id.themeColor);
            themeName = itemView.findViewById(R.id.themeName);
            checkTheme = itemView.findViewById(R.id.themeCheck);
        }
    }

    private int getColor(int themeId) {
        switch (themeId) {
            case Theme.AMBER:
                return R.color.amber;
            case Theme.BLUE:
                return R.color.blue;
            case Theme.BLUE_GREY:
                return R.color.blueGray;
            case Theme.BROWN:
                return R.color.brown;
            case Theme.CYAN:
                return R.color.cyan;
            case Theme.DEEP_ORANGE:
                return R.color.deepOrange;
            default:
                return R.color.green;

        }
    }

    public Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }

    public void setChatReceivedChatBubble(View view, int color) {
        view.setBackground(tintDrawable(view.getBackground(), ColorStateList.valueOf(ContextCompat.getColor(view.getContext(), color))));
    }
}
