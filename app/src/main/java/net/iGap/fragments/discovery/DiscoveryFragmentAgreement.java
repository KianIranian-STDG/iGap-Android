package net.iGap.fragments.discovery;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.discovery.DiscoveryItemField;
import net.iGap.adapter.items.discovery.holder.BaseViewHolder;
import net.iGap.helper.HelperError;
import net.iGap.module.AndroidUtils;
import net.iGap.request.RequestClientSetDiscoveryItemAgreement;
import net.iGap.request.RequestInfoPage;

public class DiscoveryFragmentAgreement extends DialogFragment {
    private DiscoveryItemField discoveryField;
    private String agreementSlug;
    private TextView agreement;
    private TextView emptyRecycle;
    private View scroll_view;
    private TextView btnPositive;
    private TextView btnNegative;
    private CheckBox checkBox;

    public static DiscoveryFragmentAgreement newInstance(DiscoveryItemField discoveryField, String agreementSlug) {
        DiscoveryFragmentAgreement discoveryFragment = new DiscoveryFragmentAgreement();
        Bundle bundle = new Bundle();
        bundle.putSerializable("discoveryField", discoveryField);
        bundle.putString("agreementSlug", agreementSlug);
        discoveryFragment.setArguments(bundle);
        return discoveryFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discovery_agreement, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        agreementSlug = getArguments().getString("agreementSlug");
        discoveryField = (DiscoveryItemField) getArguments().getSerializable("discoveryField");
        agreement = view.findViewById(R.id.agreement);
        scroll_view = view.findViewById(R.id.scroll_view);
        emptyRecycle = view.findViewById(R.id.emptyRecycle);
        btnPositive = view.findViewById(R.id.btnPositive);
        btnNegative = view.findViewById(R.id.btnNegative);
        checkBox = view.findViewById(R.id.fpc_checkBox_trabord);

        btnPositive.setOnClickListener(v -> onPositiveClicked());
        btnNegative.setOnClickListener(v -> onNegativeClicked());

        emptyRecycle.setOnClickListener(v -> loadData());

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.width = (int) (AndroidUtils.displaySize.x * .9);
                view.setLayoutParams(params);
            }
        });

        loadData();
    }

    private void onNegativeClicked() {
        dismiss();
    }

    private void onPositiveClicked() {
        if (!checkBox.isChecked()) {
            Toast.makeText(getContext(), R.string.error_term_not_accepted, Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isSend = new RequestClientSetDiscoveryItemAgreement().setAgreement(new RequestClientSetDiscoveryItemAgreement.OnSetAgreement() {
            @Override
            public void onSet() {
                G.handler.post(() -> {
                    discoveryField.agreement = true;
                    dismiss();
                    BaseViewHolder.handleDiscoveryFieldsClickStatic(discoveryField, getActivity(), false);
                });
            }

            @Override
            public void onError(int major, int minor) {
                HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
            }
        }, discoveryField.id);

        if (!isSend) {
            HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
        }
    }

    private void loadData() {
        boolean isSend = new RequestInfoPage().infoPageAgreementDiscovery(agreementSlug, new RequestInfoPage.OnInfoPage() {
            @Override
            public void onInfo(String body) {
                setStateLoadDone(body);
            }

            @Override
            public void onError(int major, int minor) {
                setStateErrorLoad();
            }
        });

        if (isSend) {
            setStateLoadingData();
        } else {
            setStateErrorLoad();
        }
    }

    private void setStateLoadingData() {
        G.handler.post(() -> {
            scroll_view.setVisibility(View.GONE);
            emptyRecycle.setVisibility(View.GONE);
            agreement.setVisibility(View.GONE);
        });
    }

    private void setStateErrorLoad() {
        G.handler.post(() -> {
            if (getContext() != null)
                HelperError.showSnackMessage(getContext().getString(R.string.wallet_error_server), false);
            emptyRecycle.setVisibility(View.VISIBLE);
            scroll_view.setVisibility(View.GONE);
            agreement.setVisibility(View.GONE);
        });

    }

    private void setStateLoadDone(String body) {
        G.handler.post(() -> {
            scroll_view.setVisibility(View.VISIBLE);
            emptyRecycle.setVisibility(View.GONE);
            agreement.setVisibility(View.VISIBLE);
            agreement.setText(Html.fromHtml(body).toString());
        });
    }
}
