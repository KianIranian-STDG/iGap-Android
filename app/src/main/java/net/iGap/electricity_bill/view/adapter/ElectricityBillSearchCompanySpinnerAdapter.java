package net.iGap.electricity_bill.view.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.squareup.picasso.Picasso;

import net.iGap.R;
import net.iGap.electricity_bill.repository.model.CompanyList;

import org.stellar.sdk.responses.AccountResponse;

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
        }
        TextView companyName = layout.findViewById(R.id.elecCompanyNameCell);

        if (position == 0)
            companyName.setText(context.getResources().getString(R.string.elecBill_search_billSpinner));
        companyName.setText(companies.get(position).getTitle());

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
