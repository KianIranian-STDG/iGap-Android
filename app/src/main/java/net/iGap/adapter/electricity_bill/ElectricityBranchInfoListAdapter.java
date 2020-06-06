package net.iGap.adapter.electricity_bill;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.model.bill.BillInfo;
import net.iGap.model.bill.GasBranchData;
import net.iGap.model.electricity_bill.ElectricityBranchData;
import net.iGap.model.electricity_bill.ElectricityResponseModel;

public class ElectricityBranchInfoListAdapter extends RecyclerView.Adapter<ElectricityBranchInfoListAdapter.ViewHolder> {

    private ElectricityResponseModel<ElectricityBranchData> mDataElec;
    private GasBranchData mDataGas;
    private BillInfo.BillType type;
    private Context context;

    public ElectricityBranchInfoListAdapter(Context context, ElectricityResponseModel<ElectricityBranchData> data, BillInfo.BillType type) {
        this.mDataElec = data;
        this.context = context;
        this.type = type;
    }

    public ElectricityBranchInfoListAdapter(Context context, GasBranchData data, BillInfo.BillType type) {
        this.mDataGas = data;
        this.context = context;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_elec_branch_info_cell, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        switch (type) {
            case ELECTRICITY:
                viewHolder.initViewElec(position);
                break;
            case GAS:
                viewHolder.initViewGas(position);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return 21;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title, desc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.billTitle);
            desc = itemView.findViewById(R.id.billValue);
        }

        void initViewElec(int position) {
            switch (position) {
                case 0:
                    title.setText("جزئیات قبض شما");
                    desc.setVisibility(View.GONE);
                    break;
                case 1:
                    title.setText("شناسه قبض");
                    desc.setText(getRightUnicodeFormat(mDataElec.getData().getBillID()));
                    break;
                case 2:
                    title.setText("شناسه پرداخت");
                    desc.setText(getRightUnicodeFormat(mDataElec.getData().getPaymentID()));
                    break;
                case 3:
                    title.setText("کد شرکت توزیع");
                    desc.setText(getRightUnicodeFormat(mDataElec.getData().getCompanyCode()));
                    break;
                case 4:
                    title.setText("شرکت توزیع");
                    desc.setText(mDataElec.getData().getCompanyName());
                    break;
                case 5:
                    title.setText("فاز");
                    if (mDataElec.getData().getPhase().equals("1"))
                        desc.setText("تکفاز");
                    else
                        desc.setText("سه فاز");
                    break;
                case 6:
                    title.setText("ولتاژ");
                    if (mDataElec.getData().getVoltageType().equals("1"))
                        desc.setText("اولیه");
                    else
                        desc.setText("ثانویه");
                    break;
                case 7:
                    title.setText("نوع تعرفه");
                    switch (mDataElec.getData().getTariffType()) {
                        case "10":
                            desc.setText("خانگی");
                            break;
                        case "11":
                            desc.setText("خانگی آزاد");
                            break;
                        case "20":
                            desc.setText("عمومی");
                            break;
                        case "21":
                            desc.setText("عمومی آزاد");
                            break;
                        case "30":
                            desc.setText("کشاورزی");
                            break;
                        case "31":
                            desc.setText("کشاورزی آزاد");
                            break;
                        case "40":
                            desc.setText("صنعتی");
                            break;
                        case "41":
                            desc.setText("صنعتی آزاد");
                            break;
                        case "50":
                            desc.setText("تجاری");
                            break;
                        case "51":
                            desc.setText("تجاری آزاد");
                            break;
                    }
                    break;
                case 8:
                    title.setText("نوع مشترک");
                    if (mDataElec.getData().getCustomerType() != null) {
                        if (mDataElec.getData().getCustomerType().equals("1")) {
                            desc.setText("حقیقی");
                        } else {
                            desc.setText("حقوقی");
                        }
                    } else {
                        desc.setText("");
                    }
                    break;
                case 9:
                    title.setText("نام مشترک");
                    desc.setText(mDataElec.getData().getCustomerName() + " " + mDataElec.getData().getCustomerFamily());
                    break;
                case 10:
                    title.setText("شماره مشترک");
                    desc.setText(getRightUnicodeFormat(mDataElec.getData().getTelNumber()));
                    break;
                case 11:
                    title.setText("شماره همراه مشترک");
                    desc.setText(getRightUnicodeFormat(mDataElec.getData().getMobileNumber()));
                    break;
                case 12:
                    title.setText("آدرس محل انشعاب");
                    desc.setText(mDataElec.getData().getServiceAddress());
                    break;
                case 13:
                    title.setText("کد پستی محل انشعاب");
                    desc.setText(getRightUnicodeFormat(mDataElec.getData().getServicePostCode()));
                    break;
                case 14:
                    title.setText("محدوده جغرافیایی");
                    if (mDataElec.getData().getLocation_status().equals("1"))
                        desc.setText("شهری");
                    else if (mDataElec.getData().getLocation_status().equals("2"))
                        desc.setText("روستایی");
                    else
                        desc.setText("خارج از محدوده خدمات شهری");
                    break;
                case 15:
                    title.setText("شماره بدنه کنتور");
                    desc.setText(getRightUnicodeFormat(mDataElec.getData().getSerialNumber()));
                    break;
                case 16:
                    title.setText("مهلت پرداخت بدهی");
                    desc.setText(getRightUnicodeFormat(mDataElec.getData().getPaymentDeadLineDate()));
                    break;
                case 17:
                    title.setText("آخرین تاریخ قرائت کنتور");
                    desc.setText(getRightUnicodeFormat(mDataElec.getData().getLastReadDate()));
                    break;
                case 18:
                    title.setText("تاریخ انقضا پروانه");
                    desc.setText(getRightUnicodeFormat(mDataElec.getData().getLicenseExpireDate()));
                    break;
                case 19:
                    title.setText("قدرت قراردادی");
                    desc.setText(mDataElec.getData().getContractDemand());
                    break;
                default:
                    title.setText("");
                    desc.setText("");
            }
        }

