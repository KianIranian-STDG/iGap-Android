package net.iGap.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.proto.ProtoGlobal;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdapterDrBot extends RecyclerView.Adapter<AdapterDrBot.ViewHolder> {


    public interface OnHandleDrBot {

        void goToRoomBot(ProtoGlobal.Favorite favorite);

        void sendMessageBOt(ProtoGlobal.Favorite favorite);

    }

    private List<ProtoGlobal.Favorite> itemDrBots;
    private OnHandleDrBot onHandleDrBot;

    public AdapterDrBot(List<ProtoGlobal.Favorite> itemDrBots, OnHandleDrBot onHandleDrBot) {
        this.itemDrBots = itemDrBots;
        this.onHandleDrBot = onHandleDrBot;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_rcv_dr_bot, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        ProtoGlobal.Favorite itemDrBot = itemDrBots.get(i);

        if (!itemDrBot.getImage().equals("")) {
            byte[] theByteArray = Base64.decode(itemDrBot.getImage(), Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(theByteArray, 0, theByteArray.length);
            holder.icon.setImageBitmap(bmp);

        } else {
            holder.icon.setVisibility(View.GONE);
        }
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(75);
        String bgColor = "#" + itemDrBot.getBgcolor();
        if (checkColor(bgColor)) {
            gd.setColor(Color.parseColor(bgColor));
        } else {
            gd.setColor(G.context.getResources().getColor(R.color.colorOldBlack));
        }

        holder.itemView.setBackgroundDrawable(gd);
        holder.title.setText(itemDrBot.getName());

        String txtColor = "#" + itemDrBot.getTextcolor();
        if (checkColor(txtColor)) {
            holder.title.setTextColor(Color.parseColor(txtColor));
        } else {
            holder.title.setTextColor(G.context.getResources().getColor(R.color.white));
        }
    }

    private boolean checkColor(String color) {
        Pattern mPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");

        Matcher matcher = mPattern.matcher(color);

        return matcher.matches();
    }

    @Override
    public int getItemCount() {
        return itemDrBots.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView icon;
        private TextView title;
        private RippleView rippleView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.imgDrBot);
            title = itemView.findViewById(R.id.txtTitleDrBot);
            title = itemView.findViewById(R.id.txtTitleDrBot);
            rippleView = itemView.findViewById(R.id.rippleBot);
            rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) throws IOException {

                    ProtoGlobal.Favorite item = itemDrBots.get(getAdapterPosition());

                    if (onHandleDrBot != null) {
                        if (item.getValue().startsWith("@")) {
                            onHandleDrBot.goToRoomBot(item);
                        } else {
                            onHandleDrBot.sendMessageBOt(item);
                        }
                    }

                }
            });
        }
    }

}
