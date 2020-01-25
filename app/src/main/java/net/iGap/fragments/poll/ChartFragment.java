package net.iGap.fragments.poll;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import net.iGap.Theme;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.adapter.items.poll.PollItem;
import net.iGap.adapter.items.poll.PollItemField;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
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
    private HelperToolbar mHelperToolbar;
    private SwipeRefreshLayout pullToRefresh;
    private int pollId;//get this value from getArguments()

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_poll_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            pollId = getArguments().getInt("pollId");
        } else {
            if (getActivity() != null)
                getActivity().onBackPressed();
        }

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                });

        ViewGroup viewGroup = view.findViewById(R.id.chart_toolbar);
        viewGroup.addView(mHelperToolbar.getView());
        pullToRefresh = view.findViewById(R.id.sweep);
        chart = view.findViewById(R.id.type8_chart0);
        chart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) Math.floor(value));
            }
        });

        chart.setMaxVisibleValueCount(100);
        chart.setPinchZoom(true);
        chart.setHighlightPerTapEnabled(false);
        chart.setDrawValueAboveBar(true);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setTouchEnabled(true);
        chart.setDoubleTapToZoomEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setTextColor(new Theme().getTitleTextColor(chart.getContext()));
        xAxis.setTypeface(ResourcesCompat.getFont(chart.getContext(), R.font.main_font));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(ViewMaker.dpToPixel(4));
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        chart.getAxisLeft().setAxisMinimum(0);
//        chart.getAxisLeft().setLabelCount(max);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getAxisLeft().setDrawGridLines(false);

        // add a nice and smooth animation
        chart.animateY(1000);

        chart.getLegend().setEnabled(false);

        tryToUpdateOrFetchRecycleViewData(0);
    }

    private void tryToUpdateOrFetchRecycleViewData(int count) {
        setRefreshing(true);
        boolean isSend = updateOrFetchRecycleViewData();

        if (!isSend) {
            if (count < 3) {
                G.handler.postDelayed(() -> tryToUpdateOrFetchRecycleViewData(count + 1), 1000);
            } else {
                setRefreshing(false);
            }
        }
    }

    private boolean updateOrFetchRecycleViewData() {
        return new RequestClientGetPoll().getPoll(pollId, new OnPollList() {
            @Override
            public void onPollListReady(ArrayList<PollItem> pollArrayList, String title) {
                G.handler.post(() -> {
                    pollList = pollArrayList;
                    notifyChangeData();
                    mHelperToolbar.setDefaultTitle(title);
                    setRefreshing(false);
                });
            }

            @Override
            public void onError(int major, int minor) {
                G.handler.post(() -> setRefreshing(false));
            }
        });
    }

    public void bindView(String[] labels, ArrayList<BarEntry> barEntries) {
        int maxSize = 0;
        int totalWith = (int) ViewMaker.pixelsToDp((float) Resources.getSystem().getDisplayMetrics().widthPixels, getContext());
        for (int i = 0; i < labels.length; i++) {
            if (labels[i].length() > 13) {
                labels[i] = labels[i].substring(0, 10) + "...";
            }
            if (labels[i].length() > maxSize) {
                maxSize = labels[i].length();
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

                //number 22.154456 => show 2 digit of float 22.15 if crashed just show number -> 22
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
        //set1.setBarBorderWidth(set1.getBarBorderWidth() == 1.f ? 0.f : 1.f);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        chart.setData(data);
        chart.setFitBars(true);


        chart.setVisibleXRangeMaximum((float) totalWith / (maxSize * 6));
        chart.invalidate();
        chart.getData().notifyDataChanged();

    }

    private void setRefreshing(boolean value) {
        pullToRefresh.setRefreshing(value);
/*        if (value) {
            emptyRecycle.setVisibility(View.GONE);
        } else {
            if (pollAdapter.getItemCount() == 0) {
                emptyRecycle.setVisibility(View.VISIBLE);
            } else {
                emptyRecycle.setVisibility(View.GONE);
            }
        }*/
    }

    public void notifyChangeData() {
        /*this.notifyChangeData();*/
        showChart();
    }

    public void addChatToEnd(String[] labels, ArrayList<BarEntry> barEntries, long sum) {
        //convert to percent
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
