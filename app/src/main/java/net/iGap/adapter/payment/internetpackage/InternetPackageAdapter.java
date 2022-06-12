package net.iGap.adapter.payment.internetpackage;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.messenger.theme.Theme;
import net.iGap.model.paymentPackage.InternetPackage;

import java.text.DecimalFormat;
import java.util.List;

public class InternetPackageAdapter extends RecyclerView.Adapter<InternetPackageAdapter.ProposalPackageViewHolder> {
    private List<InternetPackage> data;
    private int prevSelectedPosition = -1;
    private int currentlySelectedPosition = -1;
    private OnItemSelectedListener<InternetPackage> selectedListener;

    public void setData(List<InternetPackage> data, int selectedType) {
        prevSelectedPosition = -1;
        currentlySelectedPosition = -1;

        if (selectedType != -1 && data.size() > 0)
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getType() == selectedType) {
                    currentlySelectedPosition = i;
                    break;
                }
            }

        this.data = data;
        notifyDataSetChanged();
    }

    public void setSelectedListener(OnItemSelectedListener<InternetPackage> selectedListener) {
        this.selectedListener = selectedListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public ProposalPackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_internet_package, parent, false);
        return new ProposalPackageViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

    class ProposalPackageViewHolder extends RecyclerView.ViewHolder {
        private InternetPackage internetPackage;
        private ConstraintLayout clParent;
        private TextView packageSize;
        private TextView packagePrice;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        ProposalPackageViewHolder(@NonNull View itemView) {
            super(itemView);
            clParent = itemView.findViewById(R.id.clParent);
            clParent.setBackground(Theme.tintDrawable(itemView.getContext().getDrawable(R.drawable.shape_payment_internet_light),itemView.getContext(),Theme.getColor(Theme.key_popup_background)));
            packageSize = itemView.findViewById(R.id.internet_package);
            packageSize.setTextColor(Theme.getColor(Theme.key_default_text));
            packagePrice = itemView.findViewById(R.id.package_price);
            packagePrice.setTextColor(Theme.getColor(Theme.key_default_text));
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

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        void bindProposalPackage(InternetPackage internetPackage) {
            this.internetPackage = internetPackage;
            packageSize.setText(internetPackage.getDescription());
            DecimalFormat df = new DecimalFormat(",###");
            String price = df.format(internetPackage.getCost());

            packagePrice.setText(String.format("%s %s", price, itemView.getContext().getResources().getString(R.string.rial)));

            if (getAdapterPosition() == currentlySelectedPosition) {
                clParent.setBackground(Theme.tintDrawable(itemView.getContext().getDrawable(R.drawable.shape_payment_internet_selected),itemView.getContext(),Theme.getColor(Theme.key_dark_theme_color)));
            } else {
                clParent.setBackground(Theme.tintDrawable(itemView.getContext().getDrawable(R.drawable.shape_payment_internet_light),itemView.getContext(),Theme.getColor(Theme.key_popup_background)));
            }
        }
    }

    public int getCurrentlySelectedPosition() {
        return currentlySelectedPosition;
    }

    public List<InternetPackage> getData() {
        return data;
    }

    public interface OnItemSelectedListener<T> {
        void onItemSelected(T item);
    }

}
