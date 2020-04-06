package org.paygear.fragment;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.cachapa.expandablelayout.ExpandableLayout;
import net.iGap.G;
import net.iGap.R;
import net.iGap.module.Theme;
import net.iGap.databinding.FragmentCardsBinding;
import net.iGap.databinding.OtpDialogBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.HelperTracker;
import net.iGap.observers.interfaces.ToolbarListener;

import org.paygear.RaadApp;
import org.paygear.RefreshLayout;
import org.paygear.WalletActivity;
import org.paygear.model.Card;
import org.paygear.model.MerchantsResult;
import org.paygear.model.Payment;
import org.paygear.model.PaymentAuth;
import org.paygear.model.SearchedAccount;
import org.paygear.utils.SettingHelper;
import org.paygear.web.Web;
import org.paygear.widget.BankCardView;

import java.util.ArrayList;

import ir.radsense.raadcore.OnFragmentInteraction;
import ir.radsense.raadcore.app.NavigationBarActivity;
import ir.radsense.raadcore.model.Account;
import ir.radsense.raadcore.model.Auth;
import ir.radsense.raadcore.utils.RaadCommonUtils;
import ir.radsense.raadcore.utils.Typefaces;
import ir.radsense.raadcore.widget.ProgressLayout;
import ir.radsense.raadcore.widget.RecyclerRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CardsFragment extends Fragment implements ToolbarListener , OnFragmentInteraction, RefreshLayout, MerchantsListAdapter.ItemClickListener {

    private static final int COLLAPSE = 60;

    //private RaadToolBar appBar;
    private ImageView appBarImage;
    private TextView appBarTitle;
    private RecyclerRefreshLayout mRefreshLayout;
    private ProgressLayout progress;
    private RecyclerView merchantsRecycler;
    private ExpandableLayout expandableLayout;
    private MerchantsListAdapter merchantsListAdapter;
    LinearLayout cardsLayout;
    ScrollView scrollView;
    ArrayList<CardView> viewItems;
    private ArrayList<SearchedAccount> merchantsList = new ArrayList<>();
    SearchedAccount selectedMerchant;

    private ArrayList<Card> mCards;
    private ArrayList<Card> merchantCards;

    private Payment mPayment;
    OtpDialogBinding otpDialogBinding;
    Dialog otpDialog;

    FragmentCardsBinding mBinding;
    private HelperToolbar mHelperToolbar;

    boolean checkAfterGift=false;
    String qrDataAccountId;

    public CardsFragment() {
    }

    public static CardsFragment newInstance(Payment payment) {
        CardsFragment fragment = new CardsFragment();
        Bundle args = new Bundle();
        args.putSerializable("Payment", payment);
        fragment.setArguments(args);
        return fragment;
    }

    public static CardsFragment newInstance(boolean checkAfterGift,String qrDataAccountId) {
        CardsFragment fragment = new CardsFragment();
        Bundle args = new Bundle();
        args.putSerializable("CheckAfterGift", checkAfterGift);
        args.putSerializable("QrDataAccountId", qrDataAccountId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPayment = (Payment) getArguments().getSerializable("Payment");
            checkAfterGift =  getArguments().getBoolean("CheckAfterGift");
            qrDataAccountId = getArguments().getString("QrDataAccountId");
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (WalletActivity.refreshLayout != null)
            WalletActivity.refreshLayout = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cards, container, false);
        mBinding = FragmentCardsBinding.bind(view);
        WalletActivity.refreshLayout = this;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HelperTracker.sendTracker(HelperTracker.TRACKER_WALLET_PAGE);
        RaadApp.selectedMerchant = null;

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLogoShown(true)
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.setting_icon , R.string.group_icon)
                .setListener(this)
                .setDefaultTitle(getString(R.string.wallet));


        mBinding.toolbarLayout.addView(mHelperToolbar.getView());

        if (mPayment != null) {
            mHelperToolbar.setDefaultTitle(getString(R.string.select_card));
        } else {
            mHelperToolbar.setDefaultTitle(getString(R.string.wallet));
           // mHelperToolbar.getLeftButton().setVisibility(View.INVISIBLE);
        }
        mRefreshLayout = view.findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (selectedMerchant == null) {
                    loadCards();
                } else {
                    ShowMerchantView(selectedMerchant);
                }
            }
        });

        viewItems = new ArrayList<>();
        scrollView = view.findViewById(R.id.scroll_view);
        cardsLayout = view.findViewById(R.id.cards);
        progress = view.findViewById(R.id.progress);
        progress.setOnRetryButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load();
            }
        });


        expandableLayout = view.findViewById(R.id.merchants_expandable_layout);
        merchantsRecycler = view.findViewById(R.id.merchants_recycler);
        merchantsList = RaadApp.merchants;
        mHelperToolbar.getSecondRightButton().setVisibility(View.GONE);
        if (merchantsList != null) {
            if (merchantsList.size() > 0) {
                setMerchantsAdapter();
            }
        } else {
            GetMerchantsList();
        }


        updateAppBar();
        mCards = RaadApp.cards;

        if(checkAfterGift){
            mCards = null;
        }

        if (mCards != null) {

            setAdapter();
        } else if (Auth.getCurrentAuth() != null) {
            load();
        }



    }

    private void setMerchantsAdapter() {
        if (merchantsList != null && merchantsList.size() > 0 && RaadApp.me != null) {
            if (merchantsList.get(0).get_id() != null)
                if (!merchantsList.get(0).get_id().equals(RaadApp.me.id)) {
                    SearchedAccount searchedAccount = new SearchedAccount();
                    searchedAccount.setAccount_type(4);
                    searchedAccount.set_id(RaadApp.me.id);
                    searchedAccount.setUsername(RaadApp.me.username);
                    searchedAccount.setName(RaadApp.me.name);
                    searchedAccount.setProfile_picture(RaadApp.me.profilePicture);
                    merchantsList.add(0, searchedAccount);
                }
        }

        if (merchantsList != null && merchantsList.size() > 0){
            mHelperToolbar.getSecondRightButton().setVisibility(View.VISIBLE);
        } else {
            mHelperToolbar.getSecondRightButton().setVisibility(View.GONE);
        }

        merchantsListAdapter = new MerchantsListAdapter(getContext(), merchantsList, Auth.getCurrentAuth().getId());
        merchantsListAdapter.setClickListener(CardsFragment.this);
        merchantsRecycler.setAdapter(merchantsListAdapter);
        merchantsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        merchantsListAdapter.notifyDataSetChanged();
        updateAppBar();

    }


    @Override
    public void onFragmentResult(Fragment fragment, Bundle bundle) {
        if (fragment instanceof AddCardFragment ||
                fragment instanceof CardFragment ||
                fragment instanceof CashOutRequestFragment) {
            load();
        }
    }

    private void load() {

        if (Auth.getCurrentAuth() != null && Auth.getCurrentAuth().getPublicKey() == null) {
            loadKey();
        } else {
            loadCards();
        }
    }

    private void loadKey() {

        if (mCards == null || mCards.size() == 0) {
            if (!mRefreshLayout.isRefreshing())
                progress.setStatus(0);
        } else {
            if (!mRefreshLayout.isRefreshing())
                mRefreshLayout.setRefreshing(true);
        }

        Web.getInstance().getWebService().getPaymentKey().enqueue(new Callback<PaymentAuth>() {
            @Override
            public void onResponse(Call<PaymentAuth> call, Response<PaymentAuth> response) {
                Boolean success = Web.checkResponse(CardsFragment.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    PaymentAuth auth = response.body();
                    if (auth != null) {
                        Auth.getCurrentAuth().setPublicKey(auth.publicKey);
                        loadCards();

                        return;
                    }
                }

                if (mCards == null || mCards.size() == 0)
                    progress.setStatus(-1, getString(R.string.error));
                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<PaymentAuth> call, Throwable t) {
                if (Web.checkFailureResponse(CardsFragment.this, call, t)) {
                    if (mCards == null || mCards.size() == 0)
                        progress.setStatus(-1, getString(R.string.network_error));
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void loadCards() {

        if (RaadApp.me == null)
            loadMyAccount();

        if (mCards == null || mCards.size() == 0) {
            if (!mRefreshLayout.isRefreshing())
                progress.setStatus(0);
        } else {
            if (!mRefreshLayout.isRefreshing())
                mRefreshLayout.setRefreshing(true);
        }

        String orderId = mPayment != null ? mPayment.orderId : null;
        Web.getInstance().getWebService().getCards(orderId, false, true).enqueue(new Callback<ArrayList<Card>>() {
            @Override
            public void onResponse(Call<ArrayList<Card>> call, Response<ArrayList<Card>> response) {
                Boolean success = Web.checkResponse(CardsFragment.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    mCards = RaadApp.cards = response.body();
                    if (mCards != null) {
                        int c = mCards.size();
                        int raadCardIndex = -1;
                        int defaultCardIndex = -1;
                        for (int i = 0; i < c; i++) {
                            if (mCards.get(i).isRaadCard())
                                raadCardIndex = i;
                            if (mCards.get(i).isDefault)
                                defaultCardIndex = i;
                        }

                        if (raadCardIndex > -1) {
                            /*if (mPayment != null && mPayment.orderType == Order.ORDER_TYPE_CHARGE_CREDIT) {
                                mCards.remove(raadCardIndex);
                            } else {
                                Card raadCard = mCards.get(raadCardIndex);
                                mCards.remove(raadCardIndex);
                                mCards.add(0, raadCard);
                            }*/

                            Card raadCard = mCards.get(raadCardIndex);
                            mCards.remove(raadCardIndex);
                            mCards.add(0, raadCard);
                        }

                        if (defaultCardIndex > -1) {
                            Card defaultCard = mCards.get(defaultCardIndex);
                            mCards.remove(defaultCardIndex);
                            mCards.add((raadCardIndex > -1 && c > 1) ? 1 : 0, defaultCard);
                        }
                    }

                    //collapsedItem = -1;
                    if(checkAfterGift){
                        setAdapter();
                        checkAfterGift = false;
                        for (int i = 0; i < mCards.size(); i++) {
                            if(qrDataAccountId.equals(mCards.get(i).clubId)){
                                if(getActivity() != null)
                                    ((NavigationBarActivity) getActivity()).pushFullFragment(
                                            CardFragment.newInstance(mPayment, mCards.get(i)), "CardFragment");

                            }
                        }

                    }else {
                        setAdapter();
                    }
                } else {
                    if (mCards == null || mCards.size() == 0)
                        progress.setStatus(-1, getString(R.string.error));
                }
                GetMerchantsList();
                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ArrayList<Card>> call, Throwable t) {
                if (Web.checkFailureResponse(CardsFragment.this, call, t)) {
                    if (mCards == null || mCards.size() == 0)
                        progress.setStatus(-1, getString(R.string.network_error));
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });

    }

    private void GetMerchantsList() {
        Web.getInstance().getWebService().searchAccounts(200, 1, "admin", "finance").enqueue(new Callback<MerchantsResult>() {
            @Override
            public void onResponse(Call<MerchantsResult> call, Response<MerchantsResult> response) {
                Boolean success = Web.checkResponse(CardsFragment.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    ArrayList<SearchedAccount> searchedAccounts = new ArrayList<>();
                    searchedAccounts = response.body().getMerchants();
                    if (searchedAccounts != null) {
                        if (searchedAccounts.size() > 0) {
                            if (merchantsList != null)
                                merchantsList.clear();
                            else {
                                merchantsList = new ArrayList<>();
                            }
                            for (SearchedAccount item : searchedAccounts) {
                                if (item.getAccount_type() == 0) {
                                    boolean isValid = false;
                                    for (SearchedAccount.UsersBean userItem : item.getUsers()) {
                                        if (userItem.getUser_id().equals(Auth.getCurrentAuth().getId()))
                                            if (userItem.getRole().equals("finance") || userItem.getRole().equals("admin"))
                                                isValid = true;

                                    }
                                    if (isValid)
                                        merchantsList.add(item);
                                }
                            }
                            setMerchantsAdapter();
                            updateAppBar();

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MerchantsResult> call, Throwable t) {

            }
        });
    }


    private void loadMyAccount() {
        Web.getInstance().getWebService().getAccountInfo(Auth.getCurrentAuth().getId(), 1).enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                Boolean success = Web.checkResponse(CardsFragment.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    RaadApp.me = response.body();
                    try {
                        SettingHelper.PrefsSave(G.context, SettingHelper.USER_ACCOUNT, RaadApp.me);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    updateAppBar();
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {

            }
        });
    }


    private void updateAppBar() {
        if (RaadApp.me == null || mPayment != null)
            return;

        Account me = RaadApp.me;
        if (RaadApp.selectedMerchant == null) {
            if (!TextUtils.isEmpty(me.name)) {
                mHelperToolbar.setDefaultTitle(me.name);
            } else {
                mHelperToolbar.setDefaultTitle(getString(R.string.paygear_user));
            }
        } else {
            mHelperToolbar.setDefaultTitle(RaadApp.selectedMerchant.getName());
        }

//        if (merchantsList != null && merchantsList.size() > 0) {
//            if (!merchantIconIsAdded) {
//                appBar.removeRightButton(0);
//                appBar.addRightButton(R.drawable.ic_store_white_24dp, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (getActivity() instanceof NavigationBarActivity) {
//                            if (expandableLayout != null) {
//                                if (expandableLayout.isExpanded())
//                                    expandableLayout.collapse(true);
//                                else {
//                                    expandableLayout.expand(true);
//                                }
//                            }
//                        }
//
//                    }
//                });
//                merchantIconIsAdded = true;
//
////                appBar.addRightButton(R.drawable.ic_action_settings, new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        if (getActivity() instanceof NavigationBarActivity) {
////                            RaadApp.selectedMerchant = null;
////                            ((NavigationBarActivity) getActivity()).replaceFragment(
////                                    new SettingsFragment(), "SettingsFragment", true);
////                        }
////                    }
////                });
//
//            }
//
//        }

//        if (me.profilePicture != null && !me.profilePicture.equals("")) {
//            Picasso.with(getContext())
//                    .load(RaadCommonUtils.getImageUrl(me.profilePicture))
//                    .transform(new CircleImageTransform())
//                    .error(R.drawable.ic_person_outline_black_24dp)
//                    .placeholder(R.drawable.profile_white_back)
//                    .fit()
//                    .into(appBarImage);
//        } else {
//            Picasso.with(getContext())
//                    .load(R.drawable.ic_person_outline_black_24dp)
//                    .transform(new CircleImageTransform())
//                    .error(R.drawable.ic_person_outline_black_24dp)
//                    .placeholder(R.drawable.ic_person_outline_black_24dp)
//                    .fit()
//                    .into(appBarImage);
//        }
    }

    private void setAdapter() {
        cardsLayout.removeAllViews();
        viewItems = new ArrayList<>();
        if (selectedMerchant == null) {
            int i = 0;
            if (mCards.size() > 0 && mCards.get(0).isRaadCard()) {
                RaadApp.paygearCard = mCards.get(0);
                if (mPayment == null) {
                    addPayGearCard(mCards.get(0));
                    if (!mCards.get(0).isProtected){
                        if (getActivity() instanceof NavigationBarActivity)
                            ((NavigationBarActivity) getActivity()).pushFullFragment(SetCardPinFragment.newInstance(mCards.get(0)), "SetCardPinFragment");

                    }
                }
                addMyCardsTitle();
                i = 1;
            }

            if ((mCards.size() == 1 && mCards.get(0).isRaadCard()) || mCards.size() == 0) {
                addEmptyCard();
            }

            for (; i < mCards.size(); i++) {
                addCard(i);
            }

            if (mCards == null || mCards.size() == 0) {
                progress.setStatus(2, getString(R.string.no_item));
            } else {
                progress.setStatus(1);
            }

            if (WalletActivity.isScan) {
                ((NavigationBarActivity) getActivity()).pushFullFragment(new ScannerFragment(), "ScannerFragment");
                WalletActivity.isScan = false;
            }
        } else {
            if (merchantCards == null)
                return;

            if (merchantCards.size() > 0) {
                if (mPayment == null) {
                    addPayGearCard(merchantCards.get(0));
                    if (!mCards.get(0).isProtected){
                        if (getActivity() instanceof NavigationBarActivity)
                            ((NavigationBarActivity) getActivity()).pushFullFragment(SetCardPinFragment.newInstance(mCards.get(0)), "SetCardPinFragment");

                    }
                }

            }
            addMyCardsTitle();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().add(cardsLayout.getId(), PaymentHistoryFragment.newInstance(0, false, true, selectedMerchant.get_id()), "PaymentHistoryFragment").commit();
            progress.setStatus(1);
        }
    }

    private void addPayGearCard(final Card card) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_cards_paygear, cardsLayout, false);
        cardsLayout.addView(view);

        ViewGroup rootView = view.findViewById(R.id.rootView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rootView.setBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme_2));
        }
        TextView balanceTitle = view.findViewById(R.id.balance_title);
        ImageView history = view.findViewById(R.id.history);
        ImageView reload = view.findViewById(R.id.reload);

        TextView balance = view.findViewById(R.id.balance);
        TextView unit = view.findViewById(R.id.unit);

        TextView imgQrCode = view.findViewById(R.id.imgQrCode);


        TextView cashableBalance = view.findViewById(R.id.chashable_balance);
        TextView cashableTitle = view.findViewById(R.id.cashable_title);
        TextView giftBalance = view.findViewById(R.id.gift_balance);
        TextView giftTitle = view.findViewById(R.id.gift_title);
        TextView cashout = view.findViewById(R.id.cashout);
        TextView charge = view.findViewById(R.id.charge);
        TextView pin = view.findViewById(R.id.pin_title);
        TextView plus = view.findViewById(R.id.plus);
        LinearLayout balanceLayout = view.findViewById(R.id.balance_layout);

        if (WalletActivity.isDarkTheme) {

            cashableBalance.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            cashableTitle.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            giftBalance.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            giftTitle.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            cashout.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            charge.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            unit.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            balance.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            balanceTitle.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            pin.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            plus.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            view.findViewById(R.id.bals_layout).setBackgroundColor(Color.parseColor("#4C5053"));
        }

        balanceLayout.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor), PorterDuff.Mode.SRC_IN));

        Typefaces.setTypeface(getContext(), Typefaces.IRAN_LIGHT, unit, cashableTitle, cashableBalance, giftTitle, giftBalance);
        Typefaces.setTypeface(getContext(), Typefaces.IRAN_MEDIUM, balanceTitle, balance, cashout, charge,pin);


        Drawable mDrawable = getResources().getDrawable(R.drawable.button_blue_selector_24dp);
        mDrawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor), PorterDuff.Mode.SRC_IN));

        if (selectedMerchant != null) {
            String name = "";
            if (selectedMerchant.getName() != null && !selectedMerchant.getName().equals("")) {
                name = selectedMerchant.getName();
            } else {
                name = selectedMerchant.getUsername();
            }

            if (selectedMerchant.getAccount_type() != 4) {
                if (selectedMerchant.getBusiness_type()==2){
                    balanceTitle.setText(getString(R.string.taxi_balance) + " " + name);
                    cashout.setText(getString(R.string.cashout_taxi));
                }else {
                    balanceTitle.setText(getString(R.string.shop_balance) + " " + name);
                    cashout.setText(getString(R.string.cashout_taxi));
                }
            } else {
                cashout.setText(getString(R.string.cash_out_paygear));
                balanceTitle.setText(getString(R.string.paygear_balance) + " " + name);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.findViewById(R.id.cashout_layout).setBackground(mDrawable);
            view.findViewById(R.id.charge_layout).setBackground(mDrawable);
            view.findViewById(R.id.pin_layout).setBackground(mDrawable);
        }


        imgQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationBarActivity) getActivity()).pushFullFragment(
                        new ScannerFragment(), "ScannerFragment");
            }
        });

        if (selectedMerchant != null) {
            history.setVisibility(View.GONE);
            view.findViewById(R.id.charge_layout).setVisibility(View.GONE);
            view.findViewById(R.id.pin_layout).setVisibility(View.VISIBLE);
            view.findViewById(R.id.pin_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showOTPDialog(true,card);
                }
            });
            if (selectedMerchant.getUsers() != null) {
                boolean isAdmin = false;
                boolean isFinance = false;
                for (SearchedAccount.UsersBean item : selectedMerchant.getUsers()) {
                    if (item.getUser_id().equals(RaadApp.me.id)) {
                        if (item.getRole().equals("admin")) {
                            isAdmin = true;
                        }
                        if (item.getRole().equals("finance")) {
                            isFinance = true;
                        }

                    }
                }
                if (isFinance && !isAdmin) {
                    view.findViewById(R.id.cashout_layout).setVisibility(View.GONE);
                    view.findViewById(R.id.pin_layout).setVisibility(View.GONE);
                } else if (isAdmin) {
                    view.findViewById(R.id.cashout_layout).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.pin_layout).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.cashout_layout).setVisibility(View.GONE);
                    view.findViewById(R.id.pin_layout).setVisibility(View.GONE);
                }
            }


            balance.setText(RaadCommonUtils.formatPrice(card.balance, false));
            cashableBalance.setText(RaadCommonUtils.formatPrice(card.cashOutBalance, false));
            //long giftPrice = card.balance - card.cashOutBalance;
            //giftBalance.setText(RaadCommonUtils.formatPrice(giftPrice, false));
            view.findViewById(R.id.bals_layout).setVisibility(View.GONE);



        } else {
            view.findViewById(R.id.pin_layout).setVisibility(View.GONE);


            // get total of all card club...
            long total = 0L;
            long balance1 = 0L;
            long balanceTotal = 0L;


            for (Card item : mCards) {


                if (item.isRaadCard()) {

                    balance1 = item.balance;

                } else {

                    if (item.type == 1 && (item.bankCode == 69 && item.clubId != null)) {
                        total = total + item.balance;

                    }


                }


                balanceTotal = card.balance + total;
                balance.setText(RaadCommonUtils.formatPrice(balanceTotal, false));


            }

            cashableBalance.setText(RaadCommonUtils.formatPrice(card.balance, false));
            giftBalance.setText(RaadCommonUtils.formatPrice(total, false));

            if (total == 0) {
                view.findViewById(R.id.bals_layout).setVisibility(View.VISIBLE);
                view.findViewById(R.id.balance_layout).getBackground().setColorFilter(new PorterDuffColorFilter(new Theme().getPrimaryColor(getContext()), PorterDuff.Mode.SRC_IN));
            }

        }


