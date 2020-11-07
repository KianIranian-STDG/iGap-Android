package net.iGap.adapter.kuknos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.kuknos.Model.Parsian.KuknosBalance;

import java.util.ArrayList;
import java.util.List;

public class WalletHistorySpinnerAdapter extends BaseAdapter {

    ArrayList<KuknosBalance.Balance> wallets;
    Context context;

    public WalletHistorySpinnerAdapter(Context context, List<KuknosBalance.Balance> objects) {
        if (wallets == null)
            wallets = new ArrayList<>();
        wallets.addAll(objects);
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
        walletName.setText("" + (wallets.get(position).getAsset().getType().equals("native") ? "PMN" : wallets.get(position).getAssetCode()));

        return layout;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View layout = convertView;

        if (layout == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.fragment_kuknos_panel_spin_cell, parent, false);
        }

        TextView walletName = layout.findViewById(R.id.fragKuknosPtextCell);
        walletName.setText("" + (wallets.get(position).getAsset().getType().equals("native") ? "PMN" : wallets.get(position).getAssetCode()));

        return layout;
    }

    @Override
    public int getCount() {
        return wallets.size();
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
