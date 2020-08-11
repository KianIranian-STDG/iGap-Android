package net.iGap.adapter.items.poll;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.data.BarEntry;

import net.iGap.R;
import net.iGap.adapter.items.poll.holder.BaseViewHolder;
import net.iGap.adapter.items.poll.holder.Type1ViewHolder;
import net.iGap.adapter.items.poll.holder.Type2ViewHolder;
import net.iGap.adapter.items.poll.holder.Type3ViewHolder;
import net.iGap.adapter.items.poll.holder.Type4ViewHolder;
import net.iGap.adapter.items.poll.holder.Type5ViewHolder;
import net.iGap.adapter.items.poll.holder.Type6ViewHolder;
import net.iGap.adapter.items.poll.holder.Type7ViewHolder;
import net.iGap.adapter.items.poll.holder.TypeChartViewHolder;
import net.iGap.adapter.items.poll.holder.TypeUnknownViewHolder;

import java.util.ArrayList;
import java.util.List;

public class PollAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<PollItem> pollList;
    private String[] labels;
    private ArrayList<BarEntry> barEntries;


    public PollAdapter(Context context, List<PollItem> pollList) {
        this.context = context;
        this.pollList = pollList;
        this.labels = null;
        this.barEntries = null;
    }

    public void setPollList(List<PollItem> pollList) {
        this.pollList = pollList;
        this.labels = null;
        this.barEntries = null;
    }

    public void notifyChangeData() {
        this.notifyDataSetChanged();
    }


    public List<PollItem> getData() {
        return this.pollList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        switch (i) {
            case 1:
                return new Type1ViewHolder(this, layoutInflater.inflate(R.layout.item_poll_1, viewGroup, false));
            case 2:
                return new Type2ViewHolder(this, layoutInflater.inflate(R.layout.item_poll_2, viewGroup, false));
            case 3:
                return new Type3ViewHolder(this, layoutInflater.inflate(R.layout.item_poll_3, viewGroup, false));
            case 4:
                return new Type4ViewHolder(this, layoutInflater.inflate(R.layout.item_poll_4, viewGroup, false));
            case 5:
                return new Type5ViewHolder(this, layoutInflater.inflate(R.layout.item_poll_5, viewGroup, false));
            case 6:
                return new Type6ViewHolder(this, layoutInflater.inflate(R.layout.item_poll_6, viewGroup, false));
            case 7:
                return new Type7ViewHolder(this, layoutInflater.inflate(R.layout.item_poll_7, viewGroup, false));
        }
        return new TypeUnknownViewHolder(this, layoutInflater.inflate(R.layout.item_discovery_unknown, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (pollList.size() == i) {
            ((TypeChartViewHolder) viewHolder).bindView(labels, barEntries);
        } else {
            ConstraintSet set = new ConstraintSet();
            set.clone((ConstraintLayout) viewHolder.itemView);
            set.setDimensionRatio(R.id.type1_card0, pollList.get(i).scale);
            set.applyTo((ConstraintLayout) viewHolder.itemView);
            ((BaseViewHolder) viewHolder).bindView(pollList.get(i));
        }
    }

    @Override
    public int getItemCount() {
        if (labels == null) {
            return pollList.size();
        }
        return pollList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return pollList.get(position).model + 1;
    }
}