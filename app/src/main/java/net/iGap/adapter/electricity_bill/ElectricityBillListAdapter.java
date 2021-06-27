package net.iGap.adapter.electricity_bill;

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
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperTracker;
import net.iGap.model.bill.BillList;
import net.iGap.model.bill.Debit;
import net.iGap.model.bill.MobileDebit;
import net.iGap.model.bill.ServiceDebit;
import net.iGap.module.CircleImageView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElectricityBillListAdapter extends RecyclerView.Adapter<ElectricityBillListAdapter.ViewHolder> {

    private Map<BillList.Bill, Debit> mdata;
    private List<BillList.Bill> bill;
    private Context context;
    private OnItemClickListener clickListener;

    public ElectricityBillListAdapter(Context context, Map<BillList.Bill, Debit> data, OnItemClickListener clickListener) {
        this.mdata = new HashMap<>();
        this.mdata.putAll(data);
        this.bill = new ArrayList<>(mdata.keySet());
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

        private TextView title, billID, billPayID, billPrice, billTime, billPayID2,
                billPayTitle, billPayTitle2, billTimeTitle, billPriceTitle, billPhone, billPhoneTitle/*, failTxt, failIcon*/;
        private ProgressBar progressPID, progressP, progressT;
        private Button pay, showDetail, retry;
        private TextView delete, edit;
        private CircleImageView logo;
        private DecimalFormat df;
//        private View failBg;

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
            logo = itemView.findViewById(R.id.billImage);
            billPayID2 = itemView.findViewById(R.id.billPayID2);
            billPayTitle = itemView.findViewById(R.id.billPayIDTitle);
            billPayTitle2 = itemView.findViewById(R.id.billPayIDTitle2);
            billTimeTitle = itemView.findViewById(R.id.billTimeTitle);
            billPriceTitle = itemView.findViewById(R.id.billPriceTitle);
            billPhone = itemView.findViewById(R.id.billPhone);
            billPhoneTitle = itemView.findViewById(R.id.billPhoneTitle);

            /*failIcon = itemView.findViewById(R.id.loadAgainIcon);
            failTxt = itemView.findViewById(R.id.loadAgain);
            failBg = itemView.findViewById(R.id.loadBackground);*/
            retry = itemView.findViewById(R.id.reloadBtn);

            df = new DecimalFormat(",###");
        }

        void initServiceView(ServiceDebit debit, int position) {
            title.setText(bill.get(position).getBillTitle());
            pay.setOnClickListener(v -> {
                if (!debit.isLoading()) {
                    clickListener.onClick(bill.get(position), OnItemClickListener.Action.PAY);
                } else
                    Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
            });

            showDetail.setOnClickListener(v -> clickListener.onClick(bill.get(position), OnItemClickListener.Action.SHOW_DETAIL));
            delete.setOnClickListener(v -> clickListener.onClick(bill.get(position), OnItemClickListener.Action.DELETE));
            edit.setOnClickListener(v -> clickListener.onClick(bill.get(position), OnItemClickListener.Action.EDIT));
            retry.setOnClickListener(v -> reloadData(position));

            if (!debit.isLoading()) {
                progressPID.setVisibility(View.GONE);
                progressP.setVisibility(View.GONE);
                progressT.setVisibility(View.GONE);
                if (debit.isFail()) {
                    retry.setVisibility(View.VISIBLE);
                    pay.setVisibility(View.GONE);
                    showDetail.setVisibility(View.GONE);
                    /*failIcon.setVisibility(View.VISIBLE);
                    failTxt.setVisibility(View.VISIBLE);
                    failBg.setVisibility(View.VISIBLE);
                    failIcon.setOnClickListener(v -> reloadData(position));
                    failTxt.setOnClickListener(v -> reloadData(position));
                    failBg.setOnClickListener(v -> reloadData(position));*/
                    return;
                }
                billPayID2.setVisibility(View.GONE);
                billPayTitle2.setVisibility(View.GONE);
                billPhoneTitle.setVisibility(View.GONE);
                billPhone.setVisibility(View.GONE);

                title.setText(bill.get(position).getBillTitle());
                if (HelperCalander.isPersianUnicode) {
                    billID.setText(HelperCalander.convertToUnicodeFarsiNumber(bill.get(position).getBillID()));
                    billPayID.setText(HelperCalander.convertToUnicodeFarsiNumber(debit.getPaymentID()));
                    billTime.setText(HelperCalander.convertToUnicodeFarsiNumber(debit.getPaymentDeadLineDate()));
                } else {
                    billID.setText(bill.get(position).getBillID());
                    billPayID.setText(debit.getPaymentID());
                    billTime.setText(debit.getPaymentDeadLineDate());
                }

                if (bill.get(position).getBillType().equals("ELECTRICITY")) {
                    logo.setImageDrawable(context.getResources().getDrawable(R.drawable.bill_elc_pec));
                    try {
                        if (HelperCalander.isPersianUnicode) {
                            billPrice.setText(HelperCalander.convertToUnicodeFarsiNumber(df.format(Integer.parseInt(debit.getTotalElectricityBillDebt())))
                                    + " " + context.getResources().getString(R.string.rial));
                            billID.setText(HelperCalander.convertToUnicodeFarsiNumber(bill.get(position).getBillID()));
                        } else {
                            billPrice.setText(df.format(Integer.parseInt(debit.getTotalElectricityBillDebt()))
                                    + " " + context.getResources().getString(R.string.rial));
                            billID.setText(bill.get(position).getBillID());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    logo.setImageDrawable(context.getResources().getDrawable(R.drawable.bill_gaz_pec));
                    try {
                        if (HelperCalander.isPersianUnicode) {
                            billPrice.setText(HelperCalander.convertToUnicodeFarsiNumber(df.format(Integer.parseInt(debit.getTotalGasBillDebt())))
                                    + " " + context.getResources().getString(R.string.rial));
                            billID.setText(HelperCalander.convertToUnicodeFarsiNumber(bill.get(position).getBillID()));
                        } else {
                            billPrice.setText(df.format(Integer.parseInt(debit.getTotalGasBillDebt()))
                                    + " " + context.getResources().getString(R.string.rial));
                            billID.setText(bill.get(position).getBillID());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        void initPhoneView(MobileDebit debit, int position) {
            title.setText(bill.get(position).getBillTitle());
            pay.setOnClickListener(v -> {
                if (!debit.isLoading()) {
                    switch (bill.get(position).getBillType()) {
                        case "ELECTRICITY":
                            HelperTracker.sendTracker(HelperTracker.TRACKER_ELECTRIC_BILL_PAY);
                            break;
                        case "GAS":
                            HelperTracker.sendTracker(HelperTracker.TRACKER_GAS_BILL_PAY);
                            break;
                        case "PHONE":
                            HelperTracker.sendTracker(HelperTracker.TRACKER_PHONE_BILL_PAY);
                            break;
                        case "MOBILE_MCI":
                            HelperTracker.sendTracker(HelperTracker.TRACKER_MOBILE_BILL_PAY);
                            break;
                    }
                    clickListener.onClick(bill.get(position), OnItemClickListener.Action.LAST_PAY);
                } else
                    Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
            });
            showDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!debit.isLoading()) {
                        switch (bill.get(position).getBillType()) {
                            case "ELECTRICITY":
                                HelperTracker.sendTracker(HelperTracker.TRACKER_ELECTRIC_BILL_PAY);
                                break;
                            case "GAS":
                                HelperTracker.sendTracker(HelperTracker.TRACKER_GAS_BILL_PAY);
                                break;
                            case "PHONE":
                                HelperTracker.sendTracker(HelperTracker.TRACKER_PHONE_BILL_PAY);
                                break;
                            case "MOBILE_MCI":
                                HelperTracker.sendTracker(HelperTracker.TRACKER_MOBILE_BILL_PAY);
                                break;
                        }
                        clickListener.onClick(bill.get(position), OnItemClickListener.Action.MID_PAY);
                    } else
                        Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                }
            });
            delete.setOnClickListener(v -> clickListener.onClick(bill.get(position), OnItemClickListener.Action.DELETE));
            edit.setOnClickListener(v -> clickListener.onClick(bill.get(position), OnItemClickListener.Action.EDIT));
            retry.setOnClickListener(v -> reloadData(position));

            if (!debit.isLoading()) {
                progressPID.setVisibility(View.GONE);
                progressP.setVisibility(View.GONE);
                progressT.setVisibility(View.GONE);
                if (debit.isFail()) {
                    retry.setVisibility(View.VISIBLE);
                    pay.setVisibility(View.GONE);
                    showDetail.setVisibility(View.GONE);
                    /*failIcon.setVisibility(View.VISIBLE);
                    failTxt.setVisibility(View.VISIBLE);
                    failBg.setVisibility(View.VISIBLE);
                    failIcon.setOnClickListener(v -> reloadData(position));
                    failTxt.setOnClickListener(v -> reloadData(position));
                    failBg.setOnClickListener(v -> reloadData(position));*/
                    return;
                }
                // TODO: 6/1/2020 this 4 lines for pay ID
                //****
                billPayID2.setVisibility(View.GONE);
                billPayTitle2.setVisibility(View.GONE);
                billPayTitle.setVisibility(View.GONE);
                billPayID.setVisibility(View.GONE);
                //****
                billPhoneTitle.setVisibility(View.VISIBLE);
                billPhone.setVisibility(View.VISIBLE);

                billPriceTitle.append(" " + context.getResources().getText(R.string.elecBill_cell_billPayLastTerm));
                billTimeTitle.setText(context.getResources().getText(R.string.elecBill_pay_billPrice) + " " + context.getResources().getText(R.string.elecBill_cell_billPayMidTerm));
                billPayTitle.append(" " + context.getResources().getText(R.string.elecBill_cell_billPayLastTerm));
                billPayTitle2.append(" " + context.getResources().getText(R.string.elecBill_cell_billPayMidTerm));
                pay.setText(context.getResources().getText(R.string.elecBill_cell_billPayPhoneLastBtn));
                showDetail.setText(context.getResources().getText(R.string.elecBill_cell_billPayPhoneMidBtn));

                if (HelperCalander.isPersianUnicode) {
                    try {
                        billID.setText(HelperCalander.convertToUnicodeFarsiNumber(debit.getLastTerm().getBillID()));
                        billPayID.setText(HelperCalander.convertToUnicodeFarsiNumber(debit.getLastTerm().getPayID()));
                        billPayID2.setText(HelperCalander.convertToUnicodeFarsiNumber(debit.getMidTerm().getPayID()));
                        billPrice.setText(HelperCalander.convertToUnicodeFarsiNumber(df.format(Integer.parseInt(debit.getLastTerm().getAmount())))
                                + " " + context.getResources().getString(R.string.rial));
                        billTime.setText(HelperCalander.convertToUnicodeFarsiNumber(df.format(Integer.parseInt(debit.getMidTerm().getAmount())))
                                + " " + context.getResources().getString(R.string.rial));
                        billPhone.setText(HelperCalander.convertToUnicodeFarsiNumber(
                                (bill.get(position).getAreaCode() == null ? "" : (bill.get(position).getAreaCode() + "-"))
                                        + bill.get(position).getPhoneNumber()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        billID.setText(debit.getLastTerm().getBillID());
                        billPayID.setText(debit.getLastTerm().getPayID());
                        billPayID2.setText(debit.getMidTerm().getPayID());
                        billPrice.setText(df.format(Integer.parseInt(debit.getLastTerm().getAmount()))
                                + " " + context.getResources().getString(R.string.rial));
                        billTime.setText(df.format(Integer.parseInt(debit.getMidTerm().getAmount()))
                                + " " + context.getResources().getString(R.string.rial));
                        billPhone.setText((bill.get(position).getAreaCode() == null ? "" : (bill.get(position).getAreaCode() + "-"))
                                + bill.get(position).getPhoneNumber());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (bill.get(position).getBillType().equals("MOBILE_MCI"))
                    logo.setImageDrawable(context.getResources().getDrawable(R.drawable.bill_mci_pec));
                else
                    logo.setImageDrawable(context.getResources().getDrawable(R.drawable.bill_telecom_pec));
            }
        }

        void initView(int position) {
            if (mdata.get(bill.get(position)).getData() instanceof ServiceDebit) {
                ServiceDebit serviceDebit = (ServiceDebit) mdata.get(bill.get(position)).getData();
                initServiceView(serviceDebit, position);
            } else {
                MobileDebit phoneDebit = (MobileDebit) mdata.get(bill.get(position)).getData();
                initPhoneView(phoneDebit, position);
            }
        }

        void reloadData(int position) {
            clickListener.onClick(bill.get(position), OnItemClickListener.Action.RELOAD);
            retry.setVisibility(View.GONE);
            pay.setVisibility(View.VISIBLE);
            showDetail.setVisibility(View.VISIBLE);
            /*failIcon.setVisibility(View.GONE);
            failTxt.setVisibility(View.GONE);
            failBg.setVisibility(View.GONE);*/
            progressPID.setVisibility(View.VISIBLE);
            progressP.setVisibility(View.VISIBLE);
            progressT.setVisibility(View.VISIBLE);
        }
    }

    public interface OnItemClickListener {
        enum Action {DELETE, EDIT, SHOW_DETAIL, PAY, MID_PAY, LAST_PAY, RELOAD}

        void onClick(BillList.Bill item, Action btnAction);
    }

}
