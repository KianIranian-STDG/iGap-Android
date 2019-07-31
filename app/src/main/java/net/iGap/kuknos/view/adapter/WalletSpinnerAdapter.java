package net.iGap.kuknos.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.iGap.R;
import net.iGap.kuknos.service.model.KuknosWalletBalanceInfoM;

import java.util.ArrayList;
import java.util.List;

public class WalletSpinnerAdapter extends BaseAdapter {

    ArrayList<KuknosWalletBalanceInfoM> wallets;
    Context context;

    public WalletSpinnerAdapter(Context context, List<KuknosWalletBalanceInfoM> objects) {
        if (wallets == null)
            wallets = new ArrayList<>();
        wallets.addAll(objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View layout = convertView;


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.fragment_kuknos_panel_spin_cell, parent, false);


        TextView walletName = layout.findViewById(R.id.fragKuknosPtextCell);
        walletName.setText("" + wallets.get(position).getAssetCode());

        ImageView walletPic = layout.findViewById(R.id.fragKuknosPimgCell);
        Picasso.get()
                .load(wallets.get(position).getAssetPicURL())
                .placeholder(R.drawable.ic_tab_wallet_normal)
                .into(walletPic);

        if (position == (wallets.size()-1) && position != 0) {
            Picasso.get().load(R.mipmap.kuknos_add).into(walletPic);
            walletName.setTypeface(null, Typeface.BOLD);
            walletName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);

            ConstraintLayout constraintLayout = layout.findViewById(R.id.fragKuknosPconstraint);
            LinearLayout linearLayout = layout.findViewById(R.id.fragKuknosPLinear);

            constraintLayout.setBackgroundResource(R.drawable.kuknos_s_last_item_style);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            float biasedValue = 0.5f;
            constraintSet.setHorizontalBias(linearLayout.getId(), biasedValue);
            constraintSet.applyTo(constraintLayout);
        } else {
            walletName.setTypeface(null, Typeface.NORMAL);
            walletName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        }

        return layout;
    }

    @Override
    public int getCount() {
        return wallets.size();
    }

    @Override
    public Object getItem(int position) {
        return wallets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
