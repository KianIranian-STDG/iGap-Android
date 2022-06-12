package net.iGap.adapter.electricity_bill;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.messenger.theme.Theme;
import net.iGap.model.electricity_bill.CompanyList;

import java.util.ArrayList;
import java.util.List;

public class ElectricityBillSearchCompanySpinnerAdapter extends BaseAdapter {

    List<CompanyList.Company> companies;
    Context context;

    public ElectricityBillSearchCompanySpinnerAdapter(Context context, List<CompanyList.Company> objects) {
        if (companies == null)
            companies = new ArrayList<>();
        companies.addAll(objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View layout = convertView;
        if (layout == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.fragment_elec_company_spin_cell, parent, false);
            layout.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        }
        TextView companyName = layout.findViewById(R.id.elecCompanyNameCell);
        companyName.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        if (position == 0)
            companyName.setText(context.getResources().getString(R.string.elecBill_search_billSpinner));
        else
            companyName.setText(companies.get(position-1).getTitle());

        return layout;
    }

    @Override
    public int getCount() {
        return companies.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return companies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return companies.get(position).getCode();
    }
}
