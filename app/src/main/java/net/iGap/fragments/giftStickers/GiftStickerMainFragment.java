package net.iGap.fragments.giftStickers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.fragments.giftStickers.buyStickerCompleted.BuyGiftStickerCompletedFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;

public class GiftStickerMainFragment extends BaseFragment {

    private HelperToolbar helperToolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gift_sticker_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        helperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.icon_back)
                .setLogoShown(true)
                .setDefaultTitle(getResources().getString(R.string.gift_sticker_title))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getChildFragmentManager().findFragmentById(R.id.giftStickerContainer) instanceof BuyGiftStickerCompletedFragment) {
                            goToHomePage();
                        } else {
                            if (getActivity() != null) {
                                getActivity().onBackPressed();
                            }
                        }
                    }
                });

        LinearLayout toolbar = view.findViewById(R.id.toolbar);
        toolbar.addView(helperToolbar.getView());

        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.giftStickerContainer);
        if (fragment == null) {
            getChildFragmentManager().beginTransaction()
                    .addToBackStack(GiftStickerHomeFragment.class.getName())
                    .add(R.id.giftStickerContainer, new GiftStickerHomeFragment(), GiftStickerHomeFragment.class.getName())
                    .commit();
        } else {
            getChildFragmentManager().beginTransaction()
                    .add(R.id.giftStickerContainer, fragment, fragment.getClass().getName()).commit();
        }
    }

    @Override
    public boolean onBackPressed() {
        if (getChildFragmentManager().getBackStackEntryCount() > 1) {
            if (getChildFragmentManager().findFragmentById(R.id.giftStickerContainer) instanceof BuyGiftStickerCompletedFragment) {
                goToHomePage();
            } else {
                getChildFragmentManager().popBackStackImmediate();
                if (getChildFragmentManager().getBackStackEntryCount() == 1) {
                    setToolbarTitle(R.string.gift_sticker_title);
                }
            }
            return true;
        } else {
            return super.onBackPressed();
        }
    }

    public void loadBuyMySticker() {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(GiftStickerPurchasedByMeMainFragment.class.getName());
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        if (fragment == null) {
            fragment = new GiftStickerPurchasedByMeMainFragment();
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        fragmentTransaction.replace(R.id.giftStickerContainer, fragment, fragment.getClass().getName()).commit();
    }

    public void loadReceivedGiftStickerPage() {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(MyGiftStickerReceivedFragment.class.getName());
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        if (fragment == null) {
            fragment = new MyGiftStickerReceivedFragment();
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        fragmentTransaction.replace(R.id.giftStickerContainer, fragment, fragment.getClass().getName()).commit();
    }

    public void loadStickerPackagePage() {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.giftStickerContainer);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putBoolean("showTitle", false);
        if (!(fragment instanceof GiftStickerPackageListFragment)) {
            fragment = GiftStickerPackageListFragment.getInstance(false);
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.giftStickerContainer, fragment, fragment.getClass().getName()).commit();
    }

    public void loadStickerPackageItemPage(StructIGStickerGroup stickerGroup) {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.giftStickerContainer);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        if (!(fragment instanceof GiftStickerItemListFragment)) {
            fragment = GiftStickerItemListFragment.getInstance(stickerGroup, false);
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        fragmentTransaction.replace(R.id.giftStickerContainer, fragment, fragment.getClass().getName()).commit();
    }

    public void loadStickerPackageItemDetailPage(StructIGSticker sticker) {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.giftStickerContainer);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        if (!(fragment instanceof GiftStickerItemDetailFragment)) {
            fragment = GiftStickerItemDetailFragment.getInstance(sticker, false);
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        fragmentTransaction.replace(R.id.giftStickerContainer, fragment, fragment.getClass().getName()).commit();
    }

    public void loadPaymentFragment(StructIGSticker structIGSticker, String paymentToke) {
        if (getActivity() != null) {
            new HelperFragment(getActivity().getSupportFragmentManager()).loadPayment(getString(R.string.gift_sticker_title), paymentToke, result -> {
                Log.wtf(this.getClass().getName(), "result.isSuccess(): " + result.isSuccess());
                if (result.isSuccess()) {
                    Toast.makeText(getActivity(), getString(R.string.successful_payment), Toast.LENGTH_LONG).show();
                    Fragment fragment = getChildFragmentManager().findFragmentById(R.id.giftStickerContainer);
                    FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                    if (!(fragment instanceof BuyGiftStickerCompletedFragment)) {
                        fragment = BuyGiftStickerCompletedFragment.getInstance(structIGSticker, null);
                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    }
                    fragmentTransaction.replace(R.id.giftStickerContainer, fragment, fragment.getClass().getName()).commit();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.unsuccessful_payment), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void goToHomePage() {
        getChildFragmentManager().popBackStack(GiftStickerHomeFragment.class.getName(), 0);
        setToolbarTitle(R.string.gift_sticker_title);
    }

    public void setToolbarTitle(int titleRes) {
        helperToolbar.setDefaultTitle(getString(titleRes));
    }
}
