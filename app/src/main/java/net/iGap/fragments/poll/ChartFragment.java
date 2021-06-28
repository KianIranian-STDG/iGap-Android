package net.iGap.fragments.poll;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.adapter.items.poll.PollItem;
import net.iGap.adapter.items.poll.PollItemField;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperToolbar;
import net.iGap.module.Theme;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.request.RequestClientGetPoll;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static net.iGap.helper.HelperCalander.convertToUnicodeFarsiNumber;

public class ChartFragment extends BaseFragment {
    private String[] labels;
    private ArrayList<BarEntry> barEntries;
    private List<PollItem> pollList;
    private BarChart chart;
    private HelperToolbar helperToolbar;
    private SwipeRefreshLayout swipeRefresh;
    private TextView emptyRecycle;
    private int pollId;
    private LinearLayout toolbar;
    private int totalWith;

    public static ChartFragment newInstance(int page) {
        ChartFragment chartFragment = new ChartFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("pollId", page);
        chartFragment.setArguments(bundle);
        return chartFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_poll_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.chart_toolbar);
        emptyRecycle = view.findViewById(R.id.emptyRecycle_chart);
        swipeRefresh = view.findViewById(R.id.sweep);
        chart = view.findViewById(R.id.type8_chart0);

        if (getArguments() != null) {
            pollId = getArguments().getInt("pollId");
        } else {
            if (getActivity() != null)
                getActivity().onBackPressed();
        }

        helperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.icon_back)
                .setLogoShown(true)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                });
        toolbar.addView(helperToolbar.getView());


        XAxis xAxis = chart.getXAxis();
        xAxis.setTextColor(new Theme().getTitleTextColor(chart.getContext()));
        xAxis.setTypeface(ResourcesCompat.getFont(chart.getContext(), R.font.main_font));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(ViewMaker.dpToPixel(4));
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        tryToUpdateOrFetchRecycleViewData(0);
        chart.setMaxVisibleValueCount(100);
        chart.setNoDataText(getString(R.string.Submitـomment));
        chart.setPinchZoom(false);
        chart.setHighlightPerTapEnabled(false);
        chart.setDrawValueAboveBar(true);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setTouchEnabled(true);
        chart.setDoubleTapToZoomEnabled(false);
        chart.getAxisLeft().setAxisMinimum(0);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.animateY(1000);
        chart.getLegend().setEnabled(false);
        chart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) Math.floor(value));
            }
        });

        emptyRecycle.setOnClickListener(v -> {
            boolean isSend = updateOrFetchRecycleViewData();
            if (!isSend) {
                HelperError.showSnackMessage(getString(R.string.wallet_error_server), false);
            } else {
                emptyRecycle.setVisibility(View.GONE);
            }
        });


        swipeRefresh.setOnRefreshListener(() -> {
            swipeRefresh.setRefreshing(true);
            boolean isSend = updateOrFetchRecycleViewData();
            if (!isSend) {
                swipeRefresh.setRefreshing(false);
                HelperError.showSnackMessage(getString(R.string.wallet_error_server), false);
            } else {
                emptyRecycle.setVisibility(View.GONE);
            }
        });
    }

    private void tryToUpdateOrFetchRecycleViewData(int count) {
        swipeRefresh.setRefreshing(true);
        boolean isSend = updateOrFetchRecycleViewData();
        if (!isSend) {
            if (count < 3) {
                G.handler.postDelayed(() -> tryToUpdateOrFetchRecycleViewData(count + 1), 1000);
            } else {
                swipeRefresh.setRefreshing(false);
                emptyRecycle.setVisibility(View.VISIBLE);
            }
        } else {
            emptyRecycle.setVisibility(View.GONE);
        }
    }

    private boolean updateOrFetchRecycleViewData() {
        return new RequestClientGetPoll().getPoll(pollId, new OnPollList() {
            @Override
            public void onPollListReady(ArrayList<PollItem> pollArrayList, String title) {
                G.handler.post(() -> {
                    pollList = pollArrayList;
                    showChart();
                    helperToolbar.setDefaultTitle(title);
                    swipeRefresh.setRefreshing(false);
                });
            }

            @Override
            public void onError(int major, int minor) {
                swipeRefresh.setRefreshing(false);
                emptyRecycle.setVisibility(View.VISIBLE);
            }
        });
    }

    public void bindView(String[] labels, ArrayList<BarEntry> barEntries) {
        int maxSize = 0;
        if (getContext() != null) {
            totalWith = (int) ViewMaker.pixelsToDp((float) Resources.getSystem().getDisplayMetrics().widthPixels, getContext());
            if (labels != null) {
                for (int i = 0; i < labels.length; i++) {
                    if (labels[i].length() > 13) {
                        labels[i] = labels[i].substring(0, 10) + "...";
                    }
                    if (labels[i].length() > maxSize) {
                        maxSize = labels[i].length();
                    }
                }
            } else {
                return;
            }

        }
        BarDataSet set1;
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

        set1 = new BarDataSet(barEntries, "Data Set");
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(new Theme().getAccentColor(chart.getContext()));
        set1.setColors(colors);
        set1.setDrawValues(true);
        set1.setValueTypeface(ResourcesCompat.getFont(chart.getContext(), R.font.main_font));
        set1.setValueTextColor(new Theme().getTitleTextColor(chart.getContext()));
        set1.setValueTextSize(ViewMaker.dpToPixel(3));
        set1.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {

                String myValue;
                try {
                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);
                    myValue = df.format(value);
                } catch (Exception e) {
                    myValue = String.valueOf((long) Math.floor(value));

                }
                if (HelperCalander.isPersianUnicode)
                    myValue = convertToUnicodeFarsiNumber(myValue);

                return myValue + "%";
            }
        });
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        BarData data = new BarData(dataSets);
        chart.setData(data);
        chart.setFitBars(true);
        chart.setVisibleXRangeMaximum((float) totalWith / (maxSize * 6));
        chart.invalidate();
        chart.getData().notifyDataChanged();

    }

    public void addChatToEnd(String[] labels, ArrayList<BarEntry> barEntries, long sum) {
        /**convert to percent*/
        for (int i = 0; i < barEntries.size(); i++) {
            barEntries.get(i).setY((barEntries.get(i).getY() * 100) / sum);
        }
        this.labels = labels;
        this.barEntries = barEntries;
    }

    public List<PollItem> getData() {
        return this.pollList;
    }

    private void showChart() {
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<BarEntry> barValue = new ArrayList<>();
        boolean userPolledBefore = false;
        long sumOfPoll = 0;
        int i = 0;
        for (PollItem pollItem : getData()) {
            for (PollItemField pollItemField : pollItem.pollItemFields) {
                if (pollItemField.clicked) {
                    userPolledBefore = true;
                }
                if (pollItemField.clickable) {

                    labels.add(pollItemField.label);
                    barValue.add(new BarEntry(i, pollItemField.sum));
                    sumOfPoll += pollItemField.sum;
                    i++;
                }
            }
        }

        String[] labels2 = new String[labels.size()];
        labels2 = labels.toArray(labels2);

        if (userPolledBefore) {
            addChatToEnd(labels2, barValue, sumOfPoll);
        }
        bindView(this.labels, this.barEntries);
    }
}
