package net.iGap.adapter.kuknos;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.kuknos.Model.Parsian.KuknosCreateAccountOpResponse;
import net.iGap.kuknos.Model.Parsian.KuknosOperationResponse;
import net.iGap.kuknos.Model.Parsian.KuknosPaymentOpResponse;
import net.iGap.module.mobileBank.JalaliCalendar;
import net.iGap.kuknos.Repository.UserRepo;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class WalletHistoryRAdapter extends RecyclerView.Adapter<WalletHistoryRAdapter.ViewHolder> {

    private KuknosOperationResponse kuknosWHistoryMS;
    private Context context;
    private UserRepo userRepo = new UserRepo();
    private String accountID;

    public WalletHistoryRAdapter(KuknosOperationResponse kuknosWHistoryMS, Context context) {
        this.kuknosWHistoryMS = kuknosWHistoryMS;
        this.context = context;
        accountID = userRepo.getAccountID();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_kuknos_whistory_cell, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.initView(kuknosWHistoryMS.getOperations().get(i));
        /*if (kuknosWHistoryMS.getOperations().get(i) instanceof KuknosPaymentOpResponse) {
            viewHolder.initView(kuknosWHistoryMS.getOperations().get(i));
        } else {
            viewHolder.initViewCreateAccount((KuknosCreateAccountOpResponse) kuknosWHistoryMS.getOperations().get(i));
        }*/
    }

    @Override
    public int getItemCount() {
        return kuknosWHistoryMS.getOperations().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView date;
        private TextView amount;
        private TextView desc;
        private TextView icon;
        private TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.kuknos_wHistoryCell_date);
            amount = itemView.findViewById(R.id.kuknos_wHistoryCell_amount);
            desc = itemView.findViewById(R.id.kuknos_wHistoryCell_desc);
            icon = itemView.findViewById(R.id.kuknos_wHistoryCell_icon);
            time = itemView.findViewById(R.id.kuknos_wHistoryCell_time);

        }

        public void initView(KuknosPaymentOpResponse model) {
            String[] temp = model.getCreatedAt().split("T");
            date.setText(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(getDateTime(model.getCreatedAt())) : getDateTime(model.getCreatedAt()));
            time.setText(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(convertTime(temp[1].substring(0, 7))) : convertTime(temp[1].substring(0, 7)));
            DecimalFormat df = new DecimalFormat("#,##0.00");
            if (model.getType() != null && model.getType().equals("create_account")) {
                amount.setText((HelperCalander.isPersianUnicode ?
                        HelperCalander.convertToUnicodeFarsiNumber(df.format(Double.valueOf(model.getStartingBalance())))
                        : df.format(Double.valueOf(model.getStartingBalance()))) + " PMN");
            } else {
                String currency;
                if (model.getAsset().getType().equals("native"))
                    currency = "PMN";
                else
                    currency = model.getAssetCode();
                amount.setText((HelperCalander.isPersianUnicode ?
                        HelperCalander.convertToUnicodeFarsiNumber(df.format(Double.valueOf(model.getAmount())))
                        : df.format(Double.valueOf(model.getAmount()))) + " " + currency);
            }
            if (model.getType() != null && model.getType().equals("create_account")) {
                desc.setText(context.getResources().getString(R.string.kuknos_wHistory_receive));
                icon.setText(context.getResources().getString(R.string.download_ic));
            } else if (accountID.equals(model.getFrom())) {
                desc.setText(context.getResources().getString(R.string.kuknos_wHistory_sent));
                icon.setText(context.getResources().getString(R.string.upload_ic));
                icon.setTextColor(Color.RED);
            } else {
                desc.setText(context.getResources().getString(R.string.kuknos_wHistory_receive));
                icon.setText(context.getResources().getString(R.string.download_ic));
//                icon.setTextColor(context.getResources().getColor(R.color.buttonColor));
            }
        }

        private String getDateTime(String entry) {
            entry = entry.replace("T", " ");
            entry = entry.replace("Z", "");
            try {
                /*String [] dateTime = entry.split(" ");
                String time = dateTime[1];*/

                String convertDate, convertTime;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date mDate = sdf.parse(entry);
                long timeInMilliseconds = mDate.getTime();
                convertDate = HelperCalander.checkHijriAndReturnTime(timeInMilliseconds / DateUtils.SECOND_IN_MILLIS);
//                convertTime = convertTime(time);
                if (HelperCalander.isPersianUnicode) {
                    convertDate = HelperCalander.convertToUnicodeFarsiNumber(convertDate);
                }
                return convertDate /*+ " | " + convertTime*/;
            } catch (Exception e) {
                e.printStackTrace();
                return JalaliCalendar.getPersianDate(entry);
            }
        }

        private String convertTime(String dateStr) {
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = null;
            try {
                date = df.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            df = new SimpleDateFormat("HH:mm");
            df.setTimeZone(TimeZone.getTimeZone("GMT+4:30"));
            return df.format(date);
        }

        public void initViewCreateAccount(KuknosCreateAccountOpResponse model) {
            String[] temp = model.getCreatedAt().split("T");
            date.setText(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(temp[0]) : temp[0]);
            time.setText(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(temp[1].substring(0, 5)) : temp[1].substring(0, 5));
            DecimalFormat df = new DecimalFormat("#,###.00");
            amount.setText(HelperCalander.isPersianUnicode ?
                    HelperCalander.convertToUnicodeFarsiNumber(df.format(Double.valueOf(model.getStartingBalance())))
                    : df.format(Double.valueOf(model.getStartingBalance())));
            desc.setText(context.getResources().getString(R.string.kuknos_wHistory_creatAccount));
//            icon.setTextColor(context.getResources().getColor(R.color.buttonColor));
        }
    }
}
