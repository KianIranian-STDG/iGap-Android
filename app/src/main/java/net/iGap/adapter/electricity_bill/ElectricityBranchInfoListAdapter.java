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
import net.iGap.model.electricity_bill.ElectricityBranchData;
import net.iGap.model.electricity_bill.ElectricityResponseModel;

public class ElectricityBranchInfoListAdapter extends RecyclerView.Adapter<ElectricityBranchInfoListAdapter.ViewHolder> {

    private ElectricityResponseModel<ElectricityBranchData> mdata;
    private Context context;

    public ElectricityBranchInfoListAdapter(Context context, ElectricityResponseModel<ElectricityBranchData> data) {
        this.mdata = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_elec_branch_info_cell, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.initView(position);
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title, desc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.billTitle);
            desc = itemView.findViewById(R.id.billValue);
        }

        void initView(int position) {
            switch (position) {
                case 0:
                    title.setText("جزئیات قبض شما");
                    desc.setVisibility(View.GONE);
                    break;
                case 1:
                    title.setText("شناسه قبض");
                    if (HelperCalander.isPersianUnicode) {
                        desc.setText(HelperCalander.convertToUnicodeFarsiNumber(mdata.getData().getBillID()));
                    }
                    else
                        desc.setText(mdata.getData().getBillID());
                    break;
                case 2:
                    title.setText("شناسه پرداخت");
                    desc.setText(mdata.getData().getPaymentID());
                    break;
                case 3:
                    title.setText("کد شرکت توزیع");
                    desc.setText(mdata.getData().getCompanyCode());
                    break;
                case 4:
                    title.setText("شرکت توزیع");
                    desc.setText(mdata.getData().getCompanyName());
                    break;
                case 5:
                    title.setText("فاز");
                    if (mdata.getData().getPhase().equals("1"))
                        desc.setText("تکفاز");
                    else
                        desc.setText("سه فاز");
                    break;
                case 6:
                    title.setText("ولتاژ");
                    if (mdata.getData().getVoltageType().equals("1"))
                        desc.setText("اولیه");
                    else
                        desc.setText("ثانویه");
                    break;
                case 7:
                    title.setText("نوع تعرفه");
                    switch (mdata.getData().getTariffType()) {
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
                    if (mdata.getData().getCustomerType() != null){
                        if (mdata.getData().getCustomerType().equals("1")) {
                            desc.setText("حقیقی");
                        }else {
                            desc.setText("حقوقی");
                        }
                    }else {
                        desc.setText("");
                    }
                    break;
                case 9:
                    title.setText("نام مشترک");
                    desc.setText(mdata.getData().getCustomerName() + " " + mdata.getData().getCustomerFamily());
                    break;
                case 10:
                    title.setText("شماره مشترک");
                    desc.setText(mdata.getData().getTelNumber());
                    break;
                case 11:
                    title.setText("شماره همراه مشترک");
                    desc.setText(mdata.getData().getMobileNumber());
                    break;
                case 12:
                    title.setText("آدرس محل انشعاب");
                    desc.setText(mdata.getData().getServiceAddress());
                    break;
                case 13:
                    title.setText("کد پستی محل انشعاب");
                    desc.setText(mdata.getData().getServicePostCode());
                    break;
                case 14:
                    title.setText("محدوده جغرافیایی");
                    if (mdata.getData().getLocation_status().equals("1"))
                        desc.setText("شهری");
                    else if (mdata.getData().getLocation_status().equals("2"))
                        desc.setText("روستایی");
                    else
                        desc.setText("خارج از محدوده خدمات شهری");
                    break;
                case 15:
                    title.setText("شماره بدنه کنتور");
                    desc.setText(mdata.getData().getSerialNumber());
                    break;
                case 16:
                    title.setText("مهلت پرداخت بدهی");
                    desc.setText(mdata.getData().getPaymentDeadLineDate());
                    break;
                case 17:
                    title.setText("آخرین تاریخ قرائت کنتور");
                    desc.setText(mdata.getData().getLastReadDate());
                    break;
                case 18:
                    title.setText("تاریخ انقضا پروانه");
                    desc.setText(mdata.getData().getLicenseExpireDate());
                    break;
                case 19:
                    title.setText("قدرت قراردادی");
                    desc.setText(mdata.getData().getContractDemand());
                    break;
            }
        }
    }

    public interface OnItemClickListener {
        enum Actoin {DELETE, EDIT, SHOW_DETAIL, PAY}
        void onClick(int position, Actoin btnAction);
    }

}
