package net.iGap.adapter.electricity_bill;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.messenger.theme.Theme;
import net.iGap.model.electricity_bill.ElectricityBranchData;

import java.util.ArrayList;
import java.util.List;

public class ElectricityBillSearchListAdapter extends RecyclerView.Adapter<ElectricityBillSearchListAdapter.ViewHolder> {

    private List<ElectricityBranchData> mData;
    private Context context;
    private OnItemClickListener clickListener;

    public ElectricityBillSearchListAdapter(Context context, List<ElectricityBranchData> data, OnItemClickListener clickListener) {
        this.mData = new ArrayList<>(data);
        this.context = context;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_elec_search_list_cell, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.initView(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView billID, customerName, customerAddress,billIDTitle,billCustomerNameTitle,billCustomerAddressTitle;
        private CardView container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            customerName = itemView.findViewById(R.id.billCustomerName);
            billCustomerNameTitle = itemView.findViewById(R.id.billCustomerNameTitle);
            billCustomerNameTitle.setTextColor(Theme.getColor(Theme.key_title_text));
            billIDTitle = itemView.findViewById(R.id.billIDTitle);
            billIDTitle.setTextColor(Theme.getColor(Theme.key_title_text));
            billID = itemView.findViewById(R.id.billID);
            billID.setTextColor(Theme.getColor(Theme.key_title_text));
            customerAddress = itemView.findViewById(R.id.billCustomerAddress);
            billCustomerAddressTitle = itemView.findViewById(R.id.billCustomerAddressTitle);
            billCustomerAddressTitle.setTextColor(Theme.getColor(Theme.key_title_text));
            container = itemView.findViewById(R.id.cardHolder);
            container.setCardBackgroundColor(Theme.getColor(Theme.key_window_background));

        }

        void initView(int position) {

            if (HelperCalander.isPersianUnicode) {
                billID.setText(HelperCalander.convertToUnicodeFarsiNumber(mData.get(position).getBillID()));
            }
            else
                billID.setText(mData.get(position).getBillID());
            customerName.setText((mData.get(position).getCustomerName()==null? "":mData.get(position).getCustomerName())
                    + " " + (mData.get(position).getCustomerFamily()==null? "" : mData.get(position).getCustomerFamily()));
            customerAddress.setText(mData.get(position).getServiceAddress());
            container.setOnClickListener(v -> clickListener.onClick(position));

        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

}
