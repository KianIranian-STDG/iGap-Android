package com.iGap.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.module.CircleImageView;
import com.iGap.module.MyType;
import com.iGap.module.StructUserInfo;

import java.util.ArrayList;

/**
 * Created by android3 on 8/10/2016.
 */
public class AdapterDrawerMenu extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<StructUserInfo> list;
    private Context context;

    public AdapterDrawerMenu(ArrayList<StructUserInfo> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v;

        switch (viewType) {

            case MyType.registered:
                v = inflater.inflate(R.layout.drawer_sub_layout_register, parent, false);
                viewHolder = new viewHolderRegisterd(v);
                break;
            case MyType.notRegistered:
                v = inflater.inflate(R.layout.drawer_sub_layout_not_register, parent, false);
                viewHolder = new viewHolderNotRegisterd(v);
                break;
            case MyType.line:
                viewHolder = getViewLine();
                break;
        }


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {

            case MyType.registered:
                configureViewHolderRegister((viewHolderRegisterd) holder, position);
                break;
            case MyType.notRegistered:
                configureViewHolderNotRegister((viewHolderNotRegisterd) holder, position);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).contactState.getValue();
    }

    private RecyclerView.ViewHolder getViewLine() {
        int density = (int) context.getResources().getSystem().getDisplayMetrics().density;
        View v = new View(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2 * density);
        params.setMargins(0, (int) (context.getResources().getDimension(R.dimen.dp12)), 0, (int) (context.getResources().getDimension(R.dimen.dp20)));
        v.setLayoutParams(params);
        v.setBackgroundColor(context.getResources().getColor(R.color.green));
        return new RecyclerView.ViewHolder(v) {
        };

    }

    private void configureViewHolderRegister(viewHolderRegisterd holder, int position) {

        //holder.imvUserImage.setImageResource(R.mipmap.c);
    }

    private void configureViewHolderNotRegister(final viewHolderNotRegisterd holder, final int position) {

        //  holder.txtMessage.setText("how are you");

        holder.btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", " invite click");
            }
        });

    }


    class myHolder extends RecyclerView.ViewHolder {

        public myHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("fff", "onclic   " + myHolder.super.getPosition());

                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Log.e("fff", "onlongclic   " + myHolder.super.getPosition());
                    return true;
                }
            });
        }
    }


    class viewHolderRegisterd extends myHolder {

        public CircleImageView imvUserImage;
        public TextView txtName;
        public TextView txtLastSeen;

        public viewHolderRegisterd(View itemView) {
            super(itemView);

            imvUserImage = (CircleImageView) itemView.findViewById(R.id.dlsr_imv_user_picture);
            txtName = (TextView) itemView.findViewById(R.id.dlsr_txt_name);
            txtLastSeen = (TextView) itemView.findViewById(R.id.dlsr_txt_name);


            txtName.setTypeface(G.arialBold);
            txtLastSeen.setTypeface(G.arial);

        }
    }

    class viewHolderNotRegisterd extends myHolder {

        public TextView txtName;
        public Button btnInvite;

        public viewHolderNotRegisterd(View itemView) {
            super(itemView);

            txtName = (TextView) itemView.findViewById(R.id.dsln_txt_name);
            btnInvite = (Button) itemView.findViewById(R.id.dsln_btn_invite);

            txtName.setTypeface(G.arialBold);
            btnInvite.setTypeface(G.arialBold);
        }
    }


}
