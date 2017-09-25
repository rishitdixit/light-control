/**
 * Copyright 2010-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */

package com.amazonaws.demo.lightcontrol;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iotdata.AWSIotDataClient;
import com.amazonaws.services.iotdata.model.GetThingShadowRequest;
import com.amazonaws.services.iotdata.model.GetThingShadowResult;
import com.amazonaws.services.iotdata.model.UpdateThingShadowRequest;
import com.amazonaws.services.iotdata.model.UpdateThingShadowResult;
import com.google.gson.Gson;

import java.nio.ByteBuffer;

public class LightActivity extends Activity implements View.OnClickListener {

    private static final String LOG_TAG = LightActivity.class.getCanonicalName();

    // --- Constants to modify per your configuration ---

    // Customer specific IoT endpoint
    // AWS Iot CLI describe-endpoint call returns: XXXXXXXXXX.iot.<region>.amazonaws.com
    private static final String CUSTOMER_SPECIFIC_ENDPOINT = "afuclvagu4q44.iot.us-east-2.amazonaws.com";
    // Cognito pool ID. For this app, pool needs to be unauthenticated pool with
    // AWS IoT permissions.
    private static final String COGNITO_POOL_ID = "us-east-2:8f7fe1b0-9ccc-46af-9798-adcf10811788";
    
    // Region of AWS IoT
    private static final Regions MY_REGION = Regions.US_EAST_2;

    CognitoCachingCredentialsProvider credentialsProvider;

    AWSIotDataClient iotDataClient;

    public static AmazonClientManager clientManager = null;

    Button viewId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clientManager = new AmazonClientManager(this);
//        ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.list_view,Android_VersionArray)
//        ListView listView = (ListView) findViewById(R.id.list_item);
//        listView.setAdapter(adapter);

        viewId = (Button)findViewById(R.id.viewId);
        viewId.setOnClickListener(this);



        // Initialize the Amazon Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                COGNITO_POOL_ID, // Identity Pool ID
                MY_REGION // Region
        );

        iotDataClient = new AWSIotDataClient(credentialsProvider);
        String iotDataEndpoint = CUSTOMER_SPECIFIC_ENDPOINT;
        iotDataClient.setEndpoint(iotDataEndpoint);

//
//        NumberPicker np = (NumberPicker) findViewById(R.id.setpoint);
//        np.setMinValue(60);
//        np.setMaxValue(80);
//        np.setWrapSelectorWheel(false);

        getShadows();
    }

    public void lightStatusUpdated(String lightStatusState) {
        Gson gson = new Gson();
        LightStatus ts = gson.fromJson(lightStatusState, LightStatus.class);
        Log.i(LOG_TAG, String.format("light:  %s", ts.state.desired.lightmode));
        Log.i(LOG_TAG, String.format("buzzer:  %s", ts.state.desired.buzzer));
        Log.i(LOG_TAG, String.format("led1:  %s", ts.state.desired.led1));
        Log.i(LOG_TAG, String.format("led2:  %s", ts.state.desired.led2));
//        Log.i(LOG_TAG, String.format("forward:  %s", ts.state.desired.forward));
//        Log.i(LOG_TAG, String.format("intTemp:  %d", ts.state.desired.intTemp));
//        Log.i(LOG_TAG, String.format("extTemp:  %d", ts.state.desired.extTemp));
//        Log.i(LOG_TAG, String.format("curState: %s", ts.state.desired.curState));

       TextView lightText = (TextView) findViewById(R.id.light);
        lightText.setText(ts.state.desired.lightmode);

        TextView buzzerText=(TextView) findViewById((R.id.buzzer));
        buzzerText.setText(ts.state.desired.buzzer);

        TextView led1Text = (TextView) findViewById(R.id.led1);
        led1Text.setText(ts.state.desired.led1);

        TextView led2Text = (TextView) findViewById(R.id.led2);
        led2Text.setText(ts.state.desired.led2);

//        TextView forwardText = (TextView) findViewById(R.id.forward);
//        forwardText.setText(ts.state.desired.forward);

//       // intTempText.setText(ts.state.desired.intTemp.toString());
//        if ("stopped".equals(ts.state.desired.curState)) {
//            intTempText.setTextColor(getResources().getColor(R.color.colorOff));
//        } else if ("heating".equals(ts.state.desired.curState)) {
//            intTempText.setTextColor(getResources().getColor(R.color.colorHeating));
//        } else if ("cooling".equals(ts.state.desired.curState)) {
//            intTempText.setTextColor(getResources().getColor(R.color.colorCooling));
//        }
//
//        TextView extTempText = (TextView) findViewById(R.id.extTemp);
//        extTempText.setText(ts.state.desired.extTemp.toString());
    }

    public void lightControlUpdated(String lightControlState) {
        Gson gson = new Gson();
        LightControl tc = gson.fromJson(lightControlState, LightControl.class);
        Log.i(LOG_TAG, String.format("pot:  %s", tc.state.reported.pot_value));

//        Log.i(LOG_TAG, String.format("setPoint: %d", tc.state.desired.setPoint));
//        Log.i(LOG_TAG, String.format("enabled: %b", tc.state.desired.enabled));
//
//        NumberPicker np = (NumberPicker) findViewById(R.id.setpoint);
//        np.setValue(tc.state.desired.setPoint);

        TextView lightText = (TextView) findViewById(R.id.pot);
        lightText.setText(tc.state.reported.pot_value);


//        ToggleButton tb = (ToggleButton) findViewById(R.id.enableButton);
//        tb.setChecked(tc.state.desired.enabled);


//        ToggleButton tb1 = (ToggleButton) findViewById(R.id.enableButton1);
//        tb1.setChecked(tc.state.desired.enabled);
//
//        ToggleButton tb2 = (ToggleButton) findViewById(R.id.enableButton2);
//        tb2.setChecked(tc.state.desired.enabled);
//
//        ToggleButton tb3 = (ToggleButton) findViewById(R.id.enableButton3);
//        tb3.setChecked(tc.state.desired.enabled);

//        ToggleButton tb4 = (ToggleButton) findViewById(R.id.enableButton4);
//       tb4.setChecked(tc.state.reported.enabled);

//        Button b = (Button) findViewById(R.id.viewId);
//        b.setClickable(tc.state.desired.enabled);


//        Button viewId = (Button) findViewById(R.id.viewId);
//        viewId.setText(viewId);
    }

    public void getShadow(View view) {
        getShadows();
    }

    public void getShadows() {

        GetShadowTask getStatusShadowTask = new GetShadowTask("power_control");
        getStatusShadowTask.execute();

        GetShadowTask getControlShadowTask = new GetShadowTask("analog_pot");
        getControlShadowTask.execute();
//        GetShadowTask getStatusShadowTask = new GetShadowTask("TemperatureStatus");
//        getStatusShadowTask.execute();
//
//        GetShadowTask getControlShadowTask = new GetShadowTask("TemperatureControl");
//        getControlShadowTask.execute();
    }

