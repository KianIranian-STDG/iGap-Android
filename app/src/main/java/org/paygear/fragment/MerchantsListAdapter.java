package org.paygear.fragment;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import net.iGap.R;
import net.iGap.databinding.MerchantsListItemBinding;

import org.paygear.RaadApp;
import org.paygear.model.SearchedAccount;

import java.util.ArrayList;

import ir.radsense.raadcore.utils.RaadCommonUtils;
import ir.radsense.raadcore.widget.CircleImageTransform;

public class MerchantsListAdapter extends RecyclerView.Adapter<MerchantsListAdapter.ViewHolder> {
    private ArrayList<SearchedAccount> Data;
    private Context _Context;
    private String userID;
    private MerchantsListAdapter.ItemClickListener clickListener;


    public MerchantsListAdapter(Context c, ArrayList<SearchedAccount> Data, String userID) {
        this.Data = Data;
        _Context = c;
        this.userID = userID;
    }

    public interface ItemClickListener {

        void merchantItemClick(SearchedAccount data, int position);
    }

    public void setClickListener(MerchantsListAdapter.ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    @Override
    public MerchantsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.merchants_list_item, parent, false);
        return new MerchantsListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MerchantsListAdapter.ViewHolder holder, int position) {
        if (Data != null) {
            holder.Load(Data.get(position), position);
        }
    }

    @Override
    public int getItemCount() {
        return (Data == null) ? 0 : Data.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        MerchantsListItemBinding mBinding;

        ViewHolder(View itemView) {
            super(itemView);
            mBinding = MerchantsListItemBinding.bind(itemView);
        }

        void Load(final SearchedAccount data, final int position) {
            if (data.getName() != null && !data.getName().equals(""))
                mBinding.title.setText(data.getName());
            else {
                mBinding.title.setText(data.getUsername());
            }
            if (data.getUsers() != null) {
                boolean isAdmin = false;
                boolean isFinance = false;
                for (SearchedAccount.UsersBean item : data.getUsers()) {

                    if (item.getUser_id().equals(userID)) {
                        if (item.getRole().equals("admin")) {
                            isAdmin = true;
                        }
                        if (item.getRole().equals("finance")) {
                            isFinance = true;
                        }

                    }
                }
                if (isAdmin) {
                    mBinding.subtitle.setText(R.string.admin);
                } else if (isFinance) {
                    mBinding.subtitle.setText(R.string.cashier);
                } else {
                    mBinding.subtitle.setText(R.string.paygear_user);
                }
            } else {
                mBinding.subtitle.setText(R.string.paygear_user);
            }
            if (data.getAccount_type() != 4) {
                if (data.getBusiness_type()==2){
                    Picasso.get().load(RaadCommonUtils.getImageUrl(data.getProfile_picture()))
                            .transform(new CircleImageTransform())
                            .error(R.drawable.ic_local_taxi_black_24dp)
                            .placeholder(R.drawable.ic_local_taxi_black_24dp)
                            .fit()
                            .into(mBinding.image);
                }else {
                    Picasso.get().load(RaadCommonUtils.getImageUrl(data.getProfile_picture()))
                            .transform(new CircleImageTransform())
                            .error(R.drawable.ic_store_black_24dp)
                            .placeholder(R.drawable.ic_store_black_24dp)
                            .fit()
                            .into(mBinding.image);
                }
            } else {
                Picasso.get().load(RaadCommonUtils.getImageUrl(data.getProfile_picture()))
                        .transform(new CircleImageTransform())
                        .error(R.drawable.ic_person_outline2_white_24dp)
                        .placeholder(R.drawable.ic_person_outline2_white_24dp)
                        .fit()
                        .into(mBinding.image);
            }

            mBinding.wholeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.merchantItemClick(data, position);
                    }
                }
            });


            if (RaadApp.selectedMerchant!=null){
                if (RaadApp.selectedMerchant.get_id().equals(data.get_id())){
                    mBinding.title.setTextColor(Color.parseColor("#2196f3"));
                    mBinding.check.setVisibility(View.VISIBLE);
                    mBinding.checkFrame.setVisibility(View.VISIBLE);

                }else {
                    mBinding.title.setTextColor(Color.parseColor("#de000000"));
                    mBinding.check.setVisibility(View.GONE);
                    mBinding.checkFrame.setVisibility(View.GONE);

                }

            }else {
                if (position==0){
                    mBinding.title.setTextColor(Color.parseColor("#2196f3"));
                    mBinding.check.setVisibility(View.VISIBLE);
                    mBinding.checkFrame.setVisibility(View.VISIBLE);

                }else {
                    mBinding.title.setTextColor(Color.parseColor("#de000000"));
                    mBinding.check.setVisibility(View.GONE);
                    mBinding.checkFrame.setVisibility(View.GONE);


                }
            }
        }
    }
}