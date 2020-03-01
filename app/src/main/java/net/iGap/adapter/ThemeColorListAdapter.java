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
import net.iGap.module.Theme;
import net.iGap.model.ThemeModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ThemeColorListAdapter extends RecyclerView.Adapter<ThemeColorListAdapter.ViewHolder> {

    private List<ThemeModel> items;
    private int selectedThemePosition = -1;
    private onThemeItemClickedListener callback;

    public ThemeColorListAdapter(onThemeItemClickedListener callback) {
        items = new ArrayList<>();
        this.callback = callback;
    }

    public void setData(List<ThemeModel> newItem) {
        items.addAll(newItem);
        notifyDataSetChanged();
    }

    public void setSelectedTheme(int selectedTheme) {
        selectedThemePosition = selectedTheme;
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

        setChatReceivedChatBubble(holder.themeColor, new Theme().getColor(items.get(position).getThemeId()));
        holder.themeName.setText(items.get(position).getThemeNameRes());
        holder.itemView.setOnClickListener(v -> {
            if (selectedThemePosition != holder.getAdapterPosition()) {
                callback.onItemClicked(selectedThemePosition, holder.getAdapterPosition());
            }
        });
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

    private Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }

    private void setChatReceivedChatBubble(@NotNull View view, int color) {
        view.setBackground(tintDrawable(view.getBackground(), ColorStateList.valueOf(ContextCompat.getColor(view.getContext(), color))));
    }

    public interface onThemeItemClickedListener {
        void onItemClicked(int oldThemePosition, int newThemePosition);
    }
}
