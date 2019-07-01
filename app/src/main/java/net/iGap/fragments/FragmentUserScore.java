package net.iGap.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintSet;
import android.support.transition.TransitionManager;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentUserScoreBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.proto.ProtoUserIVandGetScore;
import net.iGap.viewmodel.UserScoreViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FragmentUserScore extends BaseFragment {

    private UserScoreViewModel viewModel;
    private FragmentUserScoreBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_score, container, false);
        viewModel = new UserScoreViewModel();
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HelperToolbar t = HelperToolbar.create().setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.history_icon)
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

        binding.scoreView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Ensure you call it only once
                binding.scoreView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                ConstraintSet set = new ConstraintSet();
                set.clone(binding.root);
                set.constrainCircle(binding.scorePointer.getId(), binding.scoreView.getId(), binding.scoreView.getWidth() / 2, 0);
                set.constrainCircle(binding.rankPointer.getId(), binding.rankView.getId(), binding.rankView.getWidth() / 2, 180);
                set.applyTo(binding.root);
            }
        });

        viewModel.userScorePointer.observe(this, integer -> {
            if (integer != null) {
                ConstraintSet set = new ConstraintSet();
                set.clone(binding.root);
                set.constrainCircle(binding.scorePointer.getId(), binding.scoreView.getId(), binding.scoreView.getWidth() / 2, integer);
                set.applyTo(binding.root);
                TransitionManager.beginDelayedTransition(binding.root);
            }
        });

        viewModel.userRankPointer.observe(this, integer -> {
            if (integer != null) {
                ConstraintSet set = new ConstraintSet();
                set.clone(binding.root);
                set.constrainCircle(binding.rankPointer.getId(), binding.rankView.getId(), binding.rankView.getWidth() / 2, integer);
                set.applyTo(binding.root);
                TransitionManager.beginDelayedTransition(binding.root);
            }
        });

        viewModel.ivandScore.observe(this , iVandScores -> {

            if (iVandScores != null && iVandScores.size() != 0 ){

                binding.rvScoreList.setLayoutManager(new LinearLayoutManager(getContext()));
                IvandScoreAdapter adapter = new IvandScoreAdapter();
                adapter.setItems(iVandScores);
                binding.rvScoreList.setAdapter(adapter);

            }
        });
    }


    private class IvandScoreAdapter extends RecyclerView.Adapter<IvandScoreAdapter.ViewHolder>{

        private List<ProtoUserIVandGetScore.UserIVandGetScoreResponse.IVandScore> items ;
        private LayoutInflater inflater ;

        public IvandScoreAdapter() {
            inflater = LayoutInflater.from(getContext());
        }

        public void setItems(List<ProtoUserIVandGetScore.UserIVandGetScoreResponse.IVandScore> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(inflater.inflate(R.layout.row_score_items , viewGroup , false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            viewHolder.bindView(items.get(viewHolder.getAdapterPosition()));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder{

            AppCompatTextView title , count ;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                title = itemView.findViewById(R.id.row_score_txt_title);
                count = itemView.findViewById(R.id.row_score_txt_score_count);
            }

            private void bindView(ProtoUserIVandGetScore.UserIVandGetScoreResponse.IVandScore iVandScore) {

                if (G.selectedLanguage.equals("fa")){
                    title.setText(iVandScore.getFaName());
                }else {
                    title.setText(iVandScore.getEnName());
                }

                count.setText(iVandScore.getScore() + " " + getString(R.string.point));
            }
        }
    }
}
