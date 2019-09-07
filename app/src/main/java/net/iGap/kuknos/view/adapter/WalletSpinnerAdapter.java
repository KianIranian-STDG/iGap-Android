package net.iGap.kuknos.view.adapter;

import android.content.Context;
import android.graphics.Typeface;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.iGap.R;

import org.stellar.sdk.responses.AccountResponse;

import java.util.ArrayList;
import java.util.List;

public class WalletSpinnerAdapter extends BaseAdapter {

    ArrayList<AccountResponse.Balance> wallets;
    Context context;

    public WalletSpinnerAdapter(Context context, List<AccountResponse.Balance> objects) {
        if (wallets == null)
            wallets = new ArrayList<>();
        wallets.addAll(objects);
//        wallets.add(new AccountResponse.Balance("", "Add Asset", "", "", "","","",false,0));
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View layout = convertView;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.fragment_kuknos_panel_spin_cell, parent, false);

        TextView walletName = layout.findViewById(R.id.fragKuknosPtextCell);
        ImageView walletPic = layout.findViewById(R.id.fragKuknosPimgCell);
        /*Picasso.get()
                .load("www.google.com")
                .placeholder(R.drawable.ic_tab_wallet_normal)
                .into(walletPic);*/

        if (position == (getCount()-1) && position != 0 /*&& wallets.get(position).getAssetCode().equals("Add Asset")*/) {
            // set
            walletName.setText(context.getResources().getString(R.string.kuknos_panel_addAsset));
            Picasso.get().load(R.mipmap.kuknos_add).into(walletPic);
            walletPic.setVisibility(View.VISIBLE);
            // config text
            walletName.setTypeface(null, Typeface.BOLD);
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
            walletName.setTypeface(null, Typeface.NORMAL);
            walletName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            // set
            walletName.setText("" + (wallets.get(position).getAsset().getType().equals("native") ? "PMN" : wallets.get(position).getAssetCode()));
        }

        return layout;
    }

    @Override
    public int getCount() {
        return wallets.size()+1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
