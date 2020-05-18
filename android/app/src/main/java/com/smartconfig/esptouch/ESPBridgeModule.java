package com.smartconfig.esptouch;

import android.util.Log;
import android.os.AsyncTask;

import com.facebook.react.bridge.*;

import java.util.List;

public class ESPBridgeModule extends ReactContextBaseJavaModule {
    private static final String TAG = "ESPBridgeModule";

    private final ReactApplicationContext _reactContext;

    private IEsptouchTask mEsptouchTask;

    public ESPBridgeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        _reactContext = reactContext;

    }

    @Override
    public String getName() {
            return "ESPBridge";
    }

    @ReactMethod
    public void stop() {
        if (mEsptouchTask != null) {
            Log.d(TAG, "cancel task");
            mEsptouchTask.interrupt();
        }
    }

    @ReactMethod
    public void start(final ReadableMap options, final Promise promise) {
        String ssid = options.getString("ssid");
        String bssid = options.getString("bssid");
        String pass = options.getString("password");
        int taskCount = options.getInt("taskCount");
        Boolean hidden = false;
        Log.d(TAG, "ssid " + ssid + ":pass " + pass);
        stop();
        new EsptouchAsyncTask(new TaskListener() {
            @Override
            public void onFinished(List<IEsptouchResult> result) {
                // Do Something after the task has finished

                WritableArray ret = Arguments.createArray();

                Boolean resolved = false;
                try{
                    for (IEsptouchResult resultInList : result) {
                        if(!resultInList.isCancelled() &&resultInList.getBssid() != null) {
                            WritableMap map = Arguments.createMap();
                            map.putString("bssid", resultInList.getBssid());
                            map.putString("ipv4", resultInList.getInetAddress().getHostAddress());
                            ret.pushMap(map);
                            resolved = true;
                            if (!resultInList.isSuc())
                                break;

                        }
                    }

                    if(resolved) {
                        Log.d(TAG, "Success run ESPBridge");
                        promise.resolve(ret);
                    } else {
                        Log.d(TAG, "Error run ESPBridge");
                        promise.reject("Timeout");
                    }
                }catch(Exception e){
                    Log.d(TAG, "Error, ESPBridge could not complete!");
                    promise.reject("Timeout", e);
                }
            }
        }).execute(ssid, bssid, pass, Integer.toString(taskCount));
    }


    public interface TaskListener {
        public void onFinished(List<IEsptouchResult> result);
    }

    private class EsptouchAsyncTask extends AsyncTask<String, Void, List<IEsptouchResult>> {
        private final TaskListener taskListener;

        public EsptouchAsyncTask(TaskListener listener) {
            // The listener reference is passed in through the constructor
            this.taskListener = listener;
        }

        private final Object mLock = new Object();

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "Begin task");
        }

        @Override
        protected List<IEsptouchResult> doInBackground(String... params) {
            Log.d(TAG, "doing task");
            int taskResultCount = -1;
            synchronized (mLock) {
                String apSsid = params[0];
                String apBssid =  params[1];
                String apPassword = params[2];
                taskResultCount = Integer.parseInt(params[3]);
                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, getCurrentActivity().getApplicationContext());
            }
            List<IEsptouchResult> resultList = mEsptouchTask.executeForResults(taskResultCount);
            return resultList;
        }

        @Override
        protected void onPostExecute(List<IEsptouchResult> result) {

            IEsptouchResult firstResult = result.get(0);
            // check whether the task is cancelled and no results received
            if (!firstResult.isCancelled()) {
                if(this.taskListener != null) {

                    // And if it is we call the callback function on it.
                    this.taskListener.onFinished(result);
                }
            }
        }
    }
}
