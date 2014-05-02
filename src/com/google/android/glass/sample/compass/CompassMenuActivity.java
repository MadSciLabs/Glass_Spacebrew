/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.google.android.glass.sample.compass;
import com.google.android.glass.sample.compass.util.MathUtils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import at.ac.sbg.icts.spacebrew.client.SpacebrewClient;
import at.ac.sbg.icts.spacebrew.client.SpacebrewClientCallback;
import at.ac.sbg.icts.spacebrew.client.SpacebrewMessage;
import at.ac.sbg.icts.spacebrew.client.publisher.BooleanPublisher;
import at.ac.sbg.icts.spacebrew.client.publisher.RangePublisher;
import at.ac.sbg.icts.spacebrew.client.publisher.StringPublisher;

import java.lang.Runnable;

/**
 * This activity manages the options menu that appears when the user taps on the compass's live
 * card.
 */
public class CompassMenuActivity extends Activity {

	//SpacebrewClient client = new SpacebrewClient(this, "ws://spacebrew.madsci1.havasworldwide.com:9000", "SpacebrewClient", "A simple Java client");

    private final Handler mHandler = new Handler();

    private CompassService.CompassBinder mCompassService;
    private boolean mAttachedToWindow;
    private boolean mOptionsMenuOpen;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof CompassService.CompassBinder) {
                mCompassService = (CompassService.CompassBinder) service;
                openOptionsMenu();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // Do nothing.
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindService(new Intent(this, CompassService.class), mConnection, 0);
    
        /*
       	//System.out.println("testing 1");
    	Log.v("main activity","onCreate");

        client.connect();
        Log.v("sbwrapper","connect");
        
        client.addPublisher("glassX","0");
        Log.v("sbwrapper","add publish");
        */
    }

    /*
    @Override
    public void onOpen()
    {
     
    	client.send("glassX", 15);
    }

    @Override
    public void onError()
    {
        System.out.println("Error occurred.");
    }

    @Override
    public void onClose()
    {
        System.out.println("Connection to server closed.");
    }
 	*/
    
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAttachedToWindow = true;
        openOptionsMenu();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAttachedToWindow = false;
    }

    @Override
    public void openOptionsMenu() {
        if (!mOptionsMenuOpen && mAttachedToWindow && mCompassService != null) {
            super.openOptionsMenu();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.compass, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.read_aloud:
                mCompassService.readHeadingAloud();
                return true;
            case R.id.stop:
                // Stop the service at the end of the message queue for proper options menu
                // animation. This is only needed when starting an Activity or stopping a Service
                // that published a LiveCard.
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        stopService(new Intent(CompassMenuActivity.this, CompassService.class));
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
        mOptionsMenuOpen = false;

        unbindService(mConnection);

        // We must call finish() from this method to ensure that the activity ends either when an
        // item is selected from the menu or when the menu is dismissed by swiping down.
        finish();
    }
}
