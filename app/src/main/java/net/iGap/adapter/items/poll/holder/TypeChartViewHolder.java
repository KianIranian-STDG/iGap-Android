package net.iGap.adapter.items.poll.holder;

import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
import net.iGap.adapter.items.poll.PollAdapter;
import net.iGap.helper.HelperCalander;

import java.util.ArrayList;

import static net.iGap.helper.HelperCalander.convertToUnicodeFarsiNumber;

public class TypeChartViewHolder extends RecyclerView.ViewHolder {
    private BarChart chart;

    public TypeChartViewHolder(PollAdapter pollAdapter, @NonNull View itemView) {
        super(itemView);
        chart = itemView.findViewById(R.id.type8_chart0);

        chart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) Math.floor(value));
            }
        });
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60);
        chart.setPinchZoom(true);
        chart.setHighlightPerTapEnabled(false);

        chart.setDrawValueAboveBar(true);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDoubleTapToZoomEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setTextColor(new Theme().getTitleTextColor(chart.getContext()));
        xAxis.setTypeface(G.typeface_IRANSansMobile);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
//        chart.getAxisLeft().setLabelCount(max);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getAxisLeft().setDrawGridLines(false);

        // add a nice and smooth animation
        chart.animateY(1500);

        chart.getLegend().setEnabled(false);

    }

    public void bindView(String[] labels, ArrayList<BarEntry> barEntries) {
        int maxSize = 0;
        int totalWith = (int) ViewMaker.pixelsToDp((float) Resources.getSystem().getDisplayMetrics().widthPixels, itemView.getContext());
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
        colors.add(new Theme().getPrimaryColor(chart.getContext()));
        int[] a = new int[1];
        a[0] = new Theme().getPrimaryColor(chart.getContext());
        set1.setColors(colors);
        set1.setDrawValues(true);
        set1.setValueTextColor(new Theme().getTitleTextColor(chart.getContext()));
        set1.setValueTextSize(ViewMaker.dpToPixel(5));
        set1.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String myValue = String.valueOf((long) Math.floor(value));

                if (HelperCalander.isPersianUnicode)
                    myValue = convertToUnicodeFarsiNumber(myValue);

                return myValue;
            }
        });
        set1.setBarBorderWidth(set1.getBarBorderWidth() == 1.f ? 0.f : 1.f);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        chart.setData(data);
        chart.setFitBars(true);


        chart.setVisibleXRangeMaximum((float) totalWith / (maxSize * 9));
        chart.invalidate();
        chart.getData().notifyDataChanged();

    }
}
