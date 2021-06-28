package net.iGap.adapter.mobileBank;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperMobileBank;
import net.iGap.model.mobileBank.BankHistoryModel;
import net.iGap.module.mobileBank.JalaliCalendar;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MobileBankHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int historyType = 0;
    private static final int loadType = 1;
    private List<BankHistoryModel> mdata;
    private OnItemClickListener clickListener;
    private Context context;

    public MobileBankHistoryAdapter(List<BankHistoryModel> dates, OnItemClickListener clickListener) {
        this.mdata = dates;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        context = viewGroup.getContext();
        switch (viewType) {
            case historyType:
                View singleVH = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mobile_bank_history_item, viewGroup, false);
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
            case historyType:
                ((ViewHolder) viewHolder).initView(position);
                break;
            case loadType:
                ((LoadViewHolder) viewHolder).initVH();
                break;
            default:
                break;
        }
    }

    public BankHistoryModel getItem(int position) {
        return mdata.get(position);
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
        return historyType;
    }

    public void addItems(List<BankHistoryModel> postItems) {
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
        BankHistoryModel item = mdata.get(position);
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

        private TextView title, value, date, arrow, statusIcon;
        private CardView container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            value = itemView.findViewById(R.id.amount);
            date = itemView.findViewById(R.id.time);
            container = itemView.findViewById(R.id.container);
            arrow = itemView.findViewById(R.id.arrow);
            statusIcon = itemView.findViewById(R.id.status_ic);

        }

        void initView(int position) {
            if (mdata.get(position).getTransferAmount() < 0) {
                title.setText("برداشت");
                statusIcon.setText(R.string.icon_withdrawing_money_from_card);
                statusIcon.setTextColor(context.getResources().getColor(R.color.brown));
                value.setTextColor(context.getResources().getColor(R.color.brown));
            } else {
                title.setText("واریز");
                statusIcon.setText(R.string.icon_incoming_money_to_card);
                statusIcon.setTextColor(context.getResources().getColor(R.color.green));
                value.setTextColor(context.getResources().getColor(R.color.green));
            }
            DecimalFormat df = new DecimalFormat(",###");
            value.setText(HelperMobileBank.checkNumbersInMultiLangs("" + df.format(mdata.get(position).getTransferAmount())) + " " + context.getString(R.string.rial));
            date.setText(getDateTime(mdata.get(position).getDate()));
            container.setOnClickListener(v -> clickListener.onClick(position));
            if (HelperCalander.isPersianUnicode)
                arrow.setRotation(90);
        }
    }

    private String getDateTime(String entry){
        try {
            String [] dateTime = entry.split(" ");
            String time = dateTime[1];

            String convertDate , convertTime;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date mDate = sdf.parse(entry);
            long timeInMilliseconds = mDate.getTime();
            convertDate =  HelperCalander.checkHijriAndReturnTime(timeInMilliseconds / DateUtils.SECOND_IN_MILLIS);
            convertTime = convertTime(time);
            if(HelperCalander.isPersianUnicode){
                convertDate = HelperCalander.convertToUnicodeFarsiNumber(convertDate);
            }
            return convertDate + " | " + convertTime;
        }catch (Exception e){
            return JalaliCalendar.getPersianDate(entry);
        }
    }

    private String convertTime(String dateStr){
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        df.setTimeZone(TimeZone.getTimeZone("GMT+3:30"));
        return df.format(date);
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
    }

}