//    public String getVal(){
//        resString = "Yes";
//
//        return resString;
//    }
    public void enableDisableClicked(View view) {
        ToggleButton tb = (ToggleButton) findViewById(R.id.enableButton);

        Log.i(LOG_TAG, String.format("System %s", tb.isChecked() ? "ON" : "OFF"));
        UpdateShadowTask updateShadowTask = new UpdateShadowTask();
        updateShadowTask.setThingName("power_control");

        //String newState="{\"state\":{\"desired\":{\"lightmode\":\"ON\"}}}";

       String newState = String.format("{\"state\":{\"desired\":{\"lightmode\":\"%s\"}}}",
              tb.isChecked() ? "ON" : "OFF");

//        String newState = String.format("{\"state\":{\"desired\":{\"enabled\":%s}}}",
//                tb.isChecked() ? "true" : "false");
        Log.i(LOG_TAG, newState);
        updateShadowTask.setState(newState);
        updateShadowTask.execute();
    }

    //------------------Buzzer----------------------//
    public void enableDisableBuzzer(View view) {
        ToggleButton tb1 = (ToggleButton) findViewById(R.id.enableButton1);

        Log.i(LOG_TAG, String.format("System %s", tb1.isChecked() ? "ON" : "OFF"));
        UpdateShadowTask updateShadowTask = new UpdateShadowTask();
        updateShadowTask.setThingName("power_control");

        //String newState="{\"state\":{\"desired\":{\"lightmode\":\"ON\"}}}";

        String newState = String.format("{\"state\":{\"desired\":{\"buzzer\":\"%s\"}}}",
                tb1.isChecked() ? "ON" : "OFF");

//        String newState = String.format("{\"state\":{\"desired\":{\"enabled\":%s}}}",
//                tb.isChecked() ? "true" : "false");
        Log.i(LOG_TAG, newState);
        updateShadowTask.setState(newState);
        updateShadowTask.execute();
    }

    //-----------------LED1-----------------------------------//

    public void enableDisableLed1(View view) {
        ToggleButton tb2 = (ToggleButton) findViewById(R.id.enableButton2);

        Log.i(LOG_TAG, String.format("System %s", tb2.isChecked() ? "ON" : "OFF"));
        UpdateShadowTask updateShadowTask = new UpdateShadowTask();
        updateShadowTask.setThingName("power_control");

        //String newState="{\"state\":{\"desired\":{\"lightmode\":\"ON\"}}}";

        String newState = String.format("{\"state\":{\"desired\":{\"led1\":\"%s\"}}}",
                tb2.isChecked() ? "ON" : "OFF");

//        String newState = String.format("{\"state\":{\"desired\":{\"enabled\":%s}}}",
//                tb.isChecked() ? "true" : "false");
        Log.i(LOG_TAG, newState);
        updateShadowTask.setState(newState);
        updateShadowTask.execute();
    }



    //---------------------------LED2---------------------------------//

    public void enableDisableLed2(View view) {
        ToggleButton tb3 = (ToggleButton) findViewById(R.id.enableButton3);

        Log.i(LOG_TAG, String.format("System %s", tb3.isChecked() ? "ON" : "OFF"));
        UpdateShadowTask updateShadowTask = new UpdateShadowTask();
        updateShadowTask.setThingName("power_control");

        //String newState="{\"state\":{\"desired\":{\"lightmode\":\"ON\"}}}";

        String newState = String.format("{\"state\":{\"desired\":{\"led2\":\"%s\"}}}",
                tb3.isChecked() ? "ON" : "OFF");

//        String newState = String.format("{\"state\":{\"desired\":{\"enabled\":%s}}}",
//                tb.isChecked() ? "true" : "false");
        Log.i(LOG_TAG, newState);
        updateShadowTask.setState(newState);
        updateShadowTask.execute();
    }

