package net.iGap.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.helper.HelperDataUsage;
import net.iGap.interfaces.DataUsageListener;
import net.iGap.module.AndroidUtils;
import net.iGap.module.structs.DataUsageStruct;
import net.iGap.realm.RealmDataUsage;

import java.util.ArrayList;
import java.util.Collection;

public class DataUsageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<DataUsageStruct> dataList;
    private long totalReceive;
    private long totalSend;
    private boolean connectivityType;
    private DataUsageListener clearData;

    public DataUsageAdapter(Context context, ArrayList<DataUsageStruct> dataList, long totalReceive, long totalSend,boolean connectivityType,DataUsageListener clearData) {
        Log.i("WWW", "totalReceive: "+totalReceive);
        this.context = context;
        this.dataList = dataList;
        this.totalReceive = totalReceive;
        this.totalSend = totalSend;
        this.connectivityType=connectivityType;
        this.clearData=clearData;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());

        switch (viewType) {
            case 0:
                return new BaseHolder(inflater.inflate(R.layout.item_data_usage, parent, false));
            case 1:
                return new TotalViewHolder(inflater.inflate(R.layout.item_data_usage_total, parent, false));
            case 2:
                return new ClearDataHolder(inflater.inflate(R.layout.item_data_usage_reset, parent, false));
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BaseHolder) {
            ((BaseHolder) holder).txtByteReceivedNum.setText(AndroidUtils.humanReadableByteCount(dataList.get(position).getByteReceived(),true));
            ((BaseHolder) holder).txtByteSentNum.setText(AndroidUtils.humanReadableByteCount(dataList.get(position).getByteSend(),true));

            ((BaseHolder) holder).txtTitle.setText(dataList.get(position).getTitle());
            ((BaseHolder) holder).txtSentNum.setText(String.valueOf(dataList.get(position).getSendNum()));
            ((BaseHolder) holder).txtReceivedNum.setText(String.valueOf(dataList.get(position).getReceivednum()));

        } else if (holder instanceof TotalViewHolder) {

          //  ((TotalViewHolder)holder).txtTotalReceivedByte.setText(String.valueOf(totalReceive));
            ((TotalViewHolder)holder).txtTotalReceivedByte.setText(AndroidUtils.humanReadableByteCount(totalReceive,true));
          //  ((TotalViewHolder)holder).txtTotalSentByte.setText(String.valueOf(totalSend));
            ((TotalViewHolder)holder).txtTotalSentByte.setText(AndroidUtils.humanReadableByteCount(totalSend,true));

        } else if (holder instanceof ClearDataHolder){
            ((ClearDataHolder) holder).txtClearData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clearData.doClearDB(connectivityType);


                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {

        return dataList.get(position).getViewType();
    }


    public class BaseHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtSentNum, txtReceivedNum, txtByteSentNum, txtByteReceivedNum;

        public BaseHolder(View itemView) {
            super(itemView);
            txtByteReceivedNum = (TextView) itemView.findViewById(R.id.txtByteReceivedNum);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtSentNum = (TextView) itemView.findViewById(R.id.txtSentNum);
            txtReceivedNum = (TextView) itemView.findViewById(R.id.txtReceivedNum);
            txtByteSentNum = (TextView) itemView.findViewById(R.id.txtByteSentNum);

        }
    }

    public class TotalViewHolder extends RecyclerView.ViewHolder {
        TextView txtTotalSentByte, txtTotalReceivedByte;

        public TotalViewHolder(View itemView) {
            super(itemView);
            txtTotalSentByte = itemView.findViewById(R.id.txtTotalSentByte);
            txtTotalReceivedByte = itemView.findViewById(R.id.txtTotalReceivedByte);
        }
    }

    public class ClearDataHolder extends RecyclerView.ViewHolder {
        TextView txtClearData;
        public ClearDataHolder(View itemView) {
            super(itemView);
            txtClearData=itemView.findViewById(R.id.txtClearData);
        }
    }

}