        void initViewGas(int position) {
            switch (position) {
                case 0:
                    title.setText("جزئیات قبض شما");
                    desc.setVisibility(View.GONE);
                    break;
                case 1:
                    title.setText("شناسه قبض");
                    desc.setText(getRightUnicodeFormat((mDataGas.getBillID())));
                    break;
                case 2:
                    title.setText("شناسه پرداخت");
                    desc.setText(getRightUnicodeFormat(mDataGas.getPayID()));
                    break;
                case 3:
                    title.setText("شهر");
                    desc.setText(mDataGas.getCity());
                    break;
                case 4:
                    title.setText("تعداد واحد");
                    desc.setText(getRightUnicodeFormat(mDataGas.getUnit()));
                    break;
                case 5:
                    title.setText("شماره اشتراک");
                    desc.setText(getRightUnicodeFormat(mDataGas.getBuildID()));
                    break;
                case 6:
                    title.setText("سریال کنتور");
                    desc.setText(getRightUnicodeFormat(mDataGas.getSerialNumber()));
                    break;
                case 7:
                    title.setText("ظرفیت");
                    desc.setText(getRightUnicodeFormat(mDataGas.getCapacity()));
                    break;
                case 8:
                    title.setText("نوع مصرف");
                    desc.setText(mDataGas.getKind());
                    break;
                case 9:
                    title.setText("تاریخ قرائت پیشین");
                    desc.setText(getRightUnicodeFormat(mDataGas.getPreviousDate().replaceAll("-", "/")));
                    break;
                case 10:
                    title.setText("تاریخ قرائت فعلی");
                    desc.setText(getRightUnicodeFormat(mDataGas.getCurrentDate().replaceAll("-", "/")));
                    break;
                case 11:
                    title.setText("رقم کنتور پیشین");
                    desc.setText(getRightUnicodeFormat(mDataGas.getPreviousValue()));
                    break;
                case 12:
                    title.setText("رقم کنتور فعلی");
                    desc.setText(getRightUnicodeFormat(mDataGas.getCurrentValue()));
                    break;
                case 13:
                    title.setText("مصرف");
                    desc.setText(getRightUnicodeFormat(mDataGas.getStandardConsumption()));
                    break;
                case 14:
                    title.setText("بهای گاز مصرفی");
                    desc.setText(getRightUnicodeFormat(mDataGas.getGasPriceValue()) + " " + context.getResources().getString(R.string.rial));
                    break;
                case 15:
                    title.setText("آبونمان");
                    desc.setText(getRightUnicodeFormat(mDataGas.getAbonmanValue()) + " " + context.getResources().getString(R.string.rial));
                    break;
                case 16:
                    title.setText("مالیات");
                    desc.setText(getRightUnicodeFormat(mDataGas.getTax()) + " " + context.getResources().getString(R.string.rial));
                    break;
                case 17:
                    title.setText("عوارض گازرسانی");
                    desc.setText(getRightUnicodeFormat(mDataGas.getVillageTax()) + " " + context.getResources().getString(R.string.rial));
                    break;
                case 18:
                    title.setText("بیمه");
                    desc.setText(getRightUnicodeFormat(mDataGas.getAssurance()) + " " + context.getResources().getString(R.string.rial));
                    break;
                case 19:
                    title.setText("کسر هزار ریال");
                    desc.setText(getRightUnicodeFormat(mDataGas.getCurrentRounding()) + " " + context.getResources().getString(R.string.rial));
                    break;
                case 20:
                    title.setText("شماره سری");
                    desc.setText(getRightUnicodeFormat(mDataGas.getSequenceNumber()));
                    break;
                default:
                    title.setText("");
                    desc.setText("");
            }
        }

        String getRightUnicodeFormat(String input) {
            if (HelperCalander.isPersianUnicode) {
                return HelperCalander.convertToUnicodeFarsiNumber(input);
            } else
                return input;
        }
    }

}
