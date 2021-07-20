package net.iGap.adapter.kuknos;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;

import net.iGap.G;
import net.iGap.R;
import net.iGap.kuknos.Model.Parsian.KuknosBalance;

import java.util.ArrayList;
import java.util.List;

public class WalletSpinnerArrayAdapter extends ArrayAdapter<KuknosBalance.Balance> {

    ArrayList<KuknosBalance.Balance> wallets;
    Context context;

    public WalletSpinnerArrayAdapter(Context context, List<KuknosBalance.Balance> objects) {
        super(context, 0, objects);
        if (wallets == null)
            wallets = new ArrayList<>();
        wallets.addAll(objects);
//        wallets.add(new AccountResponse.Balance("", "Add Asset", "", "", "","","",false,0));
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View layout = convertView;
        if (layout == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.fragment_kuknos_panel_spin_cell_dd, parent, false);
        }

        TextView walletName = layout.findViewById(R.id.fragKuknosPtextCell);
        ImageView walletPic = layout.findViewById(R.id.fragKuknosPimgCell);
        /*Picasso.get()
                .load("www.google.com")
                .placeholder(R.drawable.ic_tab_wallet_normal)
                .into(walletPic);*/

        if (position == (getCount() - 1) && position != 0 /*&& wallets.get(position).getAssetCode().equals("Add Asset")*/) {
            // set
            walletName.setText(context.getResources().getString(R.string.kuknos_panel_addAsset));
            Glide.with(G.context).load(R.mipmap.kuknos_add).into(walletPic);
            walletPic.setVisibility(View.VISIBLE);
            // config text
            walletName.setTypeface(ResourcesCompat.getFont(context, R.font.main_font), Typeface.BOLD);
            walletName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            // set style
            ConstraintLayout constraintLayout = layout.findViewById(R.id.fragKuknosPconstraint);
            constraintLayout.setBackgroundResource(R.drawable.kuknos_s_last_item_style);
            /*
            LinearLayout linearLayout = layout.findViewById(R.id.fragKuknosPLinear);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            float biasedValue = 0.5f;
            constraintSet.setHorizontalBias(linearLayout.getId(), biasedValue);
            constraintSet.applyTo(constraintLayout);*/
        } else {
            // config text
            walletName.setTypeface(ResourcesCompat.getFont(context, R.font.main_font), Typeface.NORMAL);
            walletName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            // set
            // set if asset code is -1 then it's no asset and we add no item
            if (wallets.get(position).getAssetCode() != null && wallets.get(position).getAssetCode().equals("-1"))
                walletName.setText(context.getResources().getString(R.string.no_item));
            else
                walletName.setText("" + (wallets.get(position).getAsset().getType().equals("native") ? "PMN" : wallets.get(position).getAssetCode()));
        }

        return layout;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View layout;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.fragment_kuknos_panel_spin_cell, parent, false);

        TextView walletName = layout.findViewById(R.id.fragKuknosPtextCell);
        ImageView walletPic = layout.findViewById(R.id.fragKuknosPimgCell);
        /*Picasso.get()
                .load("www.google.com")
                .placeholder(R.drawable.ic_tab_wallet_normal)
                .into(walletPic);*/

        if (position == (getCount() - 1) && position != 0 /*&& wallets.get(position).getAssetCode().equals("Add Asset")*/) {
            // set
            walletName.setText(context.getResources().getString(R.string.kuknos_panel_addAsset));
            Glide.with(G.context).load(R.mipmap.kuknos_add).into(walletPic);
            walletPic.setVisibility(View.VISIBLE);
            // config text
            walletName.setTypeface(ResourcesCompat.getFont(context, R.font.main_font), Typeface.BOLD);
            walletName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            walletName.setTextColor(context.getResources().getColor(R.color.white));
            // set style
            ConstraintLayout constraintLayout = layout.findViewById(R.id.fragKuknosPconstraint);
            constraintLayout.setBackgroundResource(R.drawable.kuknos_s_last_item_style);
            /*
            LinearLayout linearLayout = layout.findViewById(R.id.fragKuknosPLinear);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            float biasedValue = 0.5f;
            constraintSet.setHorizontalBias(linearLayout.getId(), biasedValue);
            constraintSet.applyTo(constraintLayout);*/
        } else {
            // config text
            walletName.setTypeface(ResourcesCompat.getFont(context, R.font.main_font), Typeface.NORMAL);
            walletName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            // set if asset code is -1 then it's no asset and we add no item
            if (wallets.get(position).getAssetCode() != null && wallets.get(position).getAssetCode().equals("-1"))
                walletName.setText(context.getResources().getString(R.string.no_item));
            else
                walletName.setText("" + (wallets.get(position).getAsset().getType().equals("native") ? "PMN" : wallets.get(position).getAssetCode()));
        }

        return layout;
    }

    @Override
    public int getCount() {
        if (wallets.get(0).getAssetCode() != null && wallets.get(0).getAssetCode().equals("-1"))
            return wallets.size();
        else
            return wallets.size() + 1;
    }

    @Override
    public KuknosBalance.Balance getItem(int position) {
        return wallets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
