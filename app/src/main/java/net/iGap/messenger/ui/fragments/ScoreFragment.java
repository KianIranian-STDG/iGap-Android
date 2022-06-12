package net.iGap.messenger.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.integration.android.IntentIntegrator;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.FragmentIVandActivities;
import net.iGap.fragments.FragmentWebView;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.theme.ThemeDescriptor;
import net.iGap.messenger.ui.cell.TextCell;
import net.iGap.messenger.ui.components.AlertsCreator;
import net.iGap.messenger.ui.components.ScoreView;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.messenger.ui.toolBar.ToolbarItems;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.observers.interfaces.OnUserIVandGetScore;
import net.iGap.proto.ProtoUserIVandGetScore;
import net.iGap.request.RequestUserIVandGetScore;

import java.util.ArrayList;
import java.util.List;

public class ScoreFragment extends BaseFragment {
    private final static int history_button = 1;
    private final static int barcode_button = 2;
    private final static int point_button = 3;
    private final MutableLiveData<Integer> userScore = new MutableLiveData<>();
    private final MutableLiveData<Integer> userRank = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalRank = new MutableLiveData<>();
    private final MutableLiveData<List<ProtoUserIVandGetScore.UserIVandGetScoreResponse.IVandScore>> scores = new MutableLiveData<>();
    private List<ProtoUserIVandGetScore.UserIVandGetScoreResponse.IVandScore> scoreList = new ArrayList<>();
    private static final String TO_HOW_TO_GET_POINTS_URL = "https://d.igap.net/score";
    private static final int REQUEST_CODE_QR_IVAND_CODE = 543;
    private ScoreView scoreView;
    private int totalScroll;
    private ListAdapter listAdapter;
    private RecyclerListView listView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        requestGetScore();
    }

    @Override
    public View createView(Context context) {
        fragmentView = new FrameLayout(context);
        FrameLayout rootView = (FrameLayout) fragmentView;
        rootView.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        scoreView = new ScoreView(context);
        FrameLayout.LayoutParams scoreParams = LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 130, Gravity.TOP, 16, 12, 16, 0);
        listView = new RecyclerListView(context);
        listView.setAdapter(listAdapter = new ListAdapter());
        listView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        listView.setPadding(0, LayoutCreator.dp(140), 0, LayoutCreator.dp(25));
        listView.setClipToPadding(false);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        View topRect = new View(context);
        topRect.setBackgroundColor(Theme.getColor(Theme.key_toolbar_background));
        topRect.setLayoutParams(LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 80, Gravity.TOP));
        rootView.addView(listView);
        rootView.addView(topRect);
        rootView.addView(scoreView, scoreParams);
        listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalScroll += dy;
                if (dy != 0) {
                    scoreView.setAlpha(1 - (float) totalScroll / 100);
                    scoreView.setScaleY(1 - (float) totalScroll / 1000);
                    scoreView.setScaleX(1 - (float) totalScroll / 1000);
                    topRect.setTranslationY(-totalScroll);
                }
            }
        });
        userScore.observe(getViewLifecycleOwner(), score -> {
            scoreView.setScoreText(checkPersianNumber(score.toString()));
        });
        userRank.observe(getViewLifecycleOwner(), rank -> {
            scoreView.setRankText(checkPersianNumber(rank.toString()));
        });
        totalRank.observe(getViewLifecycleOwner(), totalRank -> {
            scoreView.setTotalRanksText(checkPersianNumber(totalRank.toString()));
        });
        scores.observe(getViewLifecycleOwner(), scores -> {
            scoreList = scores;
            listAdapter.notifyDataSetChanged();
        });
        return fragmentView;
    }

    private String checkPersianNumber(String text) {
        if (HelperCalander.isPersianUnicode) {
            return HelperCalander.convertToUnicodeFarsiNumber(text);
        } else {
            return text;
        }
    }

    @SuppressLint("ResourceType")
    @Override
    public View createToolBar(Context context) {
        toolbar = new Toolbar(context);
        toolbar.setTitle(context.getString(R.string.UserScore));
        toolbar.setBackIcon(R.drawable.ic_ab_back);
        ToolbarItems toolbarItems = toolbar.createToolbarItems();
        toolbarItems.addItem(history_button,R.string.icon_time,Color.WHITE);
        toolbarItems.addItem(barcode_button,R.string.icon_QR_code,Color.WHITE);
        toolbarItems.addItem(point_button,R.string.icon_FAQ,Color.WHITE);
        toolbar.setListener(id -> {
            if (id == -1) {
                finish();
            } else if (id == history_button) {
                if (getActivity() != null) {
                    new HelperFragment(getActivity().getSupportFragmentManager(), FragmentIVandActivities.newInstance()).setReplace(false).load();
                }
            } else if (id == barcode_button){
                if (getActivity() != null) {
                    IntentIntegrator integrator = new IntentIntegrator(getActivity());
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                    integrator.setRequestCode(REQUEST_CODE_QR_IVAND_CODE);
                    integrator.setBeepEnabled(false);
                    integrator.setPrompt("");
                    integrator.initiateScan();
                }
            } else if (id == point_button){
                if (getActivity() != null) {
                    new HelperFragment(getActivity().getSupportFragmentManager(), FragmentWebView.newInstance(TO_HOW_TO_GET_POINTS_URL)).setReplace(false).load();
                }
            }
        });
        return toolbar;
    }

    private void requestGetScore() {
        new RequestUserIVandGetScore().userIVandGetScore(new OnUserIVandGetScore() {
            @Override
            public void getScore(ProtoUserIVandGetScore.UserIVandGetScoreResponse.Builder score) {
                scores.postValue(score.getScoresList());
                userScore.postValue(score.getScore());
                userRank.postValue(score.getUserRank());
                totalRank.postValue(score.getTotalRank());
            }

            @Override
            public void onError(int major, int minor) {
                AlertsCreator.showSimpleAlert(ScoreFragment.this, context.getString(R.string.time_out_error));
            }
        });
    }

    @Override
    public List<ThemeDescriptor> getThemeDescriptor() {
        List<ThemeDescriptor> themeDescriptors = new ArrayList<>();
        themeDescriptors.add(new ThemeDescriptor(scoreView, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_window_background));
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_CELLBACKGROUNDCOLOR, Theme.key_window_background));
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_window_background));
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(toolbar, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_toolbar_background));
        return themeDescriptors;
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextCell textCell = new TextCell(context);
            textCell.setBackgroundColor(Theme.getColor(Theme.key_window_background));
            return new RecyclerListView.Holder(textCell);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            TextCell textCell = (TextCell) holder.itemView;
            ProtoUserIVandGetScore.UserIVandGetScoreResponse.IVandScore score = scoreList.get(position);
            if (score != null) {
                textCell.setScoreValue(score, true);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return scoreList.size();
        }
    }
}
