/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.mobileperf.battery;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;


public class FindTheWifiActivity extends ActionBarActivity {
    public static final String LOG_TAG = "FindTheWifiActivity";

    TextView mWifiMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        mWifiMsg = (TextView) findViewById(R.id.wifi_txt);

        Button theButtonThatNetworks = (Button) findViewById(R.id.wifi_download);
        theButtonThatNetworks.setText(R.string.network_button);

        theButtonThatNetworks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadSomething();
            }
        });
    }

    /**
     * This is a placeholder method for where your app might do something interesting! Try not to
     * confuse it with functional code.
     *
     * In this case, it can help to check the strength of the network connection before
     * your app tries connect to the network. For downloads that aren't time-sensitive or
     * that are not user-initiated, you want to avoid weak network connections in favor of WiFi.
     * These data fetches can also benefit from batching, especially with similar actions from other
     * applications.
     *
     * In this sample, we are going to demonstrate how to connect to a server, and then how to
     * schedule such network connections with the JobScheduler API. You'll note that this looks
     * remarkably familiar to the wake lock task. The main difference here is identifying what
     * kind of network connection is important (mainly, WiFi).
     */
    private void downloadSomething() {
        for (int i=0; i<10; i++) {
            mWifiMsg.append("Connection attempt, take " + i + ":\n");
            // Always check that the network is available before trying to connect. You don't want
            // to break things and embarrass yourself.
            if (isNetworkConnected()) {
                new SimpleDownloadTask().execute();
            } else {
                mWifiMsg.setText("No connection on job " + i + "; SAD FACE");
            }
        }
    }

    /**
     * Determines if the device is currently online.
     */
    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     *  Uses AsyncTask to create a task away from the main UI thread. This task creates a
     *  HTTPUrlConnection, and then downloads the contents of the webpage as an InputStream.
     *  The InputStream is then converted to a String, which is displayed in the UI by the
     *  onPostExecute() method.
     */
    private class SimpleDownloadTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                // Only display the first 50 characters of the retrieved web page content.
                int len = 50;

                URL url = new URL("https://www.google.com");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000); // 10 seconds
                conn.setConnectTimeout(15000); // 15 seconds
                conn.setRequestMethod("GET");
                //Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d(LOG_TAG, "The response is: " + response);
                InputStream is = conn.getInputStream();

                // Convert the input stream to a string
                Reader reader = new InputStreamReader(is, "UTF-8");
                char[] buffer = new char[len];
                reader.read(buffer);
                return new String(buffer);

            } catch (IOException e) {
                return "Unable to retrieve web page.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            mWifiMsg.append("\n" + result);
        }
    }
}
