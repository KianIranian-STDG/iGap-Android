package net.iGap.adapter.payment.internetpackage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.model.paymentPackage.InternetPackage;
import net.iGap.module.Theme;
import net.iGap.observers.interfaces.OnItemSelectedListener;

import java.util.List;

public class AdapterInternetPackage extends RecyclerView.Adapter<AdapterInternetPackage.ProposalPackageViewHolder> {
    private List<InternetPackage> data;
    private int prevSelectedPosition = -1;
    private int currentlySelectedPosition = -1;
    private OnItemSelectedListener<InternetPackage> selectedListener;

    public void setData(List<InternetPackage> data) {
        prevSelectedPosition = -1;
        currentlySelectedPosition = -1;
        this.data = data;
        notifyDataSetChanged();
    }

    public void setSelectedListener(OnItemSelectedListener<InternetPackage> selectedListener) {
        this.selectedListener = selectedListener;
    }

    @NonNull
    @Override
    public ProposalPackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_internet_package, parent, false);
        return new ProposalPackageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProposalPackageViewHolder holder, int position) {
        if (data != null)
            holder.bindProposalPackage(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public void unSelectCurrent() {
        if (currentlySelectedPosition < 0 && prevSelectedPosition < 0)
            return;

        int current = currentlySelectedPosition;
        int prev = prevSelectedPosition;
        prevSelectedPosition = -1;
        currentlySelectedPosition = -1;

        notifyItemChanged(current);
        notifyItemChanged(prev);
    }

    public class ProposalPackageViewHolder extends RecyclerView.ViewHolder {
        private InternetPackage internetPackage;
        private ConstraintLayout clParent;
        private TextView packageSize;
        private TextView packagePrice;

        public ProposalPackageViewHolder(@NonNull View itemView) {
            super(itemView);
            clParent = itemView.findViewById(R.id.clParent);
            packageSize = itemView.findViewById(R.id.internet_package);
            packagePrice = itemView.findViewById(R.id.package_price);

            clParent.setOnClickListener(v -> {
                if (selectedListener != null) {
                    selectedListener.onItemSelected(internetPackage);
                    prevSelectedPosition = currentlySelectedPosition;
                    currentlySelectedPosition = getAdapterPosition();
                    notifyItemChanged(prevSelectedPosition);
                    notifyItemChanged(currentlySelectedPosition);
                }
            });
        }

        public void bindProposalPackage(InternetPackage internetPackage) {
            this.internetPackage = internetPackage;
            packageSize.setText(internetPackage.getDescription());
            packagePrice.setText(String.format("%d %s", internetPackage.getCost(), itemView.getContext().getResources().getString(R.string.rial)));

            if (getAdapterPosition() == currentlySelectedPosition) {
                clParent.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.shape_payment_internet_selected));
            } else {
                if (G.themeColor == Theme.DARK)
                    clParent.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.shape_payment_internet_dark));
                else
                    clParent.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.shape_payment_internet_light));
            }
        }
    }
}
