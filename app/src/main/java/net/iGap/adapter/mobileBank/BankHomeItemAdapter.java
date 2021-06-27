package net.iGap.adapter.mobileBank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.model.mobileBank.MobileBankHomeItemsModel;

import java.util.ArrayList;
import java.util.List;

public class BankHomeItemAdapter extends RecyclerView.Adapter<BankHomeItemAdapter.ViewHolderItems> {

    private MobileBankHomeItemsListener listener;
    private List<MobileBankHomeItemsModel> items = new ArrayList<>();

    public void setListener(MobileBankHomeItemsListener listener) {
        this.listener = listener;
    }

    public void setItems(List<MobileBankHomeItemsModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setItem(MobileBankHomeItemsModel item, int position) {
        this.items.set(position, item);
        notifyItemChanged(position);
    }

    @NonNull
    @Override
    public ViewHolderItems onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderItems(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_mobile_bank_home_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderItems holder, int position) {
        if (position == 2) {
            holder.frameLayout.setVisibility(View.VISIBLE);
        }

        holder.root.setOnClickListener(v -> listener.onItemClicked(holder.getAdapterPosition(), items.get(holder.getAdapterPosition()).getTitle()));
        holder.tvTitle.setText(items.get(position).getTitle());
        holder.ivIcon.setImageResource(items.get(position).getIcon());
        holder.progressBar.setVisibility(items.get(position).getProgressVisibility());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolderItems extends RecyclerView.ViewHolder {

        private TextView tvTitle;
        private ImageView ivIcon;
        private View root;
        private FrameLayout frameLayout;
        private ProgressBar progressBar;

        ViewHolderItems(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            root = itemView.findViewById(R.id.root);
            frameLayout = itemView.findViewById(R.id.frame_progress);
            progressBar = itemView.findViewById(R.id.progress_notification);
            progressBar.getIndeterminateDrawable().setColorFilter(0XFFB6774E, android.graphics.PorterDuff.Mode.MULTIPLY);
        }
    }

    public interface MobileBankHomeItemsListener {
        void onItemClicked(int position, int title);
    }

}
