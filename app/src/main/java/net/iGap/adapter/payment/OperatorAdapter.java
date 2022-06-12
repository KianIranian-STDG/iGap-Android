package net.iGap.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.messenger.theme.Theme;
import net.iGap.model.paymentPackage.Operator;
import net.iGap.module.imageLoaderService.ImageLoadingServiceInjector;

import java.util.List;

public class OperatorAdapter extends RecyclerView.Adapter<OperatorAdapter.ViewHolder> {
    private final List<Operator> operators;
    private final Context context;
    private final SelectedRadioButton selectedRadioButton;
    private AppCompatRadioButton lastCheckedRB = null;
    private String tagKey;

    public OperatorAdapter(Context context, List<Operator> operators, SelectedRadioButton selectedRadioButton) {
        this.context = context;
        this.operators = operators;
        this.selectedRadioButton = selectedRadioButton;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_operator, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Operator operator = operators.get(position);
        setViewBackground(holder.root);
        AppCompatRadioButton radioButton = holder.radioButton;
        radioButton.setTag(operator.getKey());
        if (tagKey != null) {
            if (tagKey.equals(radioButton.getTag().toString())) {
                if (lastCheckedRB != null) {
                    lastCheckedRB.setChecked(false);
                }
                radioButton.setChecked(true);
                lastCheckedRB = radioButton;
            }
        } else {
            radioButton.setChecked(false);
            lastCheckedRB = radioButton;
        }

        radioButton.setOnClickListener(view -> {
            if (lastCheckedRB != null) {
                lastCheckedRB.setChecked(false);
            }
            lastCheckedRB = (AppCompatRadioButton) view;
            selectedRadioButton.onSelectedRadioButton(lastCheckedRB.getTag().toString());
        });
        ImageLoadingServiceInjector.inject().loadImage(holder.imageView, operator.getLogo(), 0);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastCheckedRB != null) {
                    lastCheckedRB.setChecked(false);
                }
                lastCheckedRB = radioButton;
                lastCheckedRB.setChecked(true);
                selectedRadioButton.onSelectedRadioButton(lastCheckedRB.getTag().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return operators.size();
    }

    public void setCheckedRadioButton(String operatorName) {
        this.tagKey = operatorName;
        notifyDataSetChanged();
    }

    public void setViewBackground(View view) {
        switch (Theme.getCurrentTheme().getName()) {
            case Theme.DAY_GREEN_THEME:
                view.setBackground(context.getResources().getDrawable(R.drawable.selector_topup_operator_green));
                break;
            case Theme.DARK_GREEN_THEME:
            case Theme.NIGHT_GREEN_THEME:
                view.setBackground(context.getResources().getDrawable(R.drawable.selector_topup_operator_dark));
                break;
            case Theme.DAY_YELLOW_THEME:
            case Theme.DARK_YELLOW_THEME:
            case Theme.NIGHT_YELLOW_THEME:
                view.setBackground(context.getResources().getDrawable(R.drawable.selector_topup_operator_amber));
                break;
            case Theme.DAY_BLUE_THEME:
            case Theme.DARK_BLUE_THEME:
            case Theme.NIGHT_BLUE_THEME:
                view.setBackground(context.getResources().getDrawable(R.drawable.selector_topup_operator_blue));
                break;
            case Theme.DAY_CYAN_THEME:
            case Theme.DARK_CYAN_THEME:
            case Theme.NIGHT_CYAN_THEME:
                view.setBackground(context.getResources().getDrawable(R.drawable.selector_topup_operator_purple));
                break;
            case Theme.DAY_PINK_THEME:
            case Theme.DARK_PINK_THEME:
            case Theme.NIGHT_PINK_THEME:
                view.setBackground(context.getResources().getDrawable(R.drawable.selector_topup_operator_pink));
                break;
            case Theme.DAY_RED_THEME:
            case Theme.DARK_RED_THEME:
            case Theme.NIGHT_RED_THEME:
                view.setBackground(context.getResources().getDrawable(R.drawable.selector_topup_operator_red));
                break;
            case Theme.DAY_ORANGE_THEME:
            case Theme.DARK_ORANGE_THEME:
            case Theme.NIGHT_ORANGE_THEME:
                view.setBackground(context.getResources().getDrawable(R.drawable.selector_topup_operator_orange));
                break;
        }
    }

    public interface SelectedRadioButton {
        void onSelectedRadioButton(String operatorName);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewGroup root;
        AppCompatRadioButton radioButton;
        AppCompatImageView imageView;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public ViewHolder(View view) {
            super(view);
            root = (ViewGroup) view;
            radioButton = view.findViewById(R.id.radioButton);
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{new int[]{-android.R.attr.state_enabled},new int[]{android.R.attr.state_enabled}},
                    new int[]{Theme.getColor(Theme.key_icon),Theme.getColor(Theme.key_theme_color)}
            );
            radioButton.setButtonTintList(colorStateList);
            radioButton.invalidate();
            imageView = view.findViewById(R.id.imageView);
        }
    }
}