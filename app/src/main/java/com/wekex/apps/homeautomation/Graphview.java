package com.wekex.apps.homeautomation;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.wekex.apps.homeautomation.Interfaces.MyListener;
import com.wekex.apps.homeautomation.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class Graphview extends AppCompatActivity implements MyListener {
    String TAG = "graphView";
    LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
    String PowerDno;
    private double graph2LastXValue;
    public static MyListener myListener;
    GraphView graph;
    int increment = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_view);
        myListener = this;

        PowerDno = getIntent().getStringExtra("code");
        //String str = Constants.savetoShared(this).getString(PowerDno + "R", Constants.EMPTY);
        //int no = getNO(str);

        //Log.d(TAG, jsonArray.length()+" onCreate: "+str);
        graph = findViewById(R.id.graph);


        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        //   gridLabel.setHorizontalAxisTitle("Time");
        //   gridLabel.setVerticalAxisTitle("Power");


        graph.getGridLabelRenderer().setNumVerticalLabels(10);
        graph.getGridLabelRenderer().setNumHorizontalLabels(20);


        graph.getViewport().setScrollable(false);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(100);

        graph.getViewport().setYAxisBoundsManual(false);
        graph.getViewport().setMinY(0);

        graph.getViewport().setScrollable(true);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);

      /*  graph.getViewport().setScrollable(true); // enables horizontal scrolling
        graph.getViewport().setScrollableY(true); // enables vertical scrolling
        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling   */

//        try {
//            series = new LineGraphSeries<>();
//            series.resetData(data(PowerDno));
//            graph.addSeries(series);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        //series.setOnDataPointTapListener((series, dataPoint) -> Toast.makeText(Graphview.this, "Series1: On Data Point clicked: " + dataPoint, Toast.LENGTH_SHORT).show());
        //if (series!=null)

    }

    public DataPoint[] data(String str) throws JSONException {
        Log.wtf("GRAPH_DATA", str);
        JSONArray jsonArray = new JSONArray(str);
        DataPoint[] values = new DataPoint[jsonArray.length()];     //creating an object of type DataPoint[] of size 'n'
        for (int i = 0; i < jsonArray.length(); i++) {
            DataPoint v = new DataPoint(i, jsonArray.getDouble(i));
            values[i] = v;
            graph2LastXValue++;
        }
        return values;
    }

    private DataPoint[] generateData(String str, int no) {
        JSONObject jsonObject = Constants.stringToJsonObject(str);
        Iterator<String> iter = jsonObject.keys();

        DataPoint[] values = new DataPoint[no - 1];
        int i = 0;
        while (iter.hasNext()) {
            String key = iter.next();
            double value = 0;
            try {
                value = jsonObject.getDouble(key);
            } catch (JSONException e) {
                // Something went wrong!
            }
            DataPoint v = new DataPoint(i, value);
            values[i] = v;
            graph2LastXValue++;
            i++;
        }
        return values;
    }

    private int getNO(String str) {
        JSONObject jsonObject = Constants.stringToJsonObject(str);
        Iterator<String> iter = jsonObject.keys();
        int i = 0;
        while (iter.hasNext()) {
            i++;
        }
        return i;
    }

//    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            updateUI(intent);
//        }
//    };
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        registerReceiver(broadcastReceiver, new IntentFilter(MqttMessageService.BROADCAST_ACTION));
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        unregisterReceiver(broadcastReceiver);
//    }

    private void updateUI(String data) {
//        findViewById(R.id.progressBar).setVisibility(View.GONE);
//        String Rdata = intent.getStringExtra("powerData");
        JSONObject jsonObject;
        try {
            assert data != null;
            jsonObject = new JSONObject(data);
            if (jsonObject.has("power")) {
                if (Constants.jsonObjectreader(data, "dno").equals(PowerDno.replace("power", ""))) {
                    Log.d(TAG, graph2LastXValue + "updateUI: " + Constants.jsonObjectreader(data, "power"));
                    graph2LastXValue++;
                    series.appendData(new DataPoint(graph2LastXValue, Double.parseDouble(Constants.jsonObjectreader(data, "power"))), false, 100);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void finish(View view) {
        finish();
    }

    @Override
    public void listener(String data) {
        //updateUI(data);
        Log.wtf("GRAPHVIEW_ACTIVITY_LOGS", data + increment++);

        try {
            JSONObject jsonObject = new JSONObject(data);
            String dNum = jsonObject.getString("dno");
            double power = jsonObject.getDouble("power");

            if (PowerDno.equals(dNum)) {
                series.appendData(new DataPoint(increment, power), true, 100);
                graph.addSeries(series);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
