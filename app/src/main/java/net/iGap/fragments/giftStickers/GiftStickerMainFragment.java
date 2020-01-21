package net.iGap.fragments.giftStickers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;

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
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setDefaultTitle("استیکر هدیه")
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
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
            getChildFragmentManager().popBackStack();
            return true;
        } else {
            return super.onBackPressed();
        }
    }

    public void loadBuyMySticker() {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(MyGiftStickerBuyFragment.class.getName());
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        if (fragment == null) {
            fragment = new MyGiftStickerBuyFragment();
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
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.transferMoneyContainer);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        if (!(fragment instanceof GiftStickerPackageListFragment)) {
            fragment = new GiftStickerPackageListFragment();
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        fragmentTransaction.replace(R.id.giftStickerContainer, fragment, fragment.getClass().getName()).commit();
    }

    public void loadStickerPackageItemPage(StructIGStickerGroup stickerGroup) {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.transferMoneyContainer);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        if (!(fragment instanceof GiftStickerItemListFragment)) {
            fragment = GiftStickerItemListFragment.getInstance(stickerGroup);
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        fragmentTransaction.replace(R.id.giftStickerContainer, fragment, fragment.getClass().getName()).commit();
    }

    public void setToolbarTitle(int titleRes) {
        helperToolbar.setDefaultTitle(getString(titleRes));
    }
}
