package net.iGap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.model.MultiSelectStruct;

import java.util.ArrayList;
import java.util.List;

public class SelectedItemAdapter extends RecyclerView.Adapter<SelectedItemAdapter.SelectedItemViewHolder> {
    private List<MultiSelectStruct> itemsList = new ArrayList<>();
    private OnMultiSelectItemCallBack callBack;
    private Context context;
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
        context = parent.getContext();
        return new SelectedItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_multi_select, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedItemViewHolder holder, int position) {
        holder.rootView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(LayoutCreator.dp(18),Theme.getColor(Theme.key_light_gray),Theme.getColor(Theme.key_theme_color)));
        holder.bindData(itemsList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class SelectedItemViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTv;
        private View rootView;

        public SelectedItemViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView.getRootView();
            nameTv = itemView.findViewById(R.id.tv_itemMultiSelect);
            nameTv.setTextColor(Theme.getColor(Theme.key_title_text));
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
