package net.iGap.adapter.payment.internetpackage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;

public class AdapterRecentlyPackage extends RecyclerView.Adapter<AdapterRecentlyPackage.RecentlyPackageViewHolder> {

    @NonNull
    @Override
    public RecentlyPackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_internet_package, parent, false);
        return new RecentlyPackageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentlyPackageViewHolder holder, int position) {
        holder.bindProposalPackage();
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public class RecentlyPackageViewHolder extends RecyclerView.ViewHolder {
        private TextView packageSize;
        private TextView packagePrice;

        public RecentlyPackageViewHolder(@NonNull View itemView) {
            super(itemView);
            packageSize = itemView.findViewById(R.id.internet_package);
            packagePrice = itemView.findViewById(R.id.package_price);
        }

        public void bindProposalPackage() {
            packageSize.setText("بسته دو گیگ ماهانه ");
            packagePrice.setText("5000000 ریال  ");

        }

    }

}
