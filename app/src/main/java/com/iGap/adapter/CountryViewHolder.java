package com.iGap.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iGap.R;

/**
 * Simple view holder for a single text view.
 */
class CountryViewHolder extends RecyclerView.ViewHolder {

    public TextView mTextView;
    public TextView mStatus;
    public ImageView img ;


    CountryViewHolder(View view) {
        super(view);

        img = (ImageView) view.findViewById(R.id.imageView);
        mTextView = (TextView) view.findViewById(R.id.text);
        mStatus = (TextView) view.findViewById(R.id.textView);

    }

    public void bindItem( String text, String text2) {
        try {
            mTextView.setText(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mStatus.setText(text2);
        } catch (Exception e) {
            e.printStackTrace();
        }

      /*  try {
            if(sli.equals("sli"))
                mLine.setVisibility(View.VISIBLE);
            else
                mLine.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
*/
    }

   /* @Override
    public String toString() {
        return mTextView.getText().toString();
    }*/


}
