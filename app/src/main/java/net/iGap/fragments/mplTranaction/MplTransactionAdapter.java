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
import net.iGap.module.FontIconTextView;
import net.iGap.proto.ProtoGlobal;

import java.util.ArrayList;
import java.util.List;

public class MplTransactionAdapter extends RecyclerView.Adapter<MplTransactionAdapter.MplTransactionViewHolder> {

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
    public MplTransactionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_mpl_transactin_list, viewGroup, false);
        return new MplTransactionViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull MplTransactionViewHolder mplTransActionViewHolder, int i) {
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

    public class MplTransactionViewHolder extends RecyclerView.ViewHolder {
        private TextView transferActionTypeTv;
        private FontIconTextView transferActionTypeIv;
        private TextView transferActionTimeTv;
        private TextView transferActionOrderIdTv;

        public MplTransactionViewHolder(@NonNull View rootView) {
            super(rootView);
            transferActionTypeTv = rootView.findViewById(R.id.tv_itemMplTransAction_type);
            transferActionTypeIv = rootView.findViewById(R.id.tv_itemMplTransAction_typeIcon);
            transferActionTimeTv = rootView.findViewById(R.id.tv_itemMplTransAction_time);
            transferActionOrderIdTv = rootView.findViewById(R.id.tv_itemMplTransAction_orderId);
        }

        public void bindTransaction(ProtoGlobal.MplTransaction mplTransaction) {
            switch (mplTransaction.getType()) {
                case BILL:
                    transferActionTypeTv.setText("Bill");
                    transferActionTypeIv.setText(itemView.getContext().getResources().getString(R.string.iconCardToCard));
                    break;
                case NONE:
                    transferActionTypeTv.setText("Non");
                    transferActionTypeIv.setText(itemView.getContext().getResources().getString(R.string.iconCardToCard));
                    break;
                case SALES:
                    transferActionTypeTv.setText("sales");
                    transferActionTypeIv.setText(itemView.getContext().getResources().getString(R.string.iconCardToCard));
                    break;
                case TOPUP:
                    transferActionTypeTv.setText("topup");
                    transferActionTypeIv.setText(itemView.getContext().getResources().getString(R.string.iconCardToCard));
                    break;
                case CARD_TO_CARD:
                    transferActionTypeTv.setText("Card to Card");
                    transferActionTypeIv.setText(itemView.getContext().getResources().getString(R.string.iconCardToCard));
                    break;
                case UNRECOGNIZED:
                    transferActionTypeTv.setText("UNRECOGNIZED");
                    transferActionTypeIv.setText(itemView.getContext().getResources().getString(R.string.iconCardToCard));
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
