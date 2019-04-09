package net.iGap.adapter.items.discovery.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

import net.iGap.R;
import net.iGap.proto.ProtoGlobal;

public class Type1ViewHolder extends BaseViewHolder {
    private ImageView img0;
    private CardView card0;

    public Type1ViewHolder(@NonNull View itemView) {
        super(itemView);
        img0 = itemView.findViewById(R.id.type1_img0);
        card0 = itemView.findViewById(R.id.type1_card0);
    }

    @Override
    public void bindView(ProtoGlobal.Discovery item) {

//            Glide.with(context).load(dashboardModels.get(i).getImageList().get(1)).into(((ViewType2) viewHolder).imgDashboard2);*/
    }
}
