package net.iGap.fragments.mplTranaction;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.proto.ProtoGlobal;

import java.util.ArrayList;
import java.util.List;

public class MplTransActionAdapter extends RecyclerView.Adapter<MplTransActionAdapter.MplTransActionViewHolder> {

    private List<ProtoGlobal.MplTransaction> mplTransactions = new ArrayList<>();
    private OnMplTransActionAdapterCallBack callBack;

    public void setTransAction(List<ProtoGlobal.MplTransaction> mplTransactions) {
        this.mplTransactions = mplTransactions;
        notifyDataSetChanged();
    }

    public void addTransAction(List<ProtoGlobal.MplTransaction> mplTransactions) {
        this.mplTransactions.addAll(mplTransactions);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public MplTransActionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_mpl_transactin_list, viewGroup, false);
        return new MplTransActionViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull MplTransActionViewHolder mplTransActionViewHolder, int i) {
        mplTransActionViewHolder.bindTransaction(mplTransactions.get(i));
    }

    @Override
    public int getItemCount() {
        return mplTransactions.size();
    }

    public void setCallBack(OnMplTransActionAdapterCallBack callBack) {
        this.callBack = callBack;
    }

    @FunctionalInterface
    public interface OnMplTransActionAdapterCallBack {
        void onClick(String token);
    }

    public class MplTransActionViewHolder extends RecyclerView.ViewHolder {
        private TextView transferActionTypeTv;
        private ImageView transferActionTypeIv;
        private TextView transferActionTimeTv;
        private TextView transferActionOrderIdTv;

        public MplTransActionViewHolder(@NonNull View rootView) {
            super(rootView);
            transferActionTypeTv = rootView.findViewById(R.id.tv_itemMplTransAction_type);
            transferActionTypeIv = rootView.findViewById(R.id.iv_itemMplTransAction_type);
            transferActionTimeTv = rootView.findViewById(R.id.tv_itemMplTransAction_time);
            transferActionOrderIdTv = rootView.findViewById(R.id.tv_itemMplTransAction_orderId);
        }

        public void bindTransaction(ProtoGlobal.MplTransaction mplTransaction) {
            switch (mplTransaction.getType()) {
                case BILL:
                    transferActionTypeTv.setText("Bill");
                    break;
                case NONE:
                    transferActionTypeTv.setText("Non");
                    break;
                case SALES:
                    transferActionTypeTv.setText("sales");
                    break;
                case TOPUP:
                    transferActionTypeTv.setText("topup");
                    break;
                case CARD_TO_CARD:
                    transferActionTypeTv.setText("Card to Card");
                    break;
                case UNRECOGNIZED:
                    transferActionTypeTv.setText("UNRECOGNIZED");
                    break;
            }

            transferActionTimeTv.setText(HelperCalander.getTimeForMainRoom(mplTransaction.getPayTime()));
            transferActionOrderIdTv.setText(String.valueOf(mplTransaction.getOrderId()));

            itemView.setOnClickListener(v -> {
                if (callBack != null)
                    callBack.onClick(mplTransaction.getToken());
            });

        }


    }
}
