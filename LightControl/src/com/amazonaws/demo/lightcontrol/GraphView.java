package com.amazonaws.demo.lightcontrol;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class GraphView extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_view);
        try {

            GraphView.RetreiveDataFromDynamoDB getStatusShadowTask = new GraphView.RetreiveDataFromDynamoDB();
            getStatusShadowTask.execute();

            Map<String, String> response = getStatusShadowTask.get().getResult();

            LineChart lineChart = (LineChart) findViewById(R.id.chart);

            // Setting labels
            ArrayList<String> labels = new ArrayList<String>();
            response.keySet().forEach(key -> {
                labels.add(key);
            });

            //Setting data for labels
            ArrayList<Entry> entries = new ArrayList<Entry>();
            int i = 0;
            for (String key : response.keySet()) {
                entries.add(new Entry(Long.parseLong(key), i));
                i++;
            }

            LineDataSet dataset = new LineDataSet(entries, "# of Calls");
            LineData data = new LineData(labels, dataset);

            dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
            dataset.setDrawCircleHole(true);
            dataset.setDrawFilled(true);

            lineChart.setData(data);
            lineChart.animateY(5000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private class RetreiveDataFromDynamoDB extends AsyncTask<Void, Void, AsyncTaskResult<Map>> {

        @Override
        protected AsyncTaskResult<Map> doInBackground(Void... voids) {

            // LightMode On Map
            Map<String, String> listOfLightModeOnStatus = new HashMap<>();

            // LightMode Off Map
            Map<String, String> listOfLightModeOffStatus = new HashMap<>();

            // For AWS DynamoDB scan filter
            HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();

            AmazonDynamoDBClient ddb = LightActivity.clientManager.ddb();
            DynamoDBMapper mapper = new DynamoDBMapper(ddb);
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();


            // Defining a date to retrieve a data
            Date previousDate = new GregorianCalendar(2017, Calendar.SEPTEMBER, 16).getTime();


            // Preparing a query for retrieve a data.
            Condition condition = new Condition().withComparisonOperator(ComparisonOperator.GT.toString())
                    .withAttributeValueList(new AttributeValue().withS("1505732402109"));
            scanFilter.put("Timestamp", condition);
            ScanRequest scanRequest = new ScanRequest()
                    .withTableName(Constants.TEST_TABLE_NAME)
                    .withScanFilter(scanFilter);

            // Result from the dynamo db
            ScanResult result = ddb.scan(scanRequest);

            //Parsing result
            result.getItems().forEach(response -> {
                //  System.out.println(response.get("Status").getM().get("lightmode"));
                Map<String, AttributeValue> sensorDataMap = response.get("Status").getM();
                if (sensorDataMap.containsKey("lightmode")) {
                    if (sensorDataMap.get("lightmode").getS().equals("ON")) {
                        System.out.println("Timestamp: " + response.get("Timestamp").getS() + " status: " + sensorDataMap.get("lightmode").getS());
                        listOfLightModeOnStatus.put(response.get("Timestamp").getS(), sensorDataMap.get("lightmode").getS());
                    } else {
                        listOfLightModeOffStatus.put(response.get("Timestamp").getS(), sensorDataMap.get("lightmode").getS());
                        System.out.println("Timestamp: " + response.get("Timestamp").getS() + " status: " + sensorDataMap.get("lightmode").getS());
                    }
                }
            });

            return new AsyncTaskResult<Map>(listOfLightModeOffStatus);
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<Map> result) {

        }
    }

}