//        view.findViewById(R.id.charge_layout).setBackgroundResource(R.drawable.button_green_selector_24dp);
//        view.findViewById(R.id.cashout_layout).setBackgroundResource(R.drawable.button_blue_selector_24dp);
//        view.findViewById(R.id.pin_layout).setBackgroundResource(R.drawable.button_green_selector_24dp);

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationBarActivity) getActivity()).pushFullFragment(
                        PaymentHistoryFragment.newInstance(0, true),
                        "PaymentHistoryFragment");
            }
        });
        view.findViewById(R.id.cashout_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationBarActivity) getActivity()).pushFullFragment(
                        CashOutFragment.newInstance(card, true), "CashOutFragment");
            }
        });
        view.findViewById(R.id.charge_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationBarActivity) getActivity()).pushFullFragment(
                        CashOutFragment.newInstance(RaadApp.paygearCard, false), "CashOutFragment");
            }
        });




        /*reload page*/
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedMerchant == null) {
                    loadCards();
                } else {
                    ShowMerchantView(selectedMerchant);
                }

            }
        });
    }

    private void addMyCardsTitle() {
        if (selectedMerchant == null) {
            Context context = getContext();
            int dp8 = RaadCommonUtils.getPx(8, context);
            int dp16 = RaadCommonUtils.getPx(16, context);
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams titleLayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setLayoutParams(titleLayoutParams);
            layout.setGravity(Gravity.CENTER_VERTICAL);
            layout.setPadding(dp16, dp8, dp16, dp16);
            cardsLayout.addView(layout);

            TextView title2 = new AppCompatTextView(context);
            LinearLayout.LayoutParams title2Params = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT);
            title2Params.weight = 1.0f;
            title2.setLayoutParams(title2Params);
            title2.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            title2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            title2.setTypeface(Typefaces.get(context, Typefaces.IRAN_LIGHT));
            title2.setText(R.string.my_cards);
            layout.addView(title2);


            FrameLayout addCardLayout = new FrameLayout(context);
            int dp40 = RaadCommonUtils.getPx(40, context);
            addCardLayout.setLayoutParams(new LinearLayout.LayoutParams(dp40, dp40));
            addCardLayout.setPadding(dp8, dp8, dp8, dp8);
            layout.addView(addCardLayout);

            AppCompatImageView addCard = new AppCompatImageView(context);
            addCard.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ViewCompat.setBackground(addCard, RaadCommonUtils.getRectShape(context, R.color.add_card_plus_back, 12, 0));
            addCard.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor), PorterDuff.Mode.SRC_IN));

            addCard.setImageResource(R.drawable.ic_action_add_white);
            int dp4 = RaadCommonUtils.getPx(4, context);
            //addCard.setPadding(dp4, dp4, dp4, dp4);
            addCardLayout.addView(addCard);
            addCardLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((NavigationBarActivity) getActivity()).pushFullFragment(
                            new AddCardFragment(), "AddCardFragment");
                }
            });
        } else {
            Context context = getContext();
            int dp8 = RaadCommonUtils.getPx(8, context);
            int dp16 = RaadCommonUtils.getPx(16, context);
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams titleLayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setLayoutParams(titleLayoutParams);
            layout.setGravity(Gravity.CENTER_VERTICAL);
            layout.setPadding(dp16, dp8, dp16, dp16);
            cardsLayout.addView(layout);

            TextView title2 = new TextView(context);
            LinearLayout.LayoutParams title2Params = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT);
            title2Params.weight = 1.0f;
            title2.setLayoutParams(title2Params);
            title2.setTextColor(Color.parseColor("#de000000"));
            title2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            title2.setTypeface(Typefaces.get(context, Typefaces.IRAN_YEKAN_BOLD));
            title2.setText(R.string.payment_history);
            layout.addView(title2);


            FrameLayout transactionLayout = new FrameLayout(context);
            int dp40 = RaadCommonUtils.getPx(40, context);
            transactionLayout.setLayoutParams(new LinearLayout.LayoutParams(dp40, dp40));
            transactionLayout.setPadding(dp8, dp8, dp8, dp8);
            layout.addView(transactionLayout);

            AppCompatImageView transactionHistory = new AppCompatImageView(context);
            transactionHistory.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ViewCompat.setBackground(transactionHistory, RaadCommonUtils.getRectShape(context, R.color.add_card_plus_back, 12, 0));
            transactionHistory.setImageResource(R.drawable.ic_filter_list_white_24dp);
            transactionHistory.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            int dp4 = RaadCommonUtils.getPx(4, context);
            //addCard.setPadding(dp4, dp4, dp4, dp4);
            transactionLayout.addView(transactionHistory);

        }
    }

    private void addEmptyCard() {
        Context context = getContext();
        int dp8 = RaadCommonUtils.getPx(8, context);
        int dp16 = RaadCommonUtils.getPx(16, context);

        int cardHeight = BankCardView.getDefaultCardHeight(getContext());

        CardView cardView = new CardView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, cardHeight);
        params.setMargins(dp16, 0, dp16, dp16);
        cardView.setLayoutParams(params);
        if (WalletActivity.isDarkTheme) {
            cardView.setCardBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme_2));
        } else {
            cardView.setCardBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme));
        }

        cardView.setPreventCornerOverlap(false);
        cardView.setCardElevation(RaadCommonUtils.getPx(6, context));
        cardView.setRadius(RaadCommonUtils.getPx(8, context));
        cardsLayout.addView(cardView);
        viewItems.add(cardView);

        TextView textView = new AppCompatTextView(context);
        CardView.LayoutParams textViewParams = new CardView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textViewParams.gravity = Gravity.CENTER;
        textView.setLayoutParams(textViewParams);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setTypeface(Typefaces.get(context, Typefaces.IRAN_LIGHT));
        textView.setText(R.string.click_here_for_adding_card);
        cardView.addView(textView);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationBarActivity) getActivity()).pushFullFragment(
                        new AddCardFragment(), "AddCardFragment");
            }
        });
    }

    private void addCard(final int position) {
        Card card = mCards.get(position);
        Context context = getContext();
        int dp8 = RaadCommonUtils.getPx(8, context);
        int dp16 = RaadCommonUtils.getPx(16, context);

        int cardHeight = BankCardView.getDefaultCardHeight(getContext());

        BankCardView cardView = new BankCardView(context);
        cardView.setId(position + 1);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, cardHeight);
        if (position > 1) {
            //int dp160 = RaadCommonUtils.getPx(160, context);
            int collapsedDp = RaadCommonUtils.getPx(COLLAPSE, context);
            params.setMargins(dp16, -(cardHeight + dp16 - collapsedDp), dp16, dp16);
        } else {
            params.setMargins(dp16, 0, dp16, dp16);
        }
        cardView.setLayoutParams(params);
        cardView.setPreventCornerOverlap(false);
        cardView.setCardElevation(RaadCommonUtils.getPx(6 + (position * 6), context));
        cardsLayout.addView(cardView);
        viewItems.add(cardView);
        cardView.setCard(card, position == mCards.size() - 1);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NavigationBarActivity) getActivity()).pushFullFragment(
                        CardFragment.newInstance(mPayment, mCards.get(position)), "CardFragment");
            }
        });
    }

    @Override
    public void merchantItemClick(SearchedAccount data, int position) {
        if (position == 1) {
            selectedMerchant = null;
            RaadApp.selectedMerchant = null;
            expandableLayout.collapse();
            load();
//            if (((MainActivity)getActivity()).navBarView!=null){
//                ((MainActivity)getActivity()).navBarView.setVisibility(View.VISIBLE);
//            }
        } else {
            selectedMerchant = data;
            RaadApp.selectedMerchant = data;
            expandableLayout.collapse();
            ShowMerchantView(data);
//            if (((MainActivity)getActivity()).navBarView!=null){
//                ((MainActivity)getActivity()).navBarView.setVisibility(View.GONE);
//            }

        }
        updateAppBar();
        merchantsListAdapter.notifyDataSetChanged();
    }
    private void showOTPDialog(final Boolean isRecovery, final Card merchantCard) {
        otpDialog = new Dialog(getContext());
        WindowManager.LayoutParams params = otpDialog.getWindow().getAttributes();
        otpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        otpDialog.getWindow().setAttributes(params);
        otpDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        otpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        otpDialogBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.otp_dialog, null, false);
        otpDialog.setContentView(otpDialogBinding.getRoot());
        otpDialog.setCanceledOnTouchOutside(false);
        String message = "شما درخواست بازیابی رمز داده اید. پس از تایید پیامکی شامل یک کد ۶ رقمی برای شما ارسال خواهد شد.از این کد برای تغییر رمز استفاده کنید.";
        otpDialogBinding.message.setText(message);


        otpDialogBinding.message.setTypeface(Typefaces.get(getContext(), "IRANYekanRegularMobile(FaNum)"));
        otpDialogBinding.confirm.setTypeface(Typefaces.get(getContext(), "IRANYekanRegularMobile(FaNum)"));
        otpDialogBinding.ignore.setTypeface(Typefaces.get(getContext(), "IRANYekanRegularMobile(FaNum)"));
        otpDialogBinding.title.setTypeface(Typefaces.get(getContext(), "IRANYekanRegularMobile(FaNum)"));

        WindowManager.LayoutParams wmlp = otpDialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.CENTER;
        wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        otpDialog.show();
        otpDialogBinding.ignore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otpDialog.dismiss();
            }
        });
        otpDialogBinding.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otpDialog.dismiss();
                if (getActivity() instanceof NavigationBarActivity)
                    ((NavigationBarActivity) getActivity()).pushFullFragment(SetCardPinFragment.newInstance(isRecovery,merchantCard), "SetCardPinFragment");

            }
        });


    }

    private void ShowMerchantView(final SearchedAccount data) {
        cardsLayout.removeAllViews();
        progress.setStatus(0);
        Web.getInstance().getWebService().getAccountInfo(data.get_id(), 1).enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                Boolean success = Web.checkResponse(CardsFragment.this, call, response);
                mRefreshLayout.setRefreshing(false);
                if (success == null)
                    return;

                if (success) {
                    getCredit(data);
//                    loadCards();

                } else {
                    progress.setStatus(-1, getString(R.string.error));
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                if (Web.checkFailureResponse(CardsFragment.this, call, t)) {
                    progress.setStatus(-1, getString(R.string.network_error));
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });

    }

    private void getCredit(SearchedAccount data) {
        Web.getInstance().getWebService().getUserCards(data.get_id()).enqueue(new Callback<ArrayList<Card>>() {
            @Override
            public void onResponse(Call<ArrayList<Card>> call, Response<ArrayList<Card>> response) {
                Boolean success = Web.checkResponse(CardsFragment.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    progress.setStatus(1);
                    merchantCards = response.body();
                    setAdapter();


                } else {
                    progress.setStatus(-1, getString(R.string.error));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Card>> call, Throwable t) {
                if (Web.checkFailureResponse(CardsFragment.this, call, t)) {
                    progress.setStatus(-1, getString(R.string.network_error));
                }
            }
        });

    }

    @Override
    public void setRefreshLayout(boolean refreshLayout) {
        try {
            if (isAdded() && mRefreshLayout != null)
                load();
        } catch (Exception e) {
        }

    }

    @Override
    public void onLeftIconClickListener(View view) {
        if (getActivity() != null)
            getActivity().onBackPressed();
    }

    @Override
    public void onRightIconClickListener(View view) {
        RaadApp.selectedMerchant = null;
        if (getActivity() instanceof NavigationBarActivity) {
            ((NavigationBarActivity) getActivity()).pushFullFragment(
                    FragmentSettingWallet.newInstance(), "FragmentSettingWallet");
        }
    }

    @Override
    public void onSecondRightIconClickListener(View view) {
        if (getActivity() instanceof NavigationBarActivity) {
            if (expandableLayout != null) {
                if (expandableLayout.isExpanded())
                    expandableLayout.collapse(true);
                else {
                    expandableLayout.expand(true);
                }
            }
        }
    }
}
