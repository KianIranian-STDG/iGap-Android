package net.iGap.mobileBank.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.mobileBank.repository.model.BankHistoryModel;

import java.util.ArrayList;
import java.util.List;

public class MobileBankHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int historyType = 0;
    private static final int loadType = 1;
    private List<BankHistoryModel> mdata;
    private OnItemClickListener clickListener;

    public MobileBankHistoryAdapter(List<BankHistoryModel> dates, OnItemClickListener clickListener) {
        this.mdata = dates;
//        createFakeData();
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case historyType:
                View singleVH = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mobile_bank_history_item, viewGroup, false);
                viewHolder = new ViewHolder(singleVH);
                break;
            case loadType:
                View loadVH = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_load_item, viewGroup, false);
                viewHolder = new LoadViewHolder(loadVH);
                break;
            default:
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = viewHolder.getItemViewType();
        switch (viewType) {
            case historyType:
                ((ViewHolder) viewHolder).initView(position);
                break;
            case loadType:
                ((LoadViewHolder) viewHolder).initVH();
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mdata == null)
            return 0;
        return mdata.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mdata.get(position) == null)
            return loadType;
        return historyType;
    }

    public void addItems(List<BankHistoryModel> postItems) {
        this.mdata.addAll(postItems);
        notifyDataSetChanged();
    }

    public void addLoading() {
        mdata.add(null);
        notifyItemInserted(mdata.size() - 1);
    }

    public void removeLoading() {
        if (mdata.size() == 0)
            return;

        int position = mdata.size() - 1;
        BankHistoryModel item = mdata.get(position);
        if (item == null) {
            mdata.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        mdata = null;
        mdata = new ArrayList<>();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title, value, date;
        private CardView container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            value = itemView.findViewById(R.id.amount);
            date = itemView.findViewById(R.id.time);
            container = itemView.findViewById(R.id.container);

        }

        void initView(int position) {

            title.setText(mdata.get(position).getTitle());
            value.setText(mdata.get(position).getAmount());
            date.setText(mdata.get(position).getDate());
            container.setOnClickListener(v -> clickListener.onClick(position));

        }
    }

    public class LoadViewHolder extends RecyclerView.ViewHolder {

        LoadViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void initVH() {

        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

}
