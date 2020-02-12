package net.iGap.adapter.mobileBank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.module.Theme;
import net.iGap.helper.HelperCalander;
import net.iGap.model.mobileBank.BankDateModel;

import java.util.List;

public class MobileBankDateAdapter extends RecyclerView.Adapter<MobileBankDateAdapter.ViewHolder> {

    private List<BankDateModel> mdata;
    private OnItemClickListener clickListener;
    private Context context;
    private int selectedPosition = -1;

    public MobileBankDateAdapter(List<BankDateModel> dates, OnItemClickListener clickListener) {
        this.mdata = dates;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mobile_bank_date_item, viewGroup, false);
        context = viewGroup.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.initView(position);
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView year, month;
        private ConstraintLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            year = itemView.findViewById(R.id.year);
            month = itemView.findViewById(R.id.month);
            container = itemView.findViewById(R.id.container);

        }

        void initView(int position) {

            year.setText(CompatibleUnicode(mdata.get(position).getYear().replace("13", "")));
            month.setText(mdata.get(position).getMonthName());
            container.setOnClickListener(v -> {
                if (!mdata.get(position).isActive())
                    return;
                mdata.get(selectedPosition).setSelected(false);
                notifyItemChanged(selectedPosition);
                mdata.get(position).setSelected(true);
                notifyItemChanged(position);
                clickListener.onClick(position);
            });
            if (mdata.get(position).isSelected()) {
                selectedPosition = position;
                container.setBackgroundResource(R.drawable.bank_date_selected);
                year.setTextColor(context.getResources().getColor(R.color.white));
                month.setTextColor(context.getResources().getColor(R.color.white));
            } else {
                container.setBackgroundResource(0);
                year.setTextColor(new Theme().getTitleTextColor(context));
                month.setTextColor(new Theme().getTitleTextColor(context));
            }
            if (mdata.get(position).isActive()) {

            } else {

            }
        }

        private String CompatibleUnicode(String entry) {
            return HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(entry)) : entry;
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

}
