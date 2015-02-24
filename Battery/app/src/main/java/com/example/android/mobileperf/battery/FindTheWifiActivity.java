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

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class FindTheWifiActivity extends ActionBarActivity {
    public static final String LOG_TAG = "FindTheWifiActivity";

    TextView mWifiMsg;
    ComponentName mServiceComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        mWifiMsg = (TextView) findViewById(R.id.wifi_txt);
        mServiceComponent = new ComponentName(this, MyJobService.class);
        Intent startServiceIntent = new Intent(this, MyJobService.class);
        startService(startServiceIntent);

        Button theButtonThatNetworks = (Button) findViewById(R.id.wifi_download);
        theButtonThatNetworks.setText(R.string.network_button);

        theButtonThatNetworks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadSmarter();
            }
        });
    }

    /**
     * This method polls the server via the JobScheduler API. This is the same as in the previous
     * task concerning wake locks, but now we can fine-tune our job requirements so that we are
     * only connecting via WiFi. There are many possible configurations with the JobScheduler API,
     * and you can use them to fine-tune your job requirements. Check the documentation for more
     * information.
     * https://developer.android.com/reference/android/app/job/JobInfo.Builder.html
     */
    private void downloadSmarter() {
        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        // Beginning with 10 here to distinguish this activity's jobs from the
        // FreeTheWakelockActivity's jobs within the JobScheduler API.
        for (int i=10; i<20; i++) {
            JobInfo jobInfo = new JobInfo.Builder(i, mServiceComponent)
                    .setMinimumLatency(5000) // 5 seconds
                    .setOverrideDeadline(60000) // 60 seconds (for brevity in the sample)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED) // for Wifi only
                    .build();

            mWifiMsg.append("Scheduling job " + i + "!\n");
            scheduler.schedule(jobInfo);
        }
    }
}
