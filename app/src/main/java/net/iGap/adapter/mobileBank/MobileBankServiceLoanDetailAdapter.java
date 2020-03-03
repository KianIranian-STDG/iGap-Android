package net.iGap.adapter.mobileBank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import net.iGap.R;
import net.iGap.helper.HelperMobileBank;
import net.iGap.model.mobileBank.BankServiceLoanDetailModel;
import net.iGap.module.mobileBank.JalaliCalendar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MobileBankServiceLoanDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int loanType = 0;
    private static final int loadType = 1;
    private List<BankServiceLoanDetailModel.LoanItem> mdata;
    private OnItemClickListener clickListener;
    private Context context;

    public MobileBankServiceLoanDetailAdapter(List<BankServiceLoanDetailModel.LoanItem> dates, OnItemClickListener clickListener) {
        this.mdata = dates;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        context = viewGroup.getContext();
        switch (viewType) {
            case loanType:
                View singleVH = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mobile_bank_loan_detail_cell, viewGroup, false);
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
            case loanType:
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
        return loanType;
    }

    public void addItems(List<BankServiceLoanDetailModel.LoanItem> postItems) {
        this.mdata.addAll(postItems);
        notifyDataSetChanged();
    }

    public void removeAll() {
        this.mdata.clear();
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
        BankServiceLoanDetailModel.LoanItem item = mdata.get(position);
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

        private TextView status, payAmount, unpaidAmount, penaltyAmount, paymentDate;
        private MaterialButton btnPay ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            status = itemView.findViewById(R.id.loanPayState);
            payAmount = itemView.findViewById(R.id.payAmount);
            unpaidAmount = itemView.findViewById(R.id.unpaidAmount);
            penaltyAmount = itemView.findViewById(R.id.penaltyAmount);
            paymentDate = itemView.findViewById(R.id.paymentDate);
            btnPay = itemView.findViewById(R.id.btnPay);

        }

        void initView(int position) {
            //btnPay.setVisibility(View.GONE);
            String statusTemp = "";
            switch (mdata.get(position).getPayStatus()) {
                case "PAID":
                    statusTemp = context.getResources().getString(R.string.paid);
                    break;
                case "NOT_PAID_BEFORE_MATURITY":
                case "NOT_PAID_AFTER_MATURITY":
                    btnPay.setVisibility(View.VISIBLE);
                    statusTemp = context.getResources().getString(R.string.unpaid);
                    break;
                case "UNKNOWN":
                    statusTemp = context.getResources().getString(R.string.unknown);
                    break;
                default:
                    break;
            }
            status.setText(context.getResources().getString(R.string.mobile_bank_detail_cell_payStatus, statusTemp));
            payAmount.setText(context.getResources().getString(R.string.mobile_bank_detail_cell_payAmount,
                    HelperMobileBank.checkNumbersInMultiLangs(decimalFormatter(Double.parseDouble("" + mdata.get(position).getPayedAmount()))),
                    context.getResources().getString(R.string.rial)));
            unpaidAmount.setText(context.getResources().getString(R.string.mobile_bank_detail_cell_unpaidAmount,
                    HelperMobileBank.checkNumbersInMultiLangs(decimalFormatter(Double.parseDouble("" + mdata.get(position).getUnpaidAmount()))),
                    context.getResources().getString(R.string.rial)));
            penaltyAmount.setText(context.getResources().getString(R.string.mobile_bank_detail_cell_penaltyAmount,
                    HelperMobileBank.checkNumbersInMultiLangs(decimalFormatter(Double.parseDouble("" + mdata.get(position).getPenaltyAmount()))),
                    context.getResources().getString(R.string.rial)));
            paymentDate.setText(context.getResources().getString(R.string.mobile_bank_detail_cell_paymentDate, JalaliCalendar.getPersianDate(mdata.get(position).getPayDate())));

            btnPay.setOnClickListener(v-> clickListener.onPayLoanClicked(getAdapterPosition() , mdata.get(getAdapterPosition())));
        }

        private String decimalFormatter(Double entry) {
            DecimalFormat df = new DecimalFormat(",###");
            return df.format(entry);
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
        void onPayLoanClicked(int position , BankServiceLoanDetailModel.LoanItem item);
    }

}
