package net.iGap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.model.paymentPackage.Operator;
import net.iGap.module.Theme;
import net.iGap.module.imageLoaderService.ImageLoadingServiceInjector;

import java.util.List;

public class OperatorRecyclerAdapter extends RecyclerView.Adapter<OperatorRecyclerAdapter.ViewHolder> {
    private final List<Operator> operators;
    private final Context context;
    private final SelectedRadioButton selectedRadioButton;
    private AppCompatRadioButton lastCheckedRB = null;
    private String tagKey = "";

    public OperatorRecyclerAdapter(Context context, List<Operator> operators, SelectedRadioButton selectedRadioButton) {
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
        holder.radioButton.setTag(operator.getKey());

        if (tagKey.equals(holder.radioButton.getTag().toString())) {
            if (lastCheckedRB != null) {
                lastCheckedRB.setChecked(false);
            }
            holder.radioButton.setChecked(true);
            lastCheckedRB = holder.radioButton;
        }

        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastCheckedRB != null) {
                    lastCheckedRB.setChecked(false);
                }
                lastCheckedRB = (AppCompatRadioButton) view;
                selectedRadioButton.onSelectedRadioButton(lastCheckedRB.getTag().toString());
            }
        });
        ImageLoadingServiceInjector.inject().loadImage(holder.imageView, operator.getLogo(), 0);
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
        switch (G.themeColor) {
            case Theme.DARK:
                view.setBackground(context.getResources().getDrawable(R.drawable.selector_topup_operator_dark));
                break;
            case Theme.AMBER:
                view.setBackground(context.getResources().getDrawable(R.drawable.selector_topup_operator_amber));
                break;
            case Theme.GREEN:
                view.setBackground(context.getResources().getDrawable(R.drawable.selector_topup_operator_green));
                break;
            case Theme.BLUE:
                view.setBackground(context.getResources().getDrawable(R.drawable.selector_topup_operator_blue));
                break;
            case Theme.PURPLE:
                view.setBackground(context.getResources().getDrawable(R.drawable.selector_topup_operator_purple));
                break;
            case Theme.PINK:
                view.setBackground(context.getResources().getDrawable(R.drawable.selector_topup_operator_pink));
                break;
            case Theme.RED:
                view.setBackground(context.getResources().getDrawable(R.drawable.selector_topup_operator_red));
                break;
            case Theme.ORANGE:
                view.setBackground(context.getResources().getDrawable(R.drawable.selector_topup_operator_orange));
                break;
            case Theme.GREY:
                view.setBackground(context.getResources().getDrawable(R.drawable.selector_topup_operator_dark_gray));
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

        public ViewHolder(View view) {
            super(view);
            root = (ViewGroup) view;
            radioButton = view.findViewById(R.id.radioButton);
            imageView = view.findViewById(R.id.imageView);
        }
    }
}