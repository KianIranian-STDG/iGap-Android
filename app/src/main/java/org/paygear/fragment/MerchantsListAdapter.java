package org.paygear.fragment;


import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.databinding.MerchantsListItemBinding;
import net.iGap.module.Theme;

import org.paygear.RaadApp;
import org.paygear.model.SearchedAccount;

import java.util.ArrayList;

import ir.radsense.raadcore.utils.RaadCommonUtils;
import ir.radsense.raadcore.widget.CircleImageTransform;

public class MerchantsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Object> Data;
    private Context _Context;
    private String userID;
    private MerchantsListAdapter.ItemClickListener clickListener;


    public MerchantsListAdapter(Context c, ArrayList<SearchedAccount> Data, String userID) {
        this.Data = new ArrayList<>();
        this.Data.add(c.getResources().getString(R.string.personalAccountType));

        ArrayList<SearchedAccount> shops = new ArrayList<>();
        ArrayList<SearchedAccount> taxis = new ArrayList<>();

        for (SearchedAccount searchedAccount: Data) {
            if (searchedAccount.getAccount_type() != 4) {
                if (searchedAccount.getBusiness_type()==2){
                    taxis.add(searchedAccount);
                } else {
                    shops.add(searchedAccount);
                }
            } else {
                this.Data.add(searchedAccount);
            }
        }
        if (shops.size() > 0) {
            this.Data.add(c.getResources().getString(R.string.shopAccountType));
        }
        this.Data.addAll(shops);
        if (taxis.size() > 0) {
            this.Data.add(c.getResources().getString(R.string.taxiAccountType));
        }
        this.Data.addAll(taxis);
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            AppCompatTextView textView = new AppCompatTextView(parent.getContext());
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setPadding(ViewMaker.dpToPixel(15), ViewMaker.dpToPixel(10), ViewMaker.dpToPixel(15), ViewMaker.dpToPixel(10));
            textView.setBackgroundColor(new Theme().getRootColor(textView.getContext()));
            textView.setTypeface(ResourcesCompat.getFont(textView.getContext(), R.font.main_font));
            textView.setGravity(Gravity.START);

            return new ViewHolderTitle(textView);
        } else {
            return new MerchantsListAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.merchants_list_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (Data != null) {
            if (holder instanceof ViewHolderTitle) {
                ((ViewHolderTitle) holder).Load((String) this.Data.get(position), position);
            } else {
                ((MerchantsListAdapter.ViewHolder) holder).Load((SearchedAccount) Data.get(position), position);
            }
        }
    }

    @Override
    public int getItemCount() {
        return (Data == null) ? 0 : Data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (this.Data.get(position) instanceof String) {
            return 1;
        }
        return 2;
    }

    class ViewHolderTitle extends RecyclerView.ViewHolder {
        TextView title;
        public ViewHolderTitle(TextView view) {
            super(view);
            this.title = view;
        }

        void Load(String title, final int position) {
            this.title.setText(title);
        }
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
                    Picasso.with(G.context).load(RaadCommonUtils.getImageUrl(data.getProfile_picture()))
                            .transform(new CircleImageTransform())
                            .error(R.drawable.ic_local_taxi_black_24dp)
                            .placeholder(R.drawable.ic_local_taxi_black_24dp)
                            .fit()
                            .into(mBinding.image);
                }else {
                    Picasso.with(G.context).load(RaadCommonUtils.getImageUrl(data.getProfile_picture()))
                            .transform(new CircleImageTransform())
                            .error(R.drawable.ic_store_black_24dp)
                            .placeholder(R.drawable.ic_store_black_24dp)
                            .fit()
                            .into(mBinding.image);
                }
            } else {
                Picasso.with(G.context).load(RaadCommonUtils.getImageUrl(data.getProfile_picture()))
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