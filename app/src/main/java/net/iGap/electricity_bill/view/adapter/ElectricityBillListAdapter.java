package net.iGap.electricity_bill.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.electricity_bill.repository.model.Bill;
import net.iGap.electricity_bill.repository.model.BillData;
import net.iGap.electricity_bill.repository.model.BranchDebit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElectricityBillListAdapter extends RecyclerView.Adapter<ElectricityBillListAdapter.ViewHolder> {

    private Map<BillData.BillDataModel, BranchDebit> mdata;
    private List<BillData.BillDataModel> bill;
    private List<BranchDebit> debits;
    private Context context;
    private OnItemClickListener clickListener;

    public ElectricityBillListAdapter(Context context, Map<BillData.BillDataModel, BranchDebit> data, OnItemClickListener clickListener) {
        this.mdata = new HashMap<>();
        this.mdata.putAll(data);
        this.bill = new ArrayList<>(mdata.keySet());
        this.debits = new ArrayList<>(mdata.values());
        this.context = context;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_elec_bill_list_cell, viewGroup, false);
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

        private TextView title, billID, billPayID, billPrice, billTime;
        private ProgressBar progressPID, progressP, progressT;
        private Button pay, showDetail;
        private TextView delete, edit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.billTitle);
            billID = itemView.findViewById(R.id.billID);
            billPayID = itemView.findViewById(R.id.billPayID);
            billPrice = itemView.findViewById(R.id.billPrice);
            billTime = itemView.findViewById(R.id.billTime);
            pay = itemView.findViewById(R.id.Pay);
            showDetail = itemView.findViewById(R.id.detail);
            progressPID = itemView.findViewById(R.id.ProgressVPay);
            progressP = itemView.findViewById(R.id.ProgressVPrice);
            progressT = itemView.findViewById(R.id.ProgressVTime);
            delete = itemView.findViewById(R.id.billDelete);
            edit = itemView.findViewById(R.id.billEdit);

        }

        void initView(int position) {
            if (!mdata.get(bill.get(position)).isLoading()) {
                progressPID.setVisibility(View.GONE);
                progressP.setVisibility(View.GONE);
                progressT.setVisibility(View.GONE);

                title.setText(bill.get(position).getBillTitle());
                billID.setText(bill.get(position).getBillID());

                billPayID.setText(mdata.get(bill.get(position)).getPaymentID());
                billPrice.setText(mdata.get(bill.get(position)).getTotalBillDebt());
                billTime.setText(mdata.get(bill.get(position)).getPaymentDeadLineDate());
            }

            pay.setOnClickListener(v -> {
                if (!mdata.get(bill.get(position)).isLoading()) {
                    clickListener.onClick(position, OnItemClickListener.Actoin.PAY);
                }
                else
                    Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
            });

            showDetail.setOnClickListener(v -> clickListener.onClick(position, OnItemClickListener.Actoin.SHOW_DETAIL));

            delete.setOnClickListener(v -> clickListener.onClick(position, OnItemClickListener.Actoin.DELETE));

            edit.setOnClickListener(v -> clickListener.onClick(position, OnItemClickListener.Actoin.EDIT));
        }
    }

    public interface OnItemClickListener {
        enum Actoin {DELETE, EDIT, SHOW_DETAIL, PAY}
        void onClick(int position, Actoin btnAction);
    }

}
