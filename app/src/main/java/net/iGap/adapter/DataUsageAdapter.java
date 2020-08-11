package net.iGap.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.R;
import net.iGap.module.AndroidUtils;
import net.iGap.module.structs.DataUsageStruct;
import net.iGap.observers.interfaces.DataUsageListener;

import java.util.ArrayList;

public class DataUsageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<DataUsageStruct> dataList;
    private long totalReceive;
    private long totalSend;
    private boolean connectivityType;
    private DataUsageListener clearData;

    public DataUsageAdapter(ArrayList<DataUsageStruct> dataList, long totalReceive, long totalSend, boolean connectivityType, DataUsageListener clearData) {
        this.dataList = dataList;
        this.totalReceive = totalReceive;
        this.totalSend = totalSend;
        this.connectivityType = connectivityType;
        this.clearData = clearData;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

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
            ((BaseHolder) holder).txtByteReceivedNum.setText(AndroidUtils.humanReadableByteCount(dataList.get(position).getByteReceived(), true));
            ((BaseHolder) holder).txtByteSentNum.setText(AndroidUtils.humanReadableByteCount(dataList.get(position).getByteSend(), true));

            switch (dataList.get(position).getTitle()) {
                case "IMAGE":
                    ((BaseHolder) holder).txtTitle.setText(R.string.image_message);
                    break;
                case "VIDEO":
                    ((BaseHolder) holder).txtTitle.setText(R.string.video_message);

                    break;

                case "FILE":
                    ((BaseHolder) holder).txtTitle.setText(R.string.file_message);

                    break;
                case "AUDIO":
                    ((BaseHolder) holder).txtTitle.setText(R.string.audio_message);
                    break;

                case "UNRECOGNIZED":
                    ((BaseHolder) holder).txtTitle.setText(R.string.st_Other);
                    break;

            }
            ((BaseHolder) holder).txtSentNum.setText(String.valueOf(dataList.get(position).getSendNum()));

            ((BaseHolder) holder).txtReceivedNum.setText(String.valueOf(dataList.get(position).getReceivednum()));

        } else if (holder instanceof TotalViewHolder) {

            //  ((TotalViewHolder)holder).txtTotalReceivedByte.setText(String.valueOf(totalReceive));
            ((TotalViewHolder) holder).txtTotalReceivedByte.setText(AndroidUtils.humanReadableByteCount(totalReceive, true));
            //  ((TotalViewHolder)holder).txtTotalSentByte.setText(String.valueOf(totalSend));
            ((TotalViewHolder) holder).txtTotalSentByte.setText(AndroidUtils.humanReadableByteCount(totalSend, true));

        } else if (holder instanceof ClearDataHolder) {
            ((ClearDataHolder) holder).txtClearData.setText(R.string.clear_data_usage);
            ((ClearDataHolder) holder).rvClearDataUsage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new MaterialDialog.Builder(view.getContext()).title(R.string.clearDataUsage)
                            .positiveText(R.string.yes)
                            .onPositive((dialog, which) -> {
                                clearData.doClearDB(connectivityType);
                                dialog.dismiss();
                            }).negativeText(R.string.no).show();


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
        TextView txtTitle, txtSentNum, txtReceivedNum, txtByteSentNum, txtByteReceivedNum, txtByteReceived, txtByteSent, txtReceived, txtSent;

        public BaseHolder(View itemView) {
            super(itemView);
            txtByteReceivedNum = itemView.findViewById(R.id.txtByteReceivedNum);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtSentNum = itemView.findViewById(R.id.txtSentNum);
            txtReceivedNum = itemView.findViewById(R.id.txtReceivedNum);
            txtByteSentNum = itemView.findViewById(R.id.txtByteSentNum);
            txtByteReceived = itemView.findViewById(R.id.txtByteReceived);
            txtByteSent = itemView.findViewById(R.id.txtByteSent);
            txtReceived = itemView.findViewById(R.id.txtReceived);
            txtSent = itemView.findViewById(R.id.txtSent);

            txtByteReceived.setText(R.string.bytes_received);
            txtByteSent.setText(R.string.bytes_sent);
            txtReceived.setText(R.string.received);
            txtSent.setText(R.string.sent);
        }
    }

    public class TotalViewHolder extends RecyclerView.ViewHolder {
        TextView txtTotalSentByte, txtTotalReceivedByte, txtTotalReceived, txtTotalSent, txtTitle;

        public TotalViewHolder(View itemView) {
            super(itemView);
            txtTotalSentByte = itemView.findViewById(R.id.txtTotalSentByte);

            txtTotalReceivedByte = itemView.findViewById(R.id.txtTotalReceivedByte);

            txtTotalReceived = itemView.findViewById(R.id.txtTotalReceived);
            txtTotalReceived.setText(R.string.total_received);

            txtTotalSent = itemView.findViewById(R.id.txtTotalSent);
            txtTotalSent.setText(R.string.total_sent);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtTitle.setText(R.string.total);
        }
    }

    public class ClearDataHolder extends RecyclerView.ViewHolder {
        TextView txtClearData;
        RelativeLayout rvClearDataUsage;

        public ClearDataHolder(View itemView) {
            super(itemView);
            txtClearData = itemView.findViewById(R.id.txtClearData);
            rvClearDataUsage = itemView.findViewById(R.id.rvClearDataUsage);
        }
    }
}
