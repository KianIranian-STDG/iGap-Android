package net.iGap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.google.zxing.integration.android.IntentIntegrator;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentUserScoreBinding;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.proto.ProtoUserIVandGetScore;
import net.iGap.viewmodel.UserScoreViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FragmentUserScore extends BaseFragment {

    private UserScoreViewModel viewModel;
    private FragmentUserScoreBinding binding;
    private static final int REQUEST_CODE_QR_IVAND_CODE = 543;
    private static final String TO_HOW_TO_GET_POINTS_URL = "https://d.igap.net/score";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(UserScoreViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_score, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HelperToolbar t = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.icon_back)
                .setRightIcons(R.string.icon_time)
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.score))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }

                    @Override
                    public void onRightIconClickListener(View view) {
                        // to go history
                        if (getActivity() != null) {
                            new HelperFragment(getActivity().getSupportFragmentManager(), FragmentIVandActivities.newInstance()).setReplace(false).load();
                        }
                    }
                });

        binding.toolbar.addView(t.getView());

        binding.rvScoreList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvScoreList.setAdapter(new IvandScoreAdapter());

        viewModel.getUserRankPointer().observe(getViewLifecycleOwner(), integer -> {
            if (integer != null) {
                ConstraintSet set = new ConstraintSet();
                set.clone(binding.root);
                set.constrainCircle(binding.rankPointer.getId(), binding.rankView.getId(), binding.rankView.getWidth() / 2, integer);
                set.applyTo(binding.root);
                TransitionManager.beginDelayedTransition(binding.root);
            }
        });

        viewModel.getIvandScore().observe(getViewLifecycleOwner(), iVandScores -> {
            if (binding.rvScoreList.getAdapter() instanceof IvandScoreAdapter && iVandScores != null) {
                ((IvandScoreAdapter) binding.rvScoreList.getAdapter()).setItems(iVandScores);
            }
        });

        viewModel.getGoToScannerPage().observe(getViewLifecycleOwner(), isGo -> {
            if (getActivity() != null && isGo != null && isGo) {
                IntentIntegrator integrator = new IntentIntegrator(getActivity());
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setRequestCode(REQUEST_CODE_QR_IVAND_CODE);
                integrator.setBeepEnabled(false);
                integrator.setPrompt("");
                integrator.initiateScan();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessageResource -> {
            if (errorMessageResource != null) {
                HelperError.showSnackMessage(getString(errorMessageResource), false);
            }
        });

        viewModel.getHowToGetPoints().observe(getViewLifecycleOwner(), go -> {
            getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).add(R.id.baseRootUserScore, FragmentWebView.newInstance(TO_HOW_TO_GET_POINTS_URL)).commit();
        });
    }


    private class IvandScoreAdapter extends RecyclerView.Adapter<IvandScoreAdapter.ViewHolder> {

        private List<ProtoUserIVandGetScore.UserIVandGetScoreResponse.IVandScore> items;

        public void setItems(List<ProtoUserIVandGetScore.UserIVandGetScoreResponse.IVandScore> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_score_items, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            viewHolder.bindView(items.get(viewHolder.getAdapterPosition()));
        }

        @Override
        public int getItemCount() {
            return items != null ? items.size() : 0;
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            AppCompatTextView title, count, addIcon;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                title = itemView.findViewById(R.id.row_score_txt_title);
                count = itemView.findViewById(R.id.row_score_txt_score_count);
                addIcon = itemView.findViewById(R.id.addIcon);
            }

            private void bindView(ProtoUserIVandGetScore.UserIVandGetScoreResponse.IVandScore iVandScore) {
                if (iVandScore.getScore() >= 0) {
                    addIcon.setText(R.string.icon_add_whit_circle);
                    addIcon.setTextColor(getContext().getResources().getColor(R.color.green));
                    count.setTextColor(getContext().getResources().getColor(R.color.green));
                } else {
                    addIcon.setText(R.string.icon_delete_minus);
                    addIcon.setTextColor(getContext().getResources().getColor(R.color.red));
                    count.setTextColor(getContext().getResources().getColor(R.color.red));
                }

                if (G.isAppRtl) {
                    title.setText(iVandScore.getFaName());
                } else {
                    title.setText(iVandScore.getEnName());
                }

                count.setText(checkPersianNumber(String.valueOf(Math.abs(iVandScore.getScore()))) + " " + getString(R.string.point));
            }

            private String checkPersianNumber(String text) {
                if (HelperCalander.isPersianUnicode) {
                    return HelperCalander.convertToUnicodeFarsiNumber(text);
                } else {
                    return text;
                }
            }

        }
    }
}
