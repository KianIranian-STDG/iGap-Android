package net.iGap.adapter.items.poll.holder;

import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
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
import net.iGap.adapter.items.poll.PollAdapter;
import net.iGap.helper.HelperCalander;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static net.iGap.helper.HelperCalander.convertToUnicodeFarsiNumber;

public class TypeChartViewHolder extends RecyclerView.ViewHolder {
    private HorizontalBarChart chart;

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
        xAxis.setTextColor(Color.parseColor(G.textTitleTheme));
        xAxis.setTypeface(ResourcesCompat.getFont(chart.getContext() , R.font.main_font));
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
        colors.add(Color.parseColor(G.appBarColor));
        int[] a = new int[1];
        a[0] = Color.parseColor(G.appBarColor);
        set1.setColors(colors);
        set1.setDrawValues(true);
        set1.setValueTypeface(ResourcesCompat.getFont(chart.getContext() , R.font.main_font));
        set1.setValueTextColor(Color.parseColor(G.textTitleTheme));
        set1.setValueTextSize(ViewMaker.dpToPixel(4));
        set1.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {

                String myValue;

                //number 22.154456 => show 2 digit of float 22.15 if crashed just show number -> 22
                try {
                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);
                    myValue = df.format(value);
                }catch (Exception e){
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


        //chart.setVisibleXRangeMaximum((float) totalWith / (maxSize * 7));
        chart.invalidate();
        chart.getData().notifyDataChanged();

    }
}