//    //-----------------forward------------------------------------------------//
//    public void enableDisablePot(View view) {
//        ToggleButton tb = (ToggleButton) findViewById(R.id.enableButton4);
//
//        Log.i(LOG_TAG, String.format("System %s", tb.isChecked()));
//        UpdateShadowTask updateShadowTask = new UpdateShadowTask();
//        updateShadowTask.setThingName("analog_pot");
//
//        //String newState="{\"state\":{\"desired\":{\"lightmode\":\"ON\"}}}";
//
//        String newState = String.format("{\"state\":{\"reported\":{\"pot_value\":\"%s\"}}}");
//
////        String newState = String.format("{\"state\":{\"desired\":{\"enabled\":%s}}}",
////                tb.isChecked() ? "true" : "false");
//        Log.i(LOG_TAG, newState);
//        updateShadowTask.setState(newState);
//        updateShadowTask.execute();
//    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        startActivity(new Intent(LightActivity.this,GraphView.class));
        Button b = (Button) findViewById(R.id.viewId);

        Log.i(LOG_TAG, String.format("System %s", b.isClickable() ? "ON" : "OFF"));
        UpdateShadowTask updateShadowTask = new UpdateShadowTask();
       // UpdateShadowTask.setThingName("power_control");

    }


    public void updateSetpoint(View view) {
//        NumberPicker np = (NumberPicker) findViewById(R.id.setpoint);
//        Integer newSetpoint = np.getValue();
//        Log.i(LOG_TAG, "New setpoint:" + newSetpoint);
        UpdateShadowTask updateShadowTask = new UpdateShadowTask();
        updateShadowTask.setThingName("analog_pot");
//        String newState = String.format("{\"state\":{\"desired\":{\"setPoint\":%d}}}", newSetpoint);
//        Log.i(LOG_TAG, newState);
//        updateShadowTask.setState(newState);
        updateShadowTask.execute();
    }



    private class GetShadowTask extends AsyncTask<Void, Void, AsyncTaskResult<String>> {

        private final String thingName;

        public GetShadowTask(String name) {
            thingName = name;
        }

        @Override
        protected AsyncTaskResult<String> doInBackground(Void... voids) {
            try {
                GetThingShadowRequest getThingShadowRequest = new GetThingShadowRequest()
                        .withThingName(thingName);
                GetThingShadowResult result = iotDataClient.getThingShadow(getThingShadowRequest);
                byte[] bytes = new byte[result.getPayload().remaining()];
                result.getPayload().get(bytes);
                String resultString = new String(bytes);
                return new AsyncTaskResult<String>(resultString);
            } catch (Exception e) {
                Log.e("E", "getShadowTask", e);
                return new AsyncTaskResult<String>(e);
            }
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<String> result) {
            if (result.getError() == null) {
                Log.i(GetShadowTask.class.getCanonicalName(), result.getResult());
                if ("analog_pot".equals(thingName)) {
                    lightControlUpdated(result.getResult());
                } else if ("power_control".equals(thingName)) {
                    lightStatusUpdated(result.getResult());
                }
            } else {
                Log.e(GetShadowTask.class.getCanonicalName(), "getShadowTask", result.getError());
            }
        }
    }

    private class UpdateShadowTask extends AsyncTask<Void, Void, AsyncTaskResult<String>> {

        private String thingName;
        private String updateState;

        public void setThingName(String name) {
            thingName = name;
        }

        public void setState(String state) {
            updateState = state;
        }

        @Override
        protected AsyncTaskResult<String> doInBackground(Void... voids) {
            try {
                UpdateThingShadowRequest request = new UpdateThingShadowRequest();
                request.setThingName(thingName);

                ByteBuffer payloadBuffer = ByteBuffer.wrap(updateState.getBytes());
                request.setPayload(payloadBuffer);

                UpdateThingShadowResult result = iotDataClient.updateThingShadow(request);

                byte[] bytes = new byte[result.getPayload().remaining()];
                result.getPayload().get(bytes);
                String resultString = new String(bytes);
                return new AsyncTaskResult<String>(resultString);
            } catch (Exception e) {
                Log.e(UpdateShadowTask.class.getCanonicalName(), "updateShadowTask", e);
                return new AsyncTaskResult<String>(e);
            }
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<String> result) {
            if (result.getError() == null) {
                Log.i(UpdateShadowTask.class.getCanonicalName(), result.getResult());
            } else {
                Log.e(UpdateShadowTask.class.getCanonicalName(), "Error in Update Shadow",
                        result.getError());
            }

        }


    }
}
