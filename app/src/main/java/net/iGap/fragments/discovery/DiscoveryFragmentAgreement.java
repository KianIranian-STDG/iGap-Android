package net.iGap.fragments.discovery;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.discovery.DiscoveryItemField;
import net.iGap.adapter.items.discovery.holder.BaseViewHolder;
import net.iGap.fragments.FragmentToolBarBack;
import net.iGap.helper.HelperError;
import net.iGap.request.RequestClientSetDiscoveryItemAgreement;
import net.iGap.request.RequestInfoPage;

public class DiscoveryFragmentAgreement extends FragmentToolBarBack {
    private DiscoveryItemField discoveryField;
    private String agreementSlug;
    private TextView agreement;
    private SwipeRefreshLayout pullToRefresh;
    private TextView emptyRecycle;
    private View scroll_view;

    public static DiscoveryFragmentAgreement newInstance(DiscoveryItemField discoveryField, String agreementSlug) {
        DiscoveryFragmentAgreement discoveryFragment = new DiscoveryFragmentAgreement();
        Bundle bundle = new Bundle();

        bundle.putSerializable("discoveryField", discoveryField);
        bundle.putString("agreementSlug", agreementSlug);
        discoveryFragment.setArguments(bundle);
        return discoveryFragment;
    }

    @Override
    public void onCreateViewBody(LayoutInflater inflater, LinearLayout root, @Nullable Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_discovery_agreement, root, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleTextView.setText(getContext().getText(R.string.agrement));
        agreementSlug = getArguments().getString("agreementSlug");
        discoveryField = (DiscoveryItemField) getArguments().getSerializable("discoveryField");
        agreement = view.findViewById(R.id.agreement);
        scroll_view = view.findViewById(R.id.scroll_view);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        emptyRecycle = view.findViewById(R.id.emptyRecycle);
        CheckBox fpc_checkBox_trabord = view.findViewById(R.id.fpc_checkBox_trabord);

        emptyRecycle.setOnClickListener(v -> loadData());
        pullToRefresh.setOnRefreshListener(this::loadData);

        loadData();

        fpc_checkBox_trabord.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                new MaterialDialog.Builder(getActivity()).title(R.string.accept_the_terms).
                        content(R.string.are_you_sure)
                        .positiveText(R.string.ok)
                        .negativeText(R.string.cancel)
                        .onPositive((dialog, which) -> {
                            boolean isSend = new RequestClientSetDiscoveryItemAgreement().setAgreement(new RequestClientSetDiscoveryItemAgreement.OnSetAgreement() {
                                @Override
                                public void onSet() {
                                    G.handler.post(() -> {
                                        discoveryField.agreement = true;
                                        onBackButtonClicked(buttonView);
                                        BaseViewHolder.handleDiscoveryFieldsClickStatic(discoveryField);
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
                        }).show();
            }
        });
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
            pullToRefresh.setRefreshing(true);
        });
    }

    private void setStateErrorLoad() {
        G.handler.post(() -> {
            HelperError.showSnackMessage(getString(R.string.wallet_error_server), false);
            emptyRecycle.setVisibility(View.VISIBLE);
            scroll_view.setVisibility(View.GONE);
            agreement.setVisibility(View.GONE);
            pullToRefresh.setRefreshing(false);
        });

    }

    private void setStateLoadDone(String body) {
        G.handler.post(() -> {
            scroll_view.setVisibility(View.VISIBLE);
            emptyRecycle.setVisibility(View.GONE);
            agreement.setVisibility(View.VISIBLE);
            agreement.setText(Html.fromHtml(body).toString());
            pullToRefresh.setRefreshing(false);
        });
    }
}
