package com.bank.interest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements
        OnChartValueSelectedListener {
    float principle;
    LineChart chart;
    private TextView tvX, tvY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        setupViews();


        chart = findViewById(R.id.chart1);

        // background color
        chart.setBackgroundColor(Color.WHITE);

        // disable description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // set listeners
        chart.setOnChartValueSelectedListener(this);
        chart.setDrawGridBackground(false);


        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        // chart.setScaleXEnabled(true);
        // chart.setScaleYEnabled(true);

        // force pinch zoom along both axis
        chart.setPinchZoom(true);

    }


    private void setupViews() {
        final EditText mInvestmentET = findViewById(R.id.investment);
        final EditText mInterestRate = findViewById(R.id.interest_rate);
        final EditText mTime = findViewById(R.id.time);
        Button mMakeChart = findViewById(R.id.make_chart);

        mMakeChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String investment = mInvestmentET.getText().toString();
                String interest = mInterestRate.getText().toString();
                String time = mTime.getText().toString();
                principle = Float.parseFloat(investment);
                if (investment.length() == 0 || interest.length() == 0 || time.length() == 0) {
                    Toast.makeText(MainActivity.this, getString(R.string.all_is_required), Toast.LENGTH_SHORT).show();
                    return;
                }


                float singleYearInterest
                        = Float.parseFloat(investment) /* Principle */
                        * Float.parseFloat(interest) /* Interest */
                        * 1; /* Year */


                float highestValueOfInvestment = 0;
                ArrayList<Entry> values = new ArrayList<>();
                for (int i = 0; i < Integer.parseInt(time) + 1; i++) {


                    float val = Float.parseFloat(investment) + singleYearInterest * i;
                    values.add(new Entry(i, val, getResources().getDrawable(R.drawable.star)));
                    highestValueOfInvestment = val;
                }

                YAxis yAxis;
                {   // // Y-Axis Style // //
                    yAxis = chart.getAxisLeft();

                    // disable dual axis (only use LEFT axis)
                    chart.getAxisRight().setEnabled(false);

                    // horizontal grid lines
//            yAxis.enableGridDashedLine(10f, 10f, 0f);

                    // axis range
                    yAxis.setAxisMaximum(highestValueOfInvestment + highestValueOfInvestment / 2);
                    yAxis.setAxisMinimum(-50f);
                }

                LineDataSet set1;

                if (chart.getData() != null &&
                        chart.getData().getDataSetCount() > 0) {
                    set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
                    set1.setValues(values);
                    set1.notifyDataSetChanged();
                    chart.getData().notifyDataChanged();
                    chart.notifyDataSetChanged();
                } else {
                    // create a dataset and give it a type
                    set1 = new LineDataSet(values, "Year");

                    set1.setDrawIcons(false);

                    // draw dashed line
//            set1.enableDashedLine(10f, 5f, 0f);

                    // black lines and points
                    set1.setColor(Color.BLACK);
                    set1.setCircleColor(Color.BLACK);

                    // line thickness and point size
                    set1.setLineWidth(1f);
                    set1.setCircleRadius(3f);

                    // draw points as solid circles
                    set1.setDrawCircleHole(false);

                    // customize legend entry
                    set1.setFormLineWidth(1f);
                    set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                    set1.setFormSize(15.f);

                    // text size of values
                    set1.setValueTextSize(9f);

                    // draw selection line as dashed
//            set1.enableDashedHighlightLine(10f, 5f, 0f);

                    // set the filled area
                    set1.setDrawFilled(true);
                    set1.setFillFormatter(new IFillFormatter() {
                        @Override
                        public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                            return chart.getAxisLeft().getAxisMinimum();
                        }
                    });

                    // set color of filled area
                    if (Utils.getSDKInt() >= 18) {
                        // drawables only supported on api level 18 and above
                        Drawable drawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.fade_red);
                        set1.setFillDrawable(drawable);
                    } else {
                        set1.setFillColor(Color.BLACK);
                    }

                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(set1); // add the data sets

                    // create a data object with the data sets
                    LineData data = new LineData(dataSets);

                    // set data
                    chart.setData(data);
                }
                updateInvestmentsInTextViews(values.get(values.size() - 1));
                chart.invalidate();
                hideKeyboard(MainActivity.this);
            }
        });
    }


    private void updateInvestmentsInTextViews(Entry entry) {
        TextView mAfterYear = findViewById(R.id.after_year);
        TextView mAddedInterests = findViewById(R.id.gained_interests);
        TextView mNewWealth = findViewById(R.id.new_wealth);

        mAfterYear.setText(getString(R.string.after) + " " + Math.round(entry.getX()) + " " + getString(R.string.year));
        mAddedInterests.setText(getString(R.string.added_interest) + " " + (entry.getY() - principle));
        mNewWealth.setText(getString(R.string.wealth) + ": " + entry.getY());


    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {
        updateInvestmentsInTextViews(e);

        Log.i("Entry selected", e.toString());
        Log.i("LOW HIGH", "low: " + chart.getLowestVisibleX() + ", high: " + chart.getHighestVisibleX());
        Log.i("MIN MAX", "xMin: " + chart.getXChartMin() + ", xMax: " + chart.getXChartMax() + ", yMin: " + chart.getYChartMin() + ", yMax: " + chart.getYChartMax());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}


