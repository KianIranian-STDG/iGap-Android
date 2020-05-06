package net.iGap.adapter.payment.internetpackage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;

public class AdapterProposalPackage extends RecyclerView.Adapter<AdapterProposalPackage.ProposalPackageViewHolder> {

    @NonNull
    @Override
    public ProposalPackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_internet_package, parent, false);
        return new ProposalPackageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProposalPackageViewHolder holder, int position) {
        holder.bindProposalPackage();
    }

    @Override
    public int getItemCount() {
        return 8;
    }

    public class ProposalPackageViewHolder extends RecyclerView.ViewHolder {
        private TextView packageSize;
        private TextView packagePrice;

        public ProposalPackageViewHolder(@NonNull View itemView) {
            super(itemView);
            packageSize = itemView.findViewById(R.id.internet_package);
            packagePrice = itemView.findViewById(R.id.package_price);
        }

        public void bindProposalPackage() {
            packageSize.setText("بسته ده گیگ ماهانه ");
            packagePrice.setText("30000000 ریال  ");

        }

    }

}
