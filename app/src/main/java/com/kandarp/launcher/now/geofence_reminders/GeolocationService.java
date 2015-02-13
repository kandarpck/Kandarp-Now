package com.kandarp.launcher.now.geofence_reminders;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;
import java.util.Map;

public class GeolocationService extends Service implements ConnectionCallbacks,
        OnConnectionFailedListener, LocationListener {
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    public static final String ACTION_GEOFENCES_ERROR = "me.hoen.geofence_21.ACTION_GEOFENCES_ERROR";
    public static final String ACTION_GEOFENCES_SUCCESS = "me.hoen.geofence_21.ACTION_GEOFENCES_SUCCESS";
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    private PendingIntent mPendingIntent;

    @Override
    public void onStart(Intent intent, int startId) {
        buildGoogleApiClient();

        mGoogleApiClient.connect();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void registerGeofences() {
        if (GeofenceActivity.geofencesAlreadyRegistered) {
            return;
        }

        Log.d(GeofenceActivity.TAG, "Registering Geofences");

        HashMap<String, SimpleGeofence> geofences = SimpleGeofenceStore
                .getInstance().getSimpleGeofences();

        GeofencingRequest.Builder geofencingRequestBuilder = new GeofencingRequest.Builder();
        for (Map.Entry<String, SimpleGeofence> item : geofences.entrySet()) {
            SimpleGeofence sg = item.getValue();

            geofencingRequestBuilder.addGeofence(sg.toGeofence());
        }

        GeofencingRequest geofencingRequest = geofencingRequestBuilder.build();

        mPendingIntent = requestPendingIntent();

        LocationServices.GeofencingApi.addGeofences(mGoogleApiClient,
                geofencingRequest, mPendingIntent);

        GeofenceActivity.geofencesAlreadyRegistered = true;
    }

    private PendingIntent requestPendingIntent() {

        if (null != mPendingIntent) {

            return mPendingIntent;
        } else {

            Intent intent = new Intent(this, GeofenceReceiver.class);
            return PendingIntent.getService(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

        }
    }

    public void broadcastLocationFound(Location location) {
        Intent intent = new Intent("me.hoen.geofence_21.geolocation.service");
        intent.putExtra("latitude", location.getLatitude());
        intent.putExtra("longitude", location.getLongitude());
        intent.putExtra("done", 1);

        sendBroadcast(intent);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(GeofenceActivity.TAG, "Connected to GoogleApiClient");
        startLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(GeofenceActivity.TAG,
                "new location : " + location.getLatitude() + ", "
                        + location.getLongitude() + ". "
                        + location.getAccuracy());
        broadcastLocationFound(location);

        if (!GeofenceActivity.geofencesAlreadyRegistered) {
            registerGeofences();
        }

        if (location.getAccuracy() <= 50) {
            Log.d(GeofenceActivity.TAG, "Stopping geolocation service");
            stopSelf();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(GeofenceActivity.TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(GeofenceActivity.TAG,
                "Connection failed: ConnectionResult.getErrorCode() = "
                        + result.getErrorCode());
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(GeofenceActivity.TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
