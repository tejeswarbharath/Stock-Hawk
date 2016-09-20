package com.tejeswar.android.stockhawk.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.tejeswar.android.stockhawk.R;
import com.tejeswar.android.stockhawk.rest.StockInfo;
import com.tejeswar.android.stockhawk.service.HistoryIntentService;
import com.tejeswar.android.stockhawk.service.InfoIntentService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.tejeswar.android.stockhawk.R.id.graph;

/**
 * Created by tejeswar on 9/20/2016.
 */

public class HistoryActivity extends AppCompatActivity {

    ArrayList<String> results;
    GraphView graphView;

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(br, new IntentFilter("historyResults"));
        LocalBroadcastManager.getInstance(this).registerReceiver(br_info, new IntentFilter("infoResults"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(br);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(br_info);
    }

    protected BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = intent.getStringArrayListExtra("list");
            drawGraph();
        }
    };

    protected BroadcastReceiver br_info = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            StockInfo object = (StockInfo) intent.getSerializableExtra("object");
            SimpleDateFormat f1 = new SimpleDateFormat("MM/dd/yyyy");//Hardcoded to match result's format.
            DateFormat f2 = DateFormat.getDateInstance(DateFormat.MEDIUM);
            TextView infoView = (TextView) findViewById(R.id.infoView);
            try {
                infoView.setText(getString(R.string.name) + object.Name + "\n"+
                        getString(R.string.symbol) + object.symbol + "\n"+
                        getString(R.string.exchange) + object.StockExchange + "\n" +
                        getString(R.string.currency) + object.Currency + "\n"+
                        getString(R.string.ask) + object.Ask + getString(R.string.dollar) + "\n"+
                        getString(R.string.bid) + object.Bid + getString(R.string.dollar) + "\n"+
                        getString(R.string.days_range) + object.DaysRange + getString(R.string.dollar) + "\n"+
                        getString(R.string.change) + object.Change + getString(R.string.dollar) + "\n"+
                        getString(R.string.last_trade_date) + f2.format(f1.parse(object.LastTradeDate)) + "\n" +
                        getString(R.string.year_high) + object.YearHigh + getString(R.string.dollar) + "\n"+
                        getString(R.string.year_low) + object.YearLow + getString(R.string.dollar) + "\n"+
                        getString(R.string.market_capitalization) + object.MarketCapitalization + getString(R.string.dollar) + "\n");
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        graphView = (GraphView) findViewById(graph);
        Intent mServiceIntent  = new Intent(getApplicationContext(), HistoryIntentService.class);
        Bundle bund;
        bund = getIntent().getExtras();
        mServiceIntent.putExtras(bund);
        Intent mInfoServiceIntent = new Intent(getApplicationContext(), InfoIntentService.class);
        mInfoServiceIntent.putExtras(bund);
        startService(mInfoServiceIntent);
        startService(mServiceIntent);
    }

    public void drawGraph(){
        Float[] close  = new Float[results.size()/3];
        Date [] dates = new Date[results.size()/3];
        DataPoint[] datapoints = new DataPoint[results.size()/3];
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//Hardcoded to match api result's format
        Date date;
        for(int i=0; i<results.size();i+=3){
            close[i/3] = Float.parseFloat(results.get(i+1));
            try {
                date = dateFormat.parse(results.get(i));
                dates[i/3] = date;
            }
            catch (java.text.ParseException e){}
        }
        for(int i=0;i<dates.length;i++){
            datapoints[i] = new DataPoint(i, close[close.length-1-i]);
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(datapoints);
        graphView.setTitle(getString(R.string.graph_label));
        graphView.setTitleColor(getResources().getColor(R.color.white));
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graphView);
        staticLabelsFormatter.setHorizontalLabels(new String[] {getString(R.string.days30), getString(R.string.days15), getString(R.string.yesterday)});
        graphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        graphView.addSeries(series);
    }
}
