package jp.yuruga.qbomb;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static jp.yuruga.qbomb.common.Share.*;
import static jp.yuruga.qbomb.common.Constants.*;

public class GeofenceService extends Service implements LocationListener {

    public static final String ACTION_REFRESH_GEOFENCES = "jp.yuruga.qbomb.action_refresh_geofences";
    public static final String ACTION_NOTIFY_BOMBED = "jp.yuruga.qbomb.action_notify_bombed";
    public static final String ACTION_PUSH_RECEIVED = "jp.yuruga.qbomb.action_push_received";
    private static final int NOTIFICATION_ID = 0;
    //public static final String ACTION_SEND_NOTIFICATION = "";

    // Holds the location client
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private long mLocationRequestInterval;
    private long mLocationRequestFastestInterval;
    private boolean isListeningLocation;
    private GoogleApiClient.ConnectionCallbacks mConnectionCallBacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            log("google play services client connected");
            //requestNewGeofences(null);

            //remove all geofences for debug
            removeAllGeoFences();

            //start monitoring location
            if(!isListeningLocation)
            {
                startListeningLocationUpdates();
            }
        }

        @Override
        public void onConnectionSuspended(int i) {

        }
    };
    private GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

        }
    };



    public GeofenceService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        log("GeofenceServiceOnCreate");

        mLocationRequestInterval = (long)(getResources().getInteger(R.integer.location_update_interval_s)*1000);
        mLocationRequestFastestInterval = (long)(getResources().getInteger(R.integer.location_update_fastest_interval_s)*1000);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                //.addApi(Wearable.API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(mConnectionCallBacks)
                .addOnConnectionFailedListener(mConnectionFailedListener)
                .build();
        mGoogleApiClient.connect();
        isListeningLocation = false;
        startListeningLocationUpdates();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log("Geofence onStartCommand");
        if(intent != null)
        {

            String action = intent.getAction();
            log("Geofence onStartCommand"+action);


            if(ACTION_REFRESH_GEOFENCES.equals(action))
            {
                String fenceId = intent.getStringExtra("id");
                requestNewGeofences(fenceId);
            }else if(ACTION_PUSH_RECEIVED.equals(action))
            {
                String bombId = intent.getStringExtra("bomb_id");
                //requestNewGeofences(bombId);
                //check if location inside fence
                log("push received");
                double lat = intent.getDoubleExtra("lat",0);
                double lon = intent.getDoubleExtra("lon",0);
                float radius = intent.getFloatExtra("radius", 100f);
                //put dummy data
                /*JSONArray fenceData = new JSONArray();
                JSONObject data1 = new JSONObject();
                fenceData.put(createGeofenceJSONOBject(lat, lon, radius, "fffenceid", bombId));
                addGeoFences(fenceData);*/
                Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                double d = getDistance(lat,lon,lastLocation.getLatitude(),lastLocation.getLongitude());
                log("distance is :"+d+" m");
                if(d<radius)
                {
                    Intent i = new Intent(this, GeofenceService.class);
                    i.putExtra("bomb_id", bombId);
                    i.setAction(ACTION_NOTIFY_BOMBED);
                    startService(i);
                }

            }else if(ACTION_NOTIFY_BOMBED.equals(action)) {
                String bombId = intent.getStringExtra("bomb_id");
                notifyBombed(bombId);

            }
        }


        return START_STICKY_COMPATIBILITY;
    }

    private void notifyBombed(String bomb_id)
    {
        //main notification
        NotificationCompat.Builder notifBulder = new NotificationCompat.Builder(this)
                .setContentTitle("Chotto")
                .setContentText("You have a new Enquete.")
                .setSound(Uri.parse("android.resource://jp.mdnht.drawmessenger/raw/yo"))
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                .setSmallIcon(R.drawable.icon);


        // Create an intent for the reply action
        Intent actionIntent = new Intent(this, AnswerActivity.class);
        actionIntent.putExtra("bomb_id", bomb_id);
        //actionIntent.setAction(ACTION_OPEN_WEAR_APP);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, actionIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        notifBulder.setContentIntent(pendingIntent);
        notifBulder.setAutoCancel(true);
        // Create the action
        /*NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.common_signin_btn_icon_disabled_focus_light,"launch wear app", actionPendingIntent)
                        .build();*/

        // Create a WearableExtender to add functionality for wearables
        Notification notif =
                new NotificationCompat.WearableExtender()
                        //.addPage(secondPageNotification)
                        //.addAction(action)
                        .extend(notifBulder)
                        .build();

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(NOTIFICATION_ID, notif);
    }

    private void removeAllGeoFences()
    {
        Intent intent = new Intent(this, AnswerActivity.class);
        //intent.putExtra("bomb_id", bombId);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, pendingIntent)
                .setResultCallback(new ResultCallback<Status>() {
                                       @Override
                                       public void onResult(Status status) {
                                           log("remove fences "+status.getStatus().isSuccess());
                                       }
                                   }
                );
    }

    private void addGeoFences(JSONArray dataArray)
    {

        for (int i = 0; i < dataArray.length(); i++) {
            try
            {
                JSONObject data = dataArray.getJSONObject(i);

                String id = data.getString("id");
                double longitude = data.getDouble("lon");//139.774719;
                double latitude = data.getDouble("lat");//89.00;
                String bombId = data.getString("bomb_id");

                // 半径(メートル)
                float radius = 100;

                Geofence.Builder builder = new Geofence.Builder();
                builder.setRequestId("fence"+id);
                builder.setCircularRegion(latitude, longitude, radius);

                builder.setExpirationDuration(Geofence.NEVER_EXPIRE);
                builder.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER);// | Geofence.GEOFENCE_TRANSITION_EXIT);

                ArrayList<Geofence> geofences = new ArrayList<Geofence>();


                geofences.add(builder.build());


                // PendingIntent の生成
                if(bombId != null)
                {
                    //Intent intent = new Intent(this, AnswerActivity.class);
                    /*intent.putExtra("bomb_id", bombId);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);*/
                    Intent intent = new Intent(this, GeofenceService.class);
                    intent.putExtra("bomb_id", bombId);
                    intent.setAction(ACTION_NOTIFY_BOMBED);
                    PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, geofences, pendingIntent)
                            .setResultCallback(new ResultCallback()
                            {
                                @Override
                                public void onResult(Result result) {
                                    log("addGeofenceResult: "+result.getStatus().isSuccess());
                                }
                            });
                }else
                {
                    Intent intent = new Intent(this, GeofenceService.class);
                    intent.putExtra("id",id);
                    intent.setAction(GeofenceService.ACTION_REFRESH_GEOFENCES);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, geofences, pendingIntent);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void requestNewGeofences(String parentFenceId)
    {
        log("request fence with" + parentFenceId);
        if(parentFenceId == null)
        {
            log("creating dummy geofence");
            //put dummy data
            JSONArray dummyData = new JSONArray();
            JSONObject data1 = new JSONObject();
            dummyData.put(createGeofenceJSONOBject(35.956344, 136.225573, 300, "fffenceid", "bbbombid"));

            addGeoFences(dummyData);
        }else
        {
            //TO DO: request width parent fenceId
        }
    }

    private JSONObject createGeofenceJSONOBject(double lat, double lon, float radius, String id, String bomb_id)
    {
        JSONObject o = new JSONObject();
        try {
            o.put("lat",lat);
            o.put("lon", lon);
            o.put("id", id);
            o.put("bomb_id", bomb_id);
            o.put("radius", radius);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return o;

    }

    public void startListeningLocationUpdates()
    {
        log("%%StartListening%%");
        if(mGoogleApiClient.isConnected())
        {
            mLocationRequest = LocationRequest.create();
            // Use high accuracy
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(mLocationRequestInterval);
            mLocationRequest.setFastestInterval(mLocationRequestFastestInterval);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            isListeningLocation = true;
            log("%%StartListening2222%%");
        }

    }

    public void stopListeningLocationUpdates()
    {
        if(mGoogleApiClient.isConnected())
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
            isListeningLocation = false;
        }
    }


    public double getDistance(double fLat, double fLon, double tLat, double tLon){
        double er = 6378.137f;
        double diffLat = Math.PI / 180 * (tLat - fLat);
        double diffLon = Math.PI / 180 * (tLon - fLon);
        double disLat = er * diffLat;
        double disLon = Math.cos(Math.PI / 180 * fLat) * er * diffLon;
        double dis = Math.sqrt(Math.pow(disLon, 2) + Math.pow(disLat, 2));
        return dis * 1000;
    }


    @Override
    public void onLocationChanged(Location location) {
        log("location:" + location.toString());

    }
}
