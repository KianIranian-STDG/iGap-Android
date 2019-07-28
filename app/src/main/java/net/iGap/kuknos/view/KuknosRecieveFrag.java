package net.iGap.kuknos.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentKuknosRecieveBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.kuknos.viewmodel.KuknosRecieveVM;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.module.AndroidUtils;

public class KuknosRecieveFrag extends BaseFragment {

    private FragmentKuknosRecieveBinding binding;
    private KuknosRecieveVM kuknosRecieveVM;
    private HelperToolbar mHelperToolbar;

    public static KuknosRecieveFrag newInstance() {
        KuknosRecieveFrag kuknosLoginFrag = new KuknosRecieveFrag();
        return kuknosLoginFrag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknosRecieveVM = ViewModelProviders.of(this).get(KuknosRecieveVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_recieve, container, false);
        binding.setViewmodel(kuknosRecieveVM);
        binding.setLifecycleOwner(this);

        return binding.getRoot();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.fragKuknosRcToolbar;
        Utils.darkModeHandler(toolbarLayout);
        toolbarLayout.addView(mHelperToolbar.getView());

        Button copyBtn = binding.fragKuknosRcCopy;
        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyWalletID();
            }
        });

        loadQrCode();

    }

    private void loadQrCode() {

        kuknosRecieveVM.getQrCodeURl().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                G.imageLoader.clearMemoryCache();
                G.imageLoader.displayImage(AndroidUtils.suitablePath(kuknosRecieveVM.getQrCodeURl().getValue()), binding.fragKuknosRcQrCode);
            }
        });

    }

    private void copyWalletID() {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("My Kuknos wallet address is: ", kuknosRecieveVM.getClientKey().getValue());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(), R.string.kuknos_recieve_copyToast, Toast.LENGTH_SHORT).show();
    }

}
