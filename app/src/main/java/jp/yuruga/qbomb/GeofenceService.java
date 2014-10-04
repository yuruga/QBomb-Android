package jp.yuruga.qbomb;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static jp.yuruga.qbomb.common.Share.*;
import static jp.yuruga.qbomb.common.Constants.*;

public class GeofenceService extends Service {

    public static final String ACTION_REFRESH_GEOFENCES = "jp.yuruga.qbomb.action_refresh_geofences";
    public static final String ACTION_NOTIFY_BOMBED = "jp.yuruga.qbomb.action_notify_bombed";

    // Holds the location client
    private GoogleApiClient mGoogleApiClient;
    private GoogleApiClient.ConnectionCallbacks mConnectionCallBacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            log("google play services client connected");
            requestNewGeofences(null);
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
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                //.addApi(Wearable.API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(mConnectionCallBacks)
                .addOnConnectionFailedListener(mConnectionFailedListener)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if(ACTION_REFRESH_GEOFENCES.equals(action))
        {
            String fenceId = intent.getStringExtra("id");
            requestNewGeofences(fenceId);
        }else if(ACTION_NOTIFY_BOMBED.equals(action))
        {
            String bombId = intent.getStringExtra("id");
            requestNewGeofences(bombId);
        }

        return super.onStartCommand(intent, flags, startId);
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
                builder.setRequestId("fence_"+id);
                builder.setCircularRegion(latitude, longitude, radius);
                builder.setExpirationDuration(Geofence.NEVER_EXPIRE);
                builder.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER);// | Geofence.GEOFENCE_TRANSITION_EXIT);

                ArrayList<Geofence> geofences = new ArrayList<Geofence>();
                geofences.add(builder.build());


                // PendingIntent の生成
                if(bombId != null)
                {
                    Intent intent = new Intent(this, AnswerActivity.class);
                    intent.putExtra("bomb_id", bombId);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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


}
