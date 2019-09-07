package net.iGap.adapter;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.R;
import net.iGap.interfaces.DataUsageListener;
import net.iGap.module.AndroidUtils;
import net.iGap.module.structs.DataUsageStruct;

import java.util.ArrayList;

import static net.iGap.libs.bottomNavigation.Util.Utils.darkModeHandler;
import static net.iGap.libs.bottomNavigation.Util.Utils.darkModeHandlerGray;

public class DataUsageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<DataUsageStruct> dataList;
    private long totalReceive;
    private long totalSend;
    private boolean connectivityType;
    private DataUsageListener clearData;

    public DataUsageAdapter(Context context, ArrayList<DataUsageStruct> dataList, long totalReceive, long totalSend, boolean connectivityType, DataUsageListener clearData) {
        this.context = context;
        this.dataList = dataList;
        this.totalReceive = totalReceive;
        this.totalSend = totalSend;
        this.connectivityType = connectivityType;
        this.clearData = clearData;

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
            ((BaseHolder) holder).txtByteReceivedNum.setText(AndroidUtils.humanReadableByteCount(dataList.get(position).getByteReceived(), true));
            ((BaseHolder) holder).txtByteSentNum.setText(AndroidUtils.humanReadableByteCount(dataList.get(position).getByteSend(), true));


            switch (dataList.get(position).getTitle()) {
                case "IMAGE":
                    ((BaseHolder) holder).txtTitle.setText(context.getResources().getString(R.string.image_message));
                    break;
                case "VIDEO":
                    ((BaseHolder) holder).txtTitle.setText(context.getResources().getString(R.string.video_message));

                    break;

                case "FILE":
                    ((BaseHolder) holder).txtTitle.setText(context.getResources().getString(R.string.file_message));

                    break;
                case "AUDIO":
                    ((BaseHolder) holder).txtTitle.setText(context.getResources().getString(R.string.audio_message));
                    break;

                case "UNRECOGNIZED":
                    ((BaseHolder) holder).txtTitle.setText(context.getResources().getString(R.string.st_Other));
                    break;

            }
            //    ((BaseHolder) holder).txtTitle.setText(dataList.get(position).getTitle());
    /*        if (HelperCalander.isPersianUnicode) {
            *//*    holder.txtLastMessage.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txtLastMessage.getText().toString()));
                holder.txtUnread.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txtUnread.getText().toString()));*//*

                ((BaseHolder) holder).txtSentNum.setText(HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(dataList.get(position).getSendNum())));
                ((BaseHolder) holder).txtReceivedNum.setText(HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(dataList.get(position).getReceivednum())));
            }
*/
            ((BaseHolder) holder).txtSentNum.setText(String.valueOf(dataList.get(position).getSendNum()));

            ((BaseHolder) holder).txtReceivedNum.setText(String.valueOf(dataList.get(position).getReceivednum()));

        } else if (holder instanceof TotalViewHolder) {

            //  ((TotalViewHolder)holder).txtTotalReceivedByte.setText(String.valueOf(totalReceive));
            ((TotalViewHolder) holder).txtTotalReceivedByte.setText(AndroidUtils.humanReadableByteCount(totalReceive, true));
            //  ((TotalViewHolder)holder).txtTotalSentByte.setText(String.valueOf(totalSend));
            ((TotalViewHolder) holder).txtTotalSentByte.setText(AndroidUtils.humanReadableByteCount(totalSend, true));

        } else if (holder instanceof ClearDataHolder) {
            ((ClearDataHolder) holder).txtClearData.setText(context.getResources().getString(R.string.clear_data_usage));
            ((ClearDataHolder) holder).rvClearDataUsage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new MaterialDialog.Builder(context).title(R.string.clearDataUsage)
                            //  .content(String.format(context.getString(R.string.pin_messages_content), context.getString(R.string.unpin)))

                            .positiveText(R.string.yes)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    clearData.doClearDB(connectivityType);
                                    dialog.dismiss();
                                }
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
            setTypeFace(txtByteReceivedNum);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            setTypeFace(txtTitle);
            darkModeHandler(txtTitle);

            txtSentNum = itemView.findViewById(R.id.txtSentNum);
            setTypeFace(txtSentNum);

            txtReceivedNum = itemView.findViewById(R.id.txtReceivedNum);
            setTypeFace(txtReceivedNum);

            txtByteSentNum = itemView.findViewById(R.id.txtByteSentNum);
            setTypeFace(txtByteSentNum);

            txtByteReceived = itemView.findViewById(R.id.txtByteReceived);
            txtByteReceived.setText(context.getResources().getString(R.string.bytes_received));
            setTypeFace(txtByteReceived);
            darkModeHandlerGray(txtByteReceived);

            txtByteSent = itemView.findViewById(R.id.txtByteSent);
            txtByteSent.setText(context.getResources().getString(R.string.bytes_sent));
            setTypeFace(txtByteSent);
            darkModeHandlerGray(txtByteSent);

            txtReceived = itemView.findViewById(R.id.txtReceived);
            txtReceived.setText(context.getResources().getString(R.string.received));
            setTypeFace(txtReceived);
            darkModeHandlerGray(txtReceived);

            txtSent = itemView.findViewById(R.id.txtSent);
            txtSent.setText(context.getResources().getString(R.string.sent));
            setTypeFace(txtSent);
            darkModeHandlerGray(txtSent);

            View line = itemView.findViewById(R.id.line);
            darkModeHandlerGray(line);
        }
    }

    public class TotalViewHolder extends RecyclerView.ViewHolder {
        TextView txtTotalSentByte, txtTotalReceivedByte, txtTotalReceived, txtTotalSent, txtTitle;

        public TotalViewHolder(View itemView) {
            super(itemView);
            txtTotalSentByte = itemView.findViewById(R.id.txtTotalSentByte);
            setTypeFace(txtTotalSentByte);

            txtTotalReceivedByte = itemView.findViewById(R.id.txtTotalReceivedByte);
            setTypeFace(txtTotalReceivedByte);

            txtTotalReceived = itemView.findViewById(R.id.txtTotalReceived);
            txtTotalReceived.setText(context.getResources().getString(R.string.total_received));
            setTypeFace(txtTotalReceived);
            darkModeHandlerGray(txtTotalReceived);

            txtTotalSent = itemView.findViewById(R.id.txtTotalSent);
            txtTotalSent.setText(context.getResources().getString(R.string.total_sent));
            setTypeFace(txtTotalSent);
            darkModeHandlerGray(txtTotalSent);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtTitle.setText(context.getResources().getString(R.string.total));
            darkModeHandler(txtTitle);
            setTypeFace(txtTitle);

            View line = itemView.findViewById(R.id.line);
            darkModeHandlerGray(line);
        }
    }

    public class ClearDataHolder extends RecyclerView.ViewHolder {
        TextView txtClearData;
        RelativeLayout rvClearDataUsage;

        public ClearDataHolder(View itemView) {
            super(itemView);
            txtClearData = itemView.findViewById(R.id.txtClearData);
            setTypeFace(txtClearData);
            darkModeHandler(txtClearData);

            rvClearDataUsage = itemView.findViewById(R.id.rvClearDataUsage);
        }
    }

    private void setTypeFace(TextView textView){
        textView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/IRANSansMobile.ttf"));
    }
}
