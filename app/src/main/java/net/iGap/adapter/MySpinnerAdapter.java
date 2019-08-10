package net.iGap.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class MySpinnerAdapter extends ArrayAdapter<CharSequence> {

    public MySpinnerAdapter(Context context, int resource, List<CharSequence> items) {
        super(context, resource, items);
    }

    // Affects default (closed) state of the spinner
    /*@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setTypeface(G.typeface_IRANSansMobile);

        if (position == 0) {
            view.setTextColor(G.context.getResources().getColor(R.color.gray));
        }

        return view;
    }*/

    /*// Affects opened state of the spinner
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
        view.setTypeface(G.typeface_IRANSansMobile);
        view.setTextColor(G.context.getResources().getColor(R.color.gray_4c));
        return view;
    }*/
}
